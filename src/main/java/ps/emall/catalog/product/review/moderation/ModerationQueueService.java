package ps.emall.catalog.product.review.moderation;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ps.emall.catalog.product.review.comment.ProductCommentRepository;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModerationQueueService {

    private final ModerationService moderationService;
    private final ProductCommentRepository commentRepository;

    @Value("${moderation.queue.delay-between-requests-ms:1000}")
    private long delayBetweenRequestsMs;

    @Value("${moderation.queue.capacity:10000}")
    private int capacity;

    private BlockingQueue<Long> queue;

    private Thread workerThread;
    private volatile boolean running = false;

    @PostConstruct
    public void start() {
        queue = new LinkedBlockingQueue<>(capacity);
        running = true;
        workerThread = new Thread(this::runWorker, "moderation-queue-worker");
        workerThread.setDaemon(true);
        workerThread.start();
        log.info("ModerationQueueService started. delay={}ms, capacity={}",
                delayBetweenRequestsMs, capacity);
    }

    @PreDestroy
    public void stop() {
        running = false;
        if (workerThread != null) {
            workerThread.interrupt();
        }
        log.info("ModerationQueueService stopped. Remaining in queue: {}", queue.size());
    }

    public boolean enqueue(Long commentId) {
        boolean added = queue.offer(commentId);
        if (added) {
            log.debug("Comment {} added to moderation queue. Queue size: {}", commentId, queue.size());
        } else {
            log.warn("Moderation queue is full (capacity={}). Comment {} will be retried by scheduler.",
                    capacity, commentId);
        }
        return added;
    }

    public int queueSize() {
        return queue.size();
    }

    // Worker thread

    private void runWorker() {
        log.info("Moderation queue worker started");

        while (running) {
            try {
                // Wait up to 5 seconds for the next item — allows clean shutdown
                Long commentId = queue.poll(5, TimeUnit.SECONDS);

                if (commentId == null) {
                    // Queue was empty — loop back and wait again
                    continue;
                }

                log.debug("Processing comment {} from moderation queue. Remaining: {}",
                        commentId, queue.size());

                // Load the comment and run moderation synchronously
                commentRepository.findById(commentId).ifPresentOrElse(
                        comment -> moderationService.moderateSync(comment),
                        () -> log.warn("Comment {} not found when dequeued — may have been deleted", commentId)
                );

                // Wait between requests to respect rate limits
                // This is the key mechanism: 1 second gap = max 60 req/min
                if (running && delayBetweenRequestsMs > 0) {
                    Thread.sleep(delayBetweenRequestsMs);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.info("Moderation queue worker interrupted — shutting down");
                break;
            } catch (Exception e) {
                log.error("Unexpected error in moderation queue worker: {}", e.getMessage(), e);
            }
        }

        log.info("Moderation queue worker stopped");
    }
}