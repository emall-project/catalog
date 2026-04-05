package ps.emall.catalog.product.review.moderation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ps.emall.catalog.product.review.comment.CommentStatus;
import ps.emall.catalog.product.review.comment.ProductComment;
import ps.emall.catalog.product.review.comment.ProductCommentRepository;
import ps.emall.catalog.product.review.moderation.openai.OpenAiModerationClient;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModerationServiceImpl implements ModerationService {

    private final ProductCommentRepository commentRepository;
    private final CommentModerationLogRepository moderationLogRepository;
    private final OpenAiModerationClient openAiClient;
    private final ModerationQueue moderationQueue;

    @Value("${moderation.openai.rate-limit-retries:2}")
    private int rateLimitRetries;

    @Value("${moderation.openai.rate-limit-retry-delay-ms:3000}")
    private long rateLimitRetryDelayMs;

    @Override
    public void enqueue(ProductComment comment) {
        moderationQueue.enqueue(comment.getCommentId());
        log.info("Comment {} added to moderation queue. Queue size: {}",
                comment.getCommentId(), moderationQueue.size());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void moderateSync(ProductComment comment) {
        Long commentId = comment.getCommentId();
        log.info("Moderation started: commentId={}", commentId);

        ModerationResult result = callWithRetry(comment.getContent(), commentId);

        moderationLogRepository.save(buildLog(comment, result));

        commentRepository.findById(commentId).ifPresent(c -> {
            c.setLastModerationAttemptAt(LocalDateTime.now());

            if (result.isApiError()) {
                c.setModerationRetryCount(c.getModerationRetryCount() + 1);
                log.warn("Comment {} — OpenAI error (attempt {}). Staying PENDING. reason={}",
                        commentId, c.getModerationRetryCount(), result.getReason());

            } else if (result.getDecision() == ModerationDecision.REJECTED) {
                c.setStatus(CommentStatus.REJECTED);
                c.setRejectionReason(toHumanReason(result.getReason()));
                log.info("Comment {} REJECTED. category={}, confidence={}",
                        commentId, result.getReason(), result.getConfidence());

            } else {
                c.setStatus(CommentStatus.APPROVED);
                c.setRejectionReason(null);
                log.info("Comment {} APPROVED. confidence={}", commentId, result.getConfidence());
            }

            commentRepository.save(c);
        });
    }

    private ModerationResult callWithRetry(String content, Long commentId) {
        ModerationResult result = null;
        long delayMs = rateLimitRetryDelayMs;

        for (int attempt = 1; attempt <= rateLimitRetries; attempt++) {
            result = openAiClient.moderate(content);

            if (!result.isApiError()) {
                return result;
            }

            if (attempt < rateLimitRetries) {
                log.warn("Comment {} — OpenAI attempt {}/{} failed. Waiting {}ms. reason={}",
                        commentId, attempt, rateLimitRetries, delayMs, result.getReason());
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return result;
                }
                delayMs = delayMs * 2;
            }
        }

        log.warn("Comment {} — all {} OpenAI retries exhausted. Leaving PENDING for scheduler.",
                commentId, rateLimitRetries);
        return result;
    }

    private String toHumanReason(String reason) {
        if (reason == null)
            return "المحتوى يخالف إرشادات المجتمع | Content violates community guidelines";

        return switch (reason) {
            case "harassment",
                 "تحرش", "تحرش أو لغة مسيئة"
                    -> "تحرش أو لغة مسيئة | Harassment or offensive language";

            case "harassment/threatening",
                 "تهديد"
                    -> "لغة تهديدية | Threatening language";

            case "hate",
                 "خطاب الكراهية", "كراهية"
                    -> "خطاب الكراهية | Hate speech";

            case "hate/threatening",
                 "خطاب كراهية تهديدي"
                    -> "خطاب كراهية تهديدي | Threatening hate speech";

            case "violence",
                 "عنف"
                    -> "محتوى عنيف | Violent content";

            case "violence/graphic",
                 "عنف صريح"
                    -> "محتوى عنيف صريح | Graphic violent content";

            case "sexual",
                 "محتوى جنسي"
                    -> "محتوى جنسي | Sexual content";

            case "sexual/minors",
                 "محتوى غير لائق"
                    -> "محتوى غير لائق | Inappropriate content";

            case "self-harm",
                 "إيذاء النفس"
                    -> "محتوى ضار | Harmful content";

            case "illicit",
                 "محتوى غير قانوني"
                    -> "محتوى غير مشروع | Illicit content";

            default
                    -> "المحتوى يخالف إرشادات المجتمع | Content violates community guidelines";
        };
    }

    private CommentModerationLog buildLog(ProductComment comment, ModerationResult result) {
        return CommentModerationLog.builder()
                .comment(comment)
                .provider(result.getProvider())
                .decision(result.getDecision())
                .reason(result.getReason())
                .confidence(result.getConfidence())
                .rawResponse(result.getRawResponse())
                .createdAt(LocalDateTime.now())
                .build();
    }
}