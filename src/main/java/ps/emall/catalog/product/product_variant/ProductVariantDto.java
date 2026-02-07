package ps.emall.catalog.product.product_variant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import ps.emall.catalog.attribute.AttributeDto;
import ps.emall.catalog.product.product_image.ProductImageDto;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductVariantDto {
    private Long id;

    @NotBlank(message = "product.variant.name.notblank")
    private String name;

    @NotNull(message = "product.variant.basePrice.notblank")
    @Positive(message = "product.variant.basePrice.positive")
    private Double basePrice;

    @NotNull(message = "product.variant.isDefault.notnull")
    private boolean isDefault;

    @Valid
    private List<AttributeDto> attributes;

    @Valid
    private List<ProductImageDto> images;


}
