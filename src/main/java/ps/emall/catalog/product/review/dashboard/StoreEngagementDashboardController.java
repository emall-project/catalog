package ps.emall.catalog.product.review.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ps.emall.catalog.common.response.EMallsResponseEntity;

@RestController
@RequestMapping("/stores/{storeId}/engagement")
@RequiredArgsConstructor
public class StoreEngagementDashboardController {

    private final StoreEngagementDashboardService dashboardService;

    @GetMapping("/reviews/summary")
    @PreAuthorize("@auth.isAdmin() or @auth.isAdminOrShopOwnerOf(#storeId)")
    public EMallsResponseEntity<StoreReviewSummaryDto> getReviewSummary(@PathVariable Long storeId) {
        return EMallsResponseEntity.ok(dashboardService.getReviewSummary(storeId));
    }

    @GetMapping("/comments/summary")
    @PreAuthorize("@auth.isAdmin() or @auth.isAdminOrShopOwnerOf(#storeId)")
    public EMallsResponseEntity<StoreCommentSummaryDto> getCommentSummary(@PathVariable Long storeId) {
        return EMallsResponseEntity.ok(dashboardService.getCommentSummary(storeId));
    }
}
