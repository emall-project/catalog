package ps.emall.catalog.product.product_image;

public class ProductImageMapper {
    public static ProductImageDto toDto(ProductImage entity) {
        return ProductImageDto.builder()
                .id(entity.getId())
                .imageFileKey(entity.getImageFileKey())
                .sortOrder(entity.getSortOrder())
                .build();
    }
    public static ProductImage toEntity(ProductImageDto dto) {
        return ProductImage.builder()
                .id(dto.getId())
                .imageFileKey(dto.getImageFileKey())
                .sortOrder(dto.getSortOrder())
                .build();
    }
}
