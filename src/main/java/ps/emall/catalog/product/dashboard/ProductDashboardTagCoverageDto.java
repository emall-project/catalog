package ps.emall.catalog.product.dashboard;

public record ProductDashboardTagCoverageDto(
        long productsWithTags,
        long productsWithoutTags
) {
}
