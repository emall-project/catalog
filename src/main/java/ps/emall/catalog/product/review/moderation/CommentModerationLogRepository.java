package ps.emall.catalog.product.review.moderation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentModerationLogRepository extends JpaRepository<CommentModerationLog, Long> {

    List<CommentModerationLog> findByComment_CommentIdOrderByCreatedAtDesc(Long commentId);

    boolean existsByComment_CommentIdAndProvider(Long commentId, ModerationProvider provider);
}