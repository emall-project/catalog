package ps.emall.catalog.product.product_variant;

import lombok.extern.slf4j.Slf4j;
import ps.emall.catalog.product.Product;
import ps.emall.catalog.product.product_media.ProductMediumMapper;
import ps.emall.catalog.product.product_variant.variant_attribute.VariantAttributeMapper;

@Slf4j
public class ProductVariantMapper {
    public static ProductVariantDto toDto(ProductVariant entity) {
        return ProductVariantDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .basePrice(entity.getBasePrice())
                .isDefault(entity.getIsDefault())
                .media(entity.getMedia() != null ?
                        entity.getMedia().stream().map(ProductMediumMapper::toDto).toList()
                        : null
                )
                .attributes(entity.getVariantAttributes() != null ?
                        entity.getVariantAttributes().stream().map(VariantAttributeMapper::toDto).toList()
                        : null
                )
                .build();
    }

    public static ProductVariant toEntity(ProductVariantDto dto, Product product) {
        return ProductVariant.builder()
                .id(dto.getId())
                .name(dto.getName())
                .basePrice(dto.getBasePrice())
                .isDefault(dto.isDefault())
                .product(product)
                .build();
    }
}
