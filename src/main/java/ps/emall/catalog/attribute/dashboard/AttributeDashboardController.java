package ps.emall.catalog.attribute.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ps.emall.catalog.common.response.EMallsResponseEntity;

@RestController
@RequestMapping("/attributes/dashboard")
@PreAuthorize("@auth.isAdmin()")
@RequiredArgsConstructor
public class AttributeDashboardController {

    private final AttributeDashboardService attributeDashboardService;

    @GetMapping("/summary")
    public EMallsResponseEntity<AttributeDashboardSummaryDto> getSummary() {
        return EMallsResponseEntity.ok(attributeDashboardService.getSummary());
    }
}
