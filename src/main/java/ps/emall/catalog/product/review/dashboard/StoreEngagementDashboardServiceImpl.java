package ps.emall.catalog.product.review.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ps.emall.catalog.product.review.comment.CommentStatus;
import ps.emall.catalog.product.review.comment.ProductCommentMapper;
import ps.emall.catalog.product.review.comment.ProductCommentRepository;
import ps.emall.catalog.product.review.rating.ProductReviewMapper;
import ps.emall.catalog.product.review.rating.ProductReviewRepository;

@Service
@RequiredArgsConstructor
public class StoreEngagementDashboardServiceImpl implements StoreEngagementDashboardService {

    private final ProductReviewRepository reviewRepository;
    private final ProductCommentRepository commentRepository;

    @Override
    public StoreReviewSummaryDto getReviewSummary(Long storeId) {
        double averageRating = java.util.Optional
                .ofNullable(reviewRepository.findAverageRatingByStoreId(storeId))
                .orElse(0.0);

        return StoreReviewSummaryDto.builder()
                .storeId(storeId)
                .totalReviews(reviewRepository.countByProduct_StoreId(storeId))
                .averageRating(averageRating)
                .reviewedProducts(reviewRepository.countReviewedProductsByStoreId(storeId))
                .recentReviews(
                        reviewRepository.findTop5ByProduct_StoreIdOrderByCreatedAtDesc(storeId)
                                .stream()
                                .map(ProductReviewMapper::toDto)
                                .toList()
                )
                .build();
    }

    @Override
    public StoreCommentSummaryDto getCommentSummary(Long storeId) {
        return StoreCommentSummaryDto.builder()
                .storeId(storeId)
                .totalComments(commentRepository.countByProduct_StoreId(storeId))
                .approvedComments(commentRepository.countByProduct_StoreIdAndStatus(storeId, CommentStatus.APPROVED))
                .pendingComments(commentRepository.countByProduct_StoreIdAndStatus(storeId, CommentStatus.PENDING_MODERATION))
                .reportedComments(commentRepository.countByProduct_StoreIdAndStatus(storeId, CommentStatus.REPORTED))
                .flaggedComments(commentRepository.countByProduct_StoreIdAndStatus(storeId, CommentStatus.FLAGGED))
                .rejectedComments(commentRepository.countByProduct_StoreIdAndStatus(storeId, CommentStatus.REJECTED))
                .recentComments(
                        commentRepository.findTop5ByProduct_StoreIdOrderByCreatedAtDesc(storeId)
                                .stream()
                                .map(ProductCommentMapper::toDtoWithProductInfo)
                                .toList()
                )
                .build();
    }
}
