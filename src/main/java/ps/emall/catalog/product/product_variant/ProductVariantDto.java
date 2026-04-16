package ps.emall.catalog.product.product_variant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.*;
import ps.emall.catalog.common.validation.OnCreate;
import ps.emall.catalog.common.validation.OnUpdate;
import ps.emall.catalog.product.product_media.ProductMediumDto;
import ps.emall.catalog.product.product_variant.variant_attribute.VariantAttributeDto;

import java.math.BigDecimal;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductVariantDto {

    @Null(groups = OnCreate.class, message = "product.variant.id.null")
    @NotNull(groups = OnUpdate.class, message = "product.variant.id.notnull")
    private Long id;

    @NotBlank(message = "product.variant.name.notblank")
    private String name;

    @NotNull(message = "product.variant.basePrice.notblank")
    @Positive(message = "product.variant.basePrice.positive")
    private BigDecimal basePrice;

    @NotNull(message = "product.variant.isDefault.notnull")
    private Boolean isDefault;

    @Valid
    private List<VariantAttributeDto> attributes;

    @NotNull(message = "product.variant.media.not.null")
    @Valid
    private List<ProductMediumDto> media;

    private Boolean hasDiscount;
    private BigDecimal discountedPrice;  // final price after discount for this variant
    private String discountType;
    private BigDecimal discountValue;
    private Long offerId;

}
