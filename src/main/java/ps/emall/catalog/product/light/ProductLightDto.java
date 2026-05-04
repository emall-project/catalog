package ps.emall.catalog.product.light;

import lombok.Builder;
import lombok.Data;
import ps.emall.catalog.client.media_manager.FileLightDto;

import java.math.BigDecimal;

@Data
@Builder
public class ProductLightDto {
    private Long id;
    private String name;
    private String slug;
    private String shortDescription;
    private Long defaultVariantId;
    private BigDecimal basePrice;
    private Boolean hasDiscount;
    private BigDecimal discountedPrice;
    private FileLightDto medium;
    private Long categoryId;
    private Boolean isActive;
    private Long variantsCount;
}
