package ps.emall.catalog.brand;

import ps.emall.catalog.client.media_manager.FileDto;

public class BrandMapper {
    public static Brand toEntity(BrandDto dto) {
        return Brand.builder()
                .id(dto.getId())
                .name(dto.getName())
                .slug(dto.getSlug())
                .targetedAudience(dto.getTargetedAudience())
                .ageGroup(dto.getAgeGroup())
                .isActive(dto.getIsActive())
                .imageId(dto.getImageId())
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
                .imageId(entity.getImageId())

                .createdAt(entity.getCreatedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    public static BrandDto toDto(Brand entity, FileDto image) {
        return BrandDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .targetedAudience(entity.getTargetedAudience())
                .ageGroup(entity.getAgeGroup())
                .isActive(entity.getIsActive())
                .imageId(entity.getImageId())
                .image(image)

                .createdAt(entity.getCreatedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
