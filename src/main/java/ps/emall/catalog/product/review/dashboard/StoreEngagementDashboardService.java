package ps.emall.catalog.product.review.dashboard;

public interface StoreEngagementDashboardService {
    StoreReviewSummaryDto getReviewSummary(Long storeId);
    StoreCommentSummaryDto getCommentSummary(Long storeId);
}
