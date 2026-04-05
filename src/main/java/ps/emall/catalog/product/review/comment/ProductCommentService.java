package ps.emall.catalog.product.review.comment;

import java.util.List;
import java.util.Map;

public interface ProductCommentService {

    // User: read

    List<ProductCommentDto> getApprovedByProductId(Long productId);

    ProductCommentDto getByProductIdAndUserId(Long productId, Long userId);

    List<ProductCommentDto> getMyComments(Long userId);

    // User: write

    ProductCommentDto create(Long productId, ProductCommentDto dto);

    ProductCommentDto update(Long productId, Long userId, ProductCommentDto dto);

    void delete(Long productId, Long userId);

    ProductCommentDto report(Long productId, Long commentId, Long reporterUserId);

    // Admin: read

    List<ProductCommentDto> getAllByProductId(Long productId);

    List<ProductCommentDto> getByStatus(CommentStatus status);

    Map<CommentStatus, Long> getModerationStats();

    // Admin: actions

    ProductCommentDto adminApprove(Long commentId);

    ProductCommentDto adminReject(Long commentId);

    ProductCommentDto adminFlag(Long commentId);

    ProductCommentDto adminRemoderate(Long commentId);
}