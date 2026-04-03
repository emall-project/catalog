package ps.emall.catalog.product.summary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSummary {

    Long totalProducts;
    AudienceDistribution audienceDistribution;
    AgeDistribution ageDisTribution;
    List<CategoryDistribution> categories;
    List<BrandDistribution> brands;
    PriceRange priceRange;
    List<AttributeSummary> attributeSummary;
//    List<TagDistribution> tags;
//    PopularityMetrics popularityMetrics;
}
