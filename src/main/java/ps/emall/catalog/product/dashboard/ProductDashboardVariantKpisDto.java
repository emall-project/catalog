package ps.emall.catalog.product.dashboard;

import java.math.BigDecimal;

public record ProductDashboardVariantKpisDto(
        long totalVariants,
        BigDecimal averageVariantsPerProduct,
        long productsWithSingleVariant,
        long productsWithMultipleVariants
) {
}
