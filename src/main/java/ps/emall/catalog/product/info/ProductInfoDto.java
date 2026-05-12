package ps.emall.catalog.product.info;

import lombok.*;
import ps.emall.catalog.client.media_manager.FileLightDto;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductInfoDto {

    private Long productId;
    private String name;
    private String slug;
    private String shortDescription;
    private String categoryName;
    private String brandName;
    private Boolean isActive;

    private Long storeId;
    private Long mallId;

    private FileLightDto medium;
    private List<VariantPriceInfoDto> variants;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class VariantPriceInfoDto {
        private Long variantId;
        private String variantName;
        private BigDecimal basePrice;
        private Boolean isDefault;
    }
}
