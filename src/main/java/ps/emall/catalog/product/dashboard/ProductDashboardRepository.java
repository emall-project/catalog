package ps.emall.catalog.product.dashboard;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductDashboardRepository {
    ProductDashboardKpisDto getKpis(Long storeId);

    ProductDashboardVariantKpisDto getVariantKpis(Long storeId);

    ProductDashboardTagCoverageDto getTagCoverage(Long storeId);

    ProductDashboardPriceStatsDto getPriceStats(Long storeId);

    List<NamedDistributionRowDto> getCategoryDistribution(Long storeId);

    List<NamedDistributionRowDto> getBrandDistribution(Long storeId);

    List<EnumDistributionRowDto> getAudienceDistribution(Long storeId);

    List<EnumDistributionRowDto> getAgeDistribution(Long storeId);

    List<ProductCreatedByMonthDto> getProductsCreatedByMonth(Long storeId, LocalDateTime fromInclusive);
}
