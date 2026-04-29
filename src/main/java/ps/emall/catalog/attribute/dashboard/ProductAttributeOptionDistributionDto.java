package ps.emall.catalog.attribute.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeOptionDistributionDto {
    private Long optionId;
    private String optionValue;
    private Long totalProducts;
}
