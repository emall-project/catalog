package ps.emall.catalog.category.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ps.emall.catalog.common.response.EMallsResponseEntity;

@RestController
@RequestMapping("/categories/dashboard")
@PreAuthorize("@auth.isAdmin()")
@RequiredArgsConstructor
public class DashBoardController {

    private final CategoryDashboardService categoryDashboardService;

    @GetMapping("/summary")
    public EMallsResponseEntity<CategoryDashboardSummaryDto> getSummary() {
        return EMallsResponseEntity.ok(categoryDashboardService.getSummary());
    }
}
