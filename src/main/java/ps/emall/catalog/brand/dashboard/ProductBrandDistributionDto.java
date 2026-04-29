package ps.emall.catalog.brand.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductBrandDistributionDto {
    private Long brandId;
    private String brandName;
    private Long totalProducts;
}
