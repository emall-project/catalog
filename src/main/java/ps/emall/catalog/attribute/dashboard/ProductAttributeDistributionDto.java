package ps.emall.catalog.attribute.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeDistributionDto {
    private Long attributeId;
    private String attributeName;
    private String attributeSlug;
    private Long totalProducts;
    private List<ProductAttributeOptionDistributionDto> options;

    public ProductAttributeDistributionDto(
            Long attributeId,
            String attributeName,
            String attributeSlug,
            Long totalProducts) {
        this.attributeId = attributeId;
        this.attributeName = attributeName;
        this.attributeSlug = attributeSlug;
        this.totalProducts = totalProducts;
        this.options = List.of();
    }
}
