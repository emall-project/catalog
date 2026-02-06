package ps.emall.catalog.brand;

public class BrandMapper {
    public static Brand toEntity(BrandDto dto) {
        return Brand.builder()
                .id(dto.getId())
                .name(dto.getName())
                .slug(dto.getSlug())
                .targetedAudience(dto.getTargetedAudience())
                .ageGroup(dto.getAgeGroup())
                .isActive(dto.getIsActive())
                .imageFileKey(dto.getImageFileKey())
                .build();
    }
    public static BrandDto toDto(Brand entity) {
        return BrandDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .targetedAudience(entity.getTargetedAudience())
                .ageGroup(entity.getAgeGroup())
                .isActive(entity.getIsActive())
                .imageFileKey(entity.getImageFileKey())
                .build();
    }
}
