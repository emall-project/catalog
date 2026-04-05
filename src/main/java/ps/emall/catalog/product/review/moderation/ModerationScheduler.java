package ps.emall.catalog.product.review.moderation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ps.emall.catalog.product.review.comment.CommentStatus;
import ps.emall.catalog.product.review.comment.ProductComment;
import ps.emall.catalog.product.review.comment.ProductCommentRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Two scheduled jobs:
 *
 * Job 1 — RETRY (every 10 min):
 *   Finds PENDING comments eligible for retry and adds them back to the queue.
 *   The ModerationWorker will process them in order, with proper rate limiting.
 *
 * Job 2 — FLAG (every 10 min, 1 min offset):
 *   Finds PENDING comments that exhausted all retries → sets to FLAGGED.
 *   Admin reviews them manually.
 *
 * Recovery after restart:
 *   If the server restarts, the in-memory queue is cleared.
 *   Job 1 will re-enqueue any PENDING comments within 10 minutes automatically.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ModerationScheduler {

    private final ProductCommentRepository commentRepository;
    private final ModerationQueue moderationQueue;

    @Value("${moderation.scheduler.max-retries:3}")
    private int maxRetries;

    @Value("${moderation.scheduler.retry-interval-minutes:10}")
    private int retryIntervalMinutes;

    @Scheduled(fixedDelayString = "${moderation.scheduler.retry-interval-ms:600000}")
    public void retryPendingComments() {
        LocalDateTime retryThreshold = LocalDateTime.now().minusMinutes(retryIntervalMinutes);

        List<ProductComment> toRetry = commentRepository
                .findCommentsEligibleForRetry(maxRetries, retryThreshold);

        if (toRetry.isEmpty()) {
            log.debug("ModerationScheduler: no comments eligible for retry");
            return;
        }

        log.info("ModerationScheduler: re-enqueuing {} comment(s) for retry", toRetry.size());

        // Add back to queue — the worker processes them with proper rate limiting
        toRetry.forEach(comment -> {
            moderationQueue.enqueue(comment.getCommentId());
            log.info("Re-enqueued commentId={}, retryCount={}",
                    comment.getCommentId(), comment.getModerationRetryCount());
        });
    }

    @Scheduled(
            fixedDelayString = "${moderation.scheduler.retry-interval-ms:600000}",
            initialDelayString = "60000"
    )
    @Transactional
    public void flagExhaustedComments() {
        List<ProductComment> exhausted = commentRepository
                .findExhaustedPendingComments(maxRetries);

        if (exhausted.isEmpty()) {
            log.debug("ModerationScheduler: no exhausted comments to flag");
            return;
        }

        exhausted.forEach(comment -> {
            comment.setStatus(CommentStatus.FLAGGED);
            log.warn("Comment {} exhausted {} retries — FLAGGED for admin. productId={}, userId={}",
                    comment.getCommentId(), maxRetries,
                    comment.getProduct().getId(), comment.getUserId());
        });

        commentRepository.saveAll(exhausted);
        log.info("ModerationScheduler: {} comment(s) moved to FLAGGED", exhausted.size());
    }
}