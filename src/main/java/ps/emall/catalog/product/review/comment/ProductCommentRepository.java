package ps.emall.catalog.product.review.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCommentRepository extends JpaRepository<ProductComment, Long> {

    List<ProductComment> findByProduct_IdAndStatusOrderByCreatedAtDesc(
            Long productId, CommentStatus status);

    Optional<ProductComment> findByProduct_IdAndUserId(Long productId, Long userId);

    boolean existsByProduct_IdAndUserId(Long productId, Long userId);

    List<ProductComment> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<ProductComment> findByProduct_IdOrderByCreatedAtDesc(Long productId);

    List<ProductComment> findByStatusOrderByCreatedAtAsc(CommentStatus status);

    long countByStatus(CommentStatus status);

    long countByProduct_StoreId(Long storeId);

    long countByProduct_StoreIdAndStatus(Long storeId, CommentStatus status);

    List<ProductComment> findTop5ByProduct_StoreIdOrderByCreatedAtDesc(Long storeId);

    /**
     * PENDING comments that have not yet exhausted retries
     * and whose last attempt was at least retryIntervalMinutes ago.
     */
    @Query("""
            SELECT c FROM ProductComment c
            WHERE c.status = 'PENDING_MODERATION'
            AND c.moderationRetryCount < :maxRetries
            AND (c.lastModerationAttemptAt IS NULL
                 OR c.lastModerationAttemptAt <= :retryThreshold)
            """)
    List<ProductComment> findCommentsEligibleForRetry(
            @Param("maxRetries") int maxRetries,
            @Param("retryThreshold") LocalDateTime retryThreshold);

    /**
     * PENDING comments that have exhausted all AI retry attempts → get FLAGGED.
     */
    @Query("""
            SELECT c FROM ProductComment c
            WHERE c.status = 'PENDING_MODERATION'
            AND c.moderationRetryCount >= :maxRetries
            """)
    List<ProductComment> findExhaustedPendingComments(@Param("maxRetries") int maxRetries);
}
