package ps.emall.catalog.product.review.moderation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@Slf4j
public class ModerationQueue {

    private final BlockingQueue<Long> queue = new LinkedBlockingQueue<>();

    public void enqueue(Long commentId) {
        queue.offer(commentId);
        log.debug("Comment {} added to moderation queue. Queue size: {}", commentId, queue.size());
    }

    public Long take() throws InterruptedException {
        return queue.take();
    }

    public int size() {
        return queue.size();
    }
}