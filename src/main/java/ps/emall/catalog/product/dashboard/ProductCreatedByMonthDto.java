package ps.emall.catalog.product.dashboard;

public record ProductCreatedByMonthDto(
        String month,
        long totalProducts,
        long activeProducts
) {
}
