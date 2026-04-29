package ps.emall.catalog.product.dashboard;

import java.math.BigDecimal;

public record ProductDashboardPriceStatsDto(
        BigDecimal minPrice,
        BigDecimal maxPrice,
        BigDecimal avgPrice
) {
}
