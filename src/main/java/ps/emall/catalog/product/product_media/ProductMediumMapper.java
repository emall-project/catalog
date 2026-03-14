package ps.emall.catalog.product.product_media;

public class ProductMediumMapper {
    public static ProductMediaDto toDto(ProductMedium entity) {
        return ProductMediaDto.builder()
                .id(entity.getId())
                .mediaId(entity.getMediaId())
                .sortOrder(entity.getSortOrder())
                .build();
    }
    public static ProductMedium toEntity(ProductMediaDto dto) {
        return ProductMedium.builder()
                .id(dto.getId())
                .mediaId(dto.getMediaId())
                .sortOrder(dto.getSortOrder())
                .build();
    }
}
