package ps.emall.catalog.product.review.moderation;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ps.emall.catalog.product.review.comment.ProductComment;
import ps.emall.catalog.product.review.comment.ProductCommentRepository;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Single-threaded worker that consumes the ModerationQueue.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ModerationWorker {

    private final ModerationQueue queue;
    private final ModerationService moderationService;
    private final ProductCommentRepository commentRepository;

    @Value("${moderation.worker.transaction-commit-delay-ms:300}")
    private long transactionCommitDelayMs;

    @Value("${moderation.worker.delay-between-calls-ms:1200}")
    private long delayBetweenCallsMs;

    private ExecutorService workerThread;
    private volatile boolean running = true;

    @PostConstruct
    public void start() {
        workerThread = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "moderation-worker");
            t.setDaemon(true);
            return t;
        });
        workerThread.submit(this::workerLoop);
        log.info("ModerationWorker started. commitDelay={}ms, callDelay={}ms",
                transactionCommitDelayMs, delayBetweenCallsMs);
    }

    @PreDestroy
    public void stop() {
        running = false;
        workerThread.shutdownNow();
        try {
            workerThread.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("ModerationWorker stopped.");
    }

    private void workerLoop() {
        log.info("ModerationWorker loop started");

        while (running) {
            try {
                Long commentId = queue.take();

                // Wait for the calling transaction to commit before reading from DB
                if (transactionCommitDelayMs > 0) {
                    Thread.sleep(transactionCommitDelayMs);
                }

                log.info("ModerationWorker processing commentId={}. Queue remaining: {}",
                        commentId, queue.size());

                Optional<ProductComment> optional = commentRepository.findById(commentId);

                if (optional.isEmpty()) {
                    log.warn("ModerationWorker: commentId={} no longer exists, skipping", commentId);
                } else {
                    moderationService.moderateSync(optional.get());
                }

                if (running && delayBetweenCallsMs > 0) {
                    Thread.sleep(delayBetweenCallsMs);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.info("ModerationWorker interrupted, shutting down");
                break;
            } catch (Exception e) {
                log.error("ModerationWorker unexpected error: {}", e.getMessage(), e);
            }
        }

        log.info("ModerationWorker loop ended");
    }
}