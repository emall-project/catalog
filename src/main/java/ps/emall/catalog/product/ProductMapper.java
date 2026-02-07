package ps.emall.catalog.product;

import ps.emall.catalog.product.product_variant.ProductVariantMapper;
import ps.emall.catalog.tag.TagMapper;

public class ProductMapper {
    public static ProductDto toDto(Product entity) {
        return ProductDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .targetedAudience(entity.getTargetedAudience())
                .ageGroup(entity.getAgeGroup())
                .isActive(entity.getIsActive())
                .shortDescription(entity.getShortDescription())
                .description(entity.getDescription())
                .categoryId(entity.getCategory().getId())
                .brandId(entity.getBrand().getId())
                .mallId(entity.getMallId())
                .storeId(entity.getStoreId())
                .tags(entity.getTags() != null ?
                        entity.getTags().stream().map(TagMapper::toDto).toList()
                        : null
                )
                .variants(entity.getVariants() != null ?
                        entity.getVariants().stream().map(ProductVariantMapper::toDto).toList()
                        : null
                )
                .build();
    }

    public static Product toEntity(ProductDto dto) {
        return Product.builder()
                .id(dto.getId())
                .name(dto.getName())
                .slug(dto.getSlug())
                .targetedAudience(dto.getTargetedAudience())
                .ageGroup(dto.getAgeGroup())
                .isActive(dto.getIsActive())
                .shortDescription(dto.getShortDescription())
                .description(dto.getDescription())
                .mallId(dto.getMallId())
                .storeId(dto.getStoreId())
                .tags(dto.getTags() != null ?
                        dto.getTags().stream().map(TagMapper::toEntity).toList()
                        : null
                )
                .variants(dto.getVariants() != null ?
                        dto.getVariants().stream().map(ProductVariantMapper::toEntity).toList()
                        : null
                )
                .build();
    }
}
