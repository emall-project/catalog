package ps.emall.catalog.product.review.rating;

import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ps.emall.catalog.common.response.EMallsResponseEntity;
import ps.emall.catalog.common.validation.OnCreate;
import ps.emall.catalog.common.validation.OnUpdate;

import java.util.List;

@RestController
@RequestMapping("/products/{productId}/reviews")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ProductReviewService reviewService;

    @GetMapping
    public EMallsResponseEntity<List<ProductReviewDto>> getAll(
            @PathVariable Long productId) {
        return EMallsResponseEntity.ok(reviewService.getByProductId(productId));
    }

    @GetMapping("/user/{userId}")
    public EMallsResponseEntity<ProductReviewDto> getByUser(
            @PathVariable Long productId,
            @PathVariable Long userId) {
        return EMallsResponseEntity.ok(reviewService.getByProductIdAndUserId(productId, userId));
    }

    @PostMapping
    public EMallsResponseEntity<ProductReviewDto> create(
            @PathVariable Long productId,
            @RequestBody @Validated({Default.class, OnCreate.class}) ProductReviewDto dto) {
        dto.setProductId(productId);
        return EMallsResponseEntity.created(reviewService.create(productId, dto));
    }

    @PutMapping("/user/{userId}")
    public EMallsResponseEntity<ProductReviewDto> update(
            @PathVariable Long productId,
            @PathVariable Long userId,
            @RequestBody @Validated({Default.class, OnUpdate.class}) ProductReviewDto dto) {
        return EMallsResponseEntity.ok(reviewService.update(productId, userId, dto));
    }

    @DeleteMapping("/user/{userId}")
    public EMallsResponseEntity<Void> delete(
            @PathVariable Long productId,
            @PathVariable Long userId) {
        reviewService.delete(productId, userId);
        return EMallsResponseEntity.noContent(null);
    }
}