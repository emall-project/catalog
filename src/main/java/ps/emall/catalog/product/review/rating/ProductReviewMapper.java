package ps.emall.catalog.product.review.rating;

import ps.emall.catalog.product.Product;

import java.util.Optional;

public class ProductReviewMapper {

    private ProductReviewMapper() {}

    public static ProductReviewDto toDto(ProductReview entity) {
        return Optional.ofNullable(entity)
                .map(e -> ProductReviewDto.builder()
                        .reviewId(e.getReviewId())
                        .productId(e.getProduct() != null ? e.getProduct().getId() : null)
                        .userId(e.getUserId())
                        .rating(e.getRating())
                        .build())
                .orElse(null);
    }

    public static ProductReview toEntity(ProductReviewDto dto, Product product) {
        return Optional.ofNullable(dto)
                .map(d -> ProductReview.builder()
                        .product(product)
                        .userId(d.getUserId())
                        .rating(d.getRating())
                        .build())
                .orElse(null);
    }
}