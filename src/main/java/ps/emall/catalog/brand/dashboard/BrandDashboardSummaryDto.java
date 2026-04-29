package ps.emall.catalog.brand.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandDashboardSummaryDto {
    private long totalBrands;
    private long activeBrands;
    private long inactiveBrands;
    private DashboardAudienceDistributionDto brandAudienceDistribution;
    private DashboardAudienceDistributionDto productAudienceDistribution;
    private List<ProductBrandDistributionDto> productDistributionByBrand;
}
