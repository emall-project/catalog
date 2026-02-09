package ps.emall.catalog.product.product_variant;

import lombok.extern.slf4j.Slf4j;
import ps.emall.catalog.attribute.AttributeMapper;
import ps.emall.catalog.product.product_image.ProductImageMapper;
import ps.emall.catalog.product.product_variant.variant_attribute.VariantAttributeMapper;

@Slf4j
public class ProductVariantMapper {
    public static ProductVariantDto toDto(ProductVariant entity) {
        log.info("Creating dto for entity={}", entity);
        log.info("Maping entity id {}", entity.getId());
        log.info("Mapping entity name {}", entity.getName());
        log.info("Mapping entity basePrice {}", entity.getBasePrice());
        log.info("Mapping entity isDefault {}", entity.getIsDefault());
        entity.getImages().stream().forEach(image -> {log.info("Mapping image {}", image.getImageFileKey());});
        entity.getVariantAttributes().stream().forEach(attribute -> {log.info("Mapping attirbutes {}", attribute.getId());});
        return ProductVariantDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .basePrice(entity.getBasePrice())
                .isDefault(entity.getIsDefault())
                .images(entity.getImages() != null ?
                        entity.getImages().stream().map(ProductImageMapper::toDto).toList()
                        : null
                )
                .attributes(entity.getVariantAttributes() != null ?
                        entity.getVariantAttributes().stream().map(VariantAttributeMapper::toDto).toList()
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
//                .attributes(dto.getAttributes() != null ?
//                        dto.getAttributes().stream().map(AttributeMapper::toEntity).toList()
//                        : null
//                )
                .build();
    }
}
