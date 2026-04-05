package ps.emall.catalog.product.review.rating;

import java.util.List;

public interface ProductReviewService {

    List<ProductReviewDto> getByProductId(Long productId);
    ProductReviewDto getByProductIdAndUserId(Long productId, Long userId);
    ProductReviewDto create(Long productId, ProductReviewDto dto);
    ProductReviewDto update(Long productId, Long userId, ProductReviewDto dto);
    void delete(Long productId, Long userId);
}