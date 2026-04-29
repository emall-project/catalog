package ps.emall.catalog.category.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryDistributionDto {
    private Long categoryId;
    private String categoryName;
    private Long totalProducts;
}
