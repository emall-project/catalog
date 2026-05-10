package ps.emall.catalog.product.info;

import ps.emall.catalog.client.media_manager.FileLightDto;
import ps.emall.catalog.product.Product;

import java.util.List;
import java.util.stream.Collectors;

public class ProductInfoMapper {

    private ProductInfoMapper() {}

    public static ProductInfoDto toInfoDto(Product entity) {
        return toInfoDto(entity, null);
    }

    public static ProductInfoDto toInfoDto(Product entity, FileLightDto medium) {
        if (entity == null) return null;

        List<ProductInfoDto.VariantPriceInfoDto> variants = entity.getVariants() == null
                ? List.of()
                : entity.getVariants().stream()
                .map(v -> ProductInfoDto.VariantPriceInfoDto.builder()
                        .variantId(v.getId())
                        .variantName(v.getName())
                        .basePrice(v.getBasePrice())
                        .isDefault(v.getIsDefault())
                        .build())
                .collect(Collectors.toList());

        return ProductInfoDto.builder()
                .productId(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .shortDescription(entity.getShortDescription())
                .categoryName(entity.getCategory() != null
                        ? entity.getCategory().getName() : null)
                .brandName(entity.getBrand() != null
                        ? entity.getBrand().getName() : null)
                .isActive(entity.getIsActive())
                .variants(variants)
                .storeId(entity.getStoreId())
                .mallId(entity.getMallId())
                .medium(medium)
                .build();
    }
}
