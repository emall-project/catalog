package ps.emall.catalog.favorite;

import ps.emall.catalog.product.light.ProductLightDto;
import ps.emall.catalog.product.light.ProductLightMapper;

public class FavoriteMapper {

    public static FavoriteDto toDto(Favorite entity) {
        if (entity == null) {
            return null;
        }
        ProductLightDto product = null;
        if (entity.getProduct() != null) {
            product = ProductLightMapper.toDtoLight(entity.getProduct());
        }
        return FavoriteDto.builder()
                .id(entity.getId())
                .productId(entity.getProduct() != null ? entity.getProduct().getId() : null)
                .user(entity.getUser())
                .addedAt(entity.getAddedAt())
                .product(product)
                .build();

    }
}