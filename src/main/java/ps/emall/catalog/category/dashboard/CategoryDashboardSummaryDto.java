package ps.emall.catalog.category.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDashboardSummaryDto {
    private long totalCategories;
    private long activeCategories;
    private long inactiveCategories;
    private DashboardAudienceDistributionDto categoryAudienceDistribution;
    private DashboardAudienceDistributionDto productAudienceDistribution;
    private List<ProductCategoryDistributionDto> productDistributionByCategory;
}
