package ps.emall.catalog.product.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ps.emall.catalog.common.response.EMallsResponseEntity;



@RestController
@RequestMapping("stores/{storeId}/products/dashboard")
@PreAuthorize("@auth.isAdminOrShopOwnerOf(#storeId)")
@RequiredArgsConstructor
public class ProductDashboardController {
    private final ProductDashboardService productDashboardService;

    @GetMapping("/summary")
    public EMallsResponseEntity<ProductDashboardSummaryDto> getDashboardSummary(@PathVariable Long storeId) {
        ProductDashboardSummaryDto dashboardSummary = productDashboardService.getSummary(storeId);
        return EMallsResponseEntity.ok(dashboardSummary);
    }
}
