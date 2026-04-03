package ps.emall.catalog.product.light;

import ps.emall.catalog.client.media_manager.FileLightDto;
import ps.emall.catalog.product.Product;
import ps.emall.catalog.product.product_variant.ProductVariant;

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
                .build();
    }
}
