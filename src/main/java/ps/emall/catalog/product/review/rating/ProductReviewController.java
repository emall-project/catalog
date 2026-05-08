package ps.emall.catalog.product.review.rating;

import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ps.emall.catalog.common.exception.EMallsException;
import ps.emall.catalog.common.message.MessageKey;
import ps.emall.catalog.common.response.EMallsResponseEntity;
import ps.emall.catalog.common.validation.OnCreate;
import ps.emall.catalog.common.validation.OnUpdate;
import ps.emall.catalog.security.SecurityContextUtil;

import java.util.List;

@RestController
@RequestMapping("/products/{productId}/reviews")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ProductReviewService reviewService;

    // PUBLIC

    @GetMapping
    public EMallsResponseEntity<List<ProductReviewDto>> getAll(
            @PathVariable Long productId) {
        return EMallsResponseEntity.ok(reviewService.getByProductId(productId));
    }

    @GetMapping("/me")
    @PreAuthorize("@auth.isCustomer()")
    public EMallsResponseEntity<ProductReviewDto> getMyReview(
            @PathVariable Long productId) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        try {
            return EMallsResponseEntity.ok(reviewService.getByProductIdAndUserId(productId, userId));
        } catch (EMallsException e) {
            if (MessageKey.REVIEW_NOT_FOUND.getKey().equals(e.getMessage())) {
                return EMallsResponseEntity.ok(null);
            }
            throw e;
        }
    }

    // CUSTOMER: WRITE

    @PostMapping
    @PreAuthorize("@auth.isCustomer()")
    public EMallsResponseEntity<ProductReviewDto> create(
            @PathVariable Long productId,
            @RequestBody @Validated({Default.class, OnCreate.class}) ProductReviewDto dto) {
        dto.setProductId(productId);
        return EMallsResponseEntity.created(reviewService.create(productId, dto));
    }

    @PutMapping
    @PreAuthorize("@auth.isCustomer()")
    public EMallsResponseEntity<ProductReviewDto> update(
            @PathVariable Long productId,
            @RequestBody @Validated({Default.class, OnUpdate.class}) ProductReviewDto dto) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        return EMallsResponseEntity.ok(reviewService.update(productId, userId, dto));
    }

    @DeleteMapping
    @PreAuthorize("@auth.isCustomer()")
    public EMallsResponseEntity<Void> delete(@PathVariable Long productId) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        reviewService.delete(productId, userId);
        return EMallsResponseEntity.noContent(null);
    }
}
