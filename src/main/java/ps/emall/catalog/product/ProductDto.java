package ps.emall.catalog.product;

import lombok.*;
import ps.emall.catalog.common.audience.AgeGroup;
import ps.emall.catalog.common.audience.TargetedAudience;
import ps.emall.catalog.product.product_variant.ProductVariantDto;
import ps.emall.catalog.tag.Tag;
import ps.emall.catalog.tag.TagDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {
    private Long id;

    private String name;

    private String slug;

    private TargetedAudience targetedAudience;

    private AgeGroup ageGroup;

    private Boolean isActive;

    private String shortDescription;

    private String description;

    private Long categoryId;

    private Long brandId;

    private Long mallId;

    private Long storeId;

    private List<TagDto> tags;//create if not found

    private List<ProductVariantDto> variants;//creat
}
