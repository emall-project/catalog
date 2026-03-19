package ps.emall.catalog.product.product_variant.variant_attribute;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VariantAttributeDto {

    @NotNull(message = "variant.attribute.attributeId.notnull")
    private Long attributeId;

    @NotNull(message = "variant.attribute.optionId.notnull")
    private Long optionId;
}
