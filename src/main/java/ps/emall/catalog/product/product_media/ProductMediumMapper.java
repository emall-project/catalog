package ps.emall.catalog.product.product_media;

public class ProductMediumMapper {
    public static ProductMediumDto toDto(ProductMedium entity) {
        return ProductMediumDto.builder()
                .id(entity.getId())
                .mediumId(entity.getMediumId())
                .sortOrder(entity.getSortOrder())
                .build();
    }
    public static ProductMedium toEntity(ProductMediumDto dto) {
        return ProductMedium.builder()
                .id(dto.getId())
                .mediumId(dto.getMediumId())
                .sortOrder(dto.getSortOrder())
                .build();
    }
}
