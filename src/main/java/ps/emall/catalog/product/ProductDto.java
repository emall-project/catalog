package ps.emall.catalog.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ps.emall.catalog.common.audience.AgeGroup;
import ps.emall.catalog.common.audience.TargetedAudience;
import ps.emall.catalog.common.base.EMallsBaseDto;
import ps.emall.catalog.common.validation.OnCreate;
import ps.emall.catalog.common.validation.OnUpdate;
import ps.emall.catalog.product.product_variant.ProductVariantDto;
import ps.emall.catalog.tag.Tag;
import ps.emall.catalog.tag.TagDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ProductDto extends EMallsBaseDto {

    @Null(groups = OnCreate.class, message = "product.id.null")
    @NotNull(groups = OnUpdate.class, message = "product.id.notnull")
    private Long id;

    @NotBlank(message = "product.name.notblank")
    @Size(min = 3, max = 50, message = "product.name.size")
    private String name;

    @NotBlank(message = "product.slug.notblank")
    @Pattern(
            regexp = "^[^\\s]+$",
            message = "product.slug.white.spaces"
    )
    @Pattern(
            regexp = "^(?:[a-z0-9-]|[\\p{IsArabic}&&\\p{L}])+$",
            message = "product.slug.lowercase"
    )
    @Pattern(
            regexp = "^(?:[a-z]|[\\p{IsArabic}&&\\p{L}]).*(?:[a-z]|[\\p{IsArabic}&&\\p{L}])$",
            message = "product.slug.start.end.letter"
    )
    private String slug;

    @NotNull(message = "product.targetedAudience.notnull")
    private TargetedAudience targetedAudience;

    @NotNull(message = "product.ageGroup.notnull")
    private AgeGroup ageGroup;

    @NotNull(message = "product.isActive.notnull")
    private Boolean isActive;

    @NotBlank(message = "product.shortDescription.notblank")
    @Size(min = 3, max = 100, message = "product.shortDescription.size")
    private String shortDescription;

    @NotBlank(message = "product.description.notblank")
    @Size(min = 3, max = 2500, message = "product.description.size")
    private String description;

    @NotNull(message = "product.isActive.notnull")
    private Long categoryId;

    @NotNull(message = "product.isActive.notnull")
    private Long brandId;

    @Null(message = "product.mallId.null")
    private Long mallId;

    @Null(message = "product.storeId.null")
    private Long storeId;

    @Valid
    private List<TagDto> tags;//create if not found

    @Valid
    @NotNull(groups = OnCreate.class, message = "product.variants.notnull")
    @Null(groups = OnUpdate.class, message = "product.variants.null")
    private List<ProductVariantDto> variants;//creat
}
