package ps.emall.catalog.product.dashboard;

public record NamedDistributionRowDto(
        Long id,
        String name,
        long totalProducts,
        long activeProducts
) {
}
