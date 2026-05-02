package ps.emall.catalog.product.review.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ps.emall.catalog.product.review.rating.ProductReviewDto;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreReviewSummaryDto {
    private Long storeId;
    private long totalReviews;
    private double averageRating;
    private long reviewedProducts;
    private List<ProductReviewDto> recentReviews;
}
