package ps.emall.catalog.product.light;

import ps.emall.catalog.client.media_manager.FileLightDto;
import ps.emall.catalog.product.Product;
import ps.emall.catalog.product.product_variant.ProductVariant;

import java.util.Map;
import java.util.UUID;

public class ProductLightMapper {
    public static ProductLightDto toDtoLight(Product product) {
        FileLightDto medium= new FileLightDto();
        ProductVariant defaultVariant = product.getDefaultVariant();
        medium.setId(defaultVariant.getMedia().getFirst().getMediumId());
        return ProductLightDto.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .shortDescription(product.getShortDescription())
                .basePrice(defaultVariant.getBasePrice())
                .defaultVariantId(defaultVariant.getId())
                .medium(medium)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .brandName(product.getBrand() != null ? product.getBrand().getName() : null)
                .isActive(product.getIsActive())
                .variantsCount(product.getVariants() != null ? (long) product.getVariants().size() : 0L)
                .build();
    }


    public static ProductLightDto toProductLightDto(
            Long productId,
            Map<Long, ProductLightRow> productLightRowMap,
            Map<UUID, FileLightDto> mediaMap
    ) {
        ProductLightRow row = productLightRowMap.get(productId);

        if (row == null) {
            return ProductLightDto.builder()
                    .id(productId)
                    .build();
        }

        ProductLightDto dto = ProductLightDto.builder()
                .id(row.getProductId())
                .defaultVariantId(row.getDefaultVariantId())
                .basePrice(row.getBasePrice())
                .name(row.getProductName())
                .slug(row.getProductSlug())
                .categoryName(row.getCategoryName())
                .brandName(row.getBrandName())
                .isActive(row.getIsActive())
                .variantsCount(row.getVariantsCount())
                .build();

        if (row.getMediumId() != null) {
            FileLightDto media = mediaMap.get(row.getMediumId());

            if (media != null) {
                dto.setMedium(media);
            } else {
                FileLightDto mediumRef = new FileLightDto();
                mediumRef.setId(row.getMediumId());
                dto.setMedium(mediumRef);
            }
        }

        return dto;
    }

}
