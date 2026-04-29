package ps.emall.catalog.brand.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ps.emall.catalog.common.response.EMallsResponseEntity;

@RestController
@RequestMapping("/brands/dashboard")
@PreAuthorize("@auth.isAdmin()")
@RequiredArgsConstructor
public class BrandDashboardController {

    private final BrandDashboardService brandDashboardService;

    @GetMapping("/summary")
    public EMallsResponseEntity<BrandDashboardSummaryDto> getSummary() {
        return EMallsResponseEntity.ok(brandDashboardService.getSummary());
    }
}
