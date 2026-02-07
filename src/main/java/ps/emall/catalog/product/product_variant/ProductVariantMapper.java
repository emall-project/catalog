package ps.emall.catalog.product.product_variant;

import ps.emall.catalog.attribute.AttributeMapper;
import ps.emall.catalog.product.product_image.ProductImageMapper;

public class ProductVariantMapper {
    public static ProductVariantDto toDto(ProductVariant entity) {
        return ProductVariantDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .basePrice(entity.getBasePrice())
                .isDefault(entity.getIsDefault())
                .images(entity.getImages() != null ?
                        entity.getImages().stream().map(ProductImageMapper::toDto).toList()
                        : null
                )
                .attributes(entity.getAttributes() != null ?
                        entity.getAttributes().stream().map(AttributeMapper::toDto).toList()
                        : null
                )
                .build();
    }

    public static ProductVariant toEntity(ProductVariantDto dto) {
        return ProductVariant.builder()
                .id(dto.getId())
                .name(dto.getName())
                .basePrice(dto.getBasePrice())
                .isDefault(dto.isDefault())
                .images(dto.getImages() != null ?
                        dto.getImages().stream().map(ProductImageMapper::toEntity).toList()
                        : null
                )
                .attributes(dto.getAttributes() != null ?
                        dto.getAttributes().stream().map(AttributeMapper::toEntity).toList()
                        : null
                )
                .build();
    }
}
