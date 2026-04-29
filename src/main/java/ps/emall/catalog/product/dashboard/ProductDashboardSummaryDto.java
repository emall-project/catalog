package ps.emall.catalog.product.dashboard;

import java.util.List;

public record ProductDashboardSummaryDto(
        ProductDashboardKpisDto kpis,
        ProductDashboardVariantKpisDto variantKpis,
        ProductDashboardTagCoverageDto tagCoverage,
        ProductDashboardPriceStatsDto priceStats,
        List<NamedDistributionRowDto> categoryDistribution,
        List<NamedDistributionRowDto> brandDistribution,
        List<EnumDistributionRowDto> audienceDistribution,
        List<EnumDistributionRowDto> ageDistribution,
        List<ProductCreatedByMonthDto> productsCreatedByMonth
) {
}
