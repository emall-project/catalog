package ps.emall.catalog.category;

import ps.emall.catalog.category.audience_config.CategoryAudienceConfig;
import ps.emall.catalog.category.audience_config.CategoryAudienceConfigDto;
import ps.emall.catalog.category.audience_config.CategoryAudienceConfigMapper;
import ps.emall.catalog.client.media_manager.FileDto;
import ps.emall.catalog.client.media_manager.FileLightDto;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CategoryMapper {

    public static Category toEntity(CategoryDto categoryDto) {
        Category category = Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .slug(categoryDto.getSlug())
                .targetedAudience(categoryDto.getTargetedAudience())
                .ageGroup(categoryDto.getAgeGroup())
                .isActive(categoryDto.getIsActive())
                .imageId(categoryDto.getImageId())
                .depthLevel(categoryDto.getDepthLevel())
                .build();

        if (categoryDto.getParentId() != null) {
            Category parent = new Category();
            parent.setId(categoryDto.getParentId());
            category.setParent(parent);
        }

        if (categoryDto.getAudienceConfig() != null) {
            Set<CategoryAudienceConfig> configs = categoryDto.getAudienceConfig().stream()
                    .map(dto -> CategoryAudienceConfigMapper.toEntity(dto, category))
                    .collect(Collectors.toSet());
            category.setAudienceConfig(configs);
        }

        return category;
    }

    public static CategoryDto toDto(Category category) {
        CategoryDto categoryDto = CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .targetedAudience(category.getTargetedAudience())
                .ageGroup(category.getAgeGroup())
                .isActive(category.getIsActive())
                .imageId(category.getImageId())
                .depthLevel(category.getDepthLevel())
                .createdAt(category.getCreatedAt())
                .createdBy(category.getCreatedBy())
                .updatedAt(category.getUpdatedAt())
                .updatedBy(category.getUpdatedBy())
                .audienceConfig(
                        category.getAudienceConfig() != null
                                ? category.getAudienceConfig().stream()
                                .map(CategoryAudienceConfigMapper::toDto)
                                .collect(Collectors.toSet())
                                : new HashSet<>()
                )
                .build();

        if (category.getParent() != null) {
            categoryDto.setParentId(category.getParent().getId());
        }

        return categoryDto;
    }

    public static CategoryDto toDto(Category category, FileDto image) {
        return toDto(category, image, category.getAudienceConfig());
    }

    public static CategoryDto toDto(Category category, Set<CategoryAudienceConfig> audienceConfig) {
        CategoryDto categoryDto = CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .targetedAudience(category.getTargetedAudience())
                .ageGroup(category.getAgeGroup())
                .isActive(category.getIsActive())
                .imageId(category.getImageId())
                .depthLevel(category.getDepthLevel())
                .createdAt(category.getCreatedAt())
                .createdBy(category.getCreatedBy())
                .updatedAt(category.getUpdatedAt())
                .updatedBy(category.getUpdatedBy())
                .audienceConfig(toAudienceConfigDtos(audienceConfig))
                .build();

        if (category.getParent() != null) {
            categoryDto.setParentId(category.getParent().getId());
        }

        return categoryDto;
    }

    public static CategoryDto toDto(Category category, FileDto image, Set<CategoryAudienceConfig> audienceConfig) {
        CategoryDto categoryDto = CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .targetedAudience(category.getTargetedAudience())
                .ageGroup(category.getAgeGroup())
                .isActive(category.getIsActive())
                .imageId(category.getImageId())
                .depthLevel(category.getDepthLevel())
                .createdAt(category.getCreatedAt())
                .createdBy(category.getCreatedBy())
                .updatedAt(category.getUpdatedAt())
                .updatedBy(category.getUpdatedBy())
                .image(image)
                .audienceConfig(toAudienceConfigDtos(audienceConfig))
                .build();

        if (category.getParent() != null) {
            categoryDto.setParentId(category.getParent().getId());
        }

        return categoryDto;
    }

    private static Set<CategoryAudienceConfigDto> toAudienceConfigDtos(Set<CategoryAudienceConfig> audienceConfig) {
        return audienceConfig != null
                ? audienceConfig.stream()
                .map(CategoryAudienceConfigMapper::toDto)
                .collect(Collectors.toSet())
                : new HashSet<>();
    }

    public static CategoryLightDto toLightDto(Category category) {
        return CategoryLightDto.builder()
                .id(category.getId())
                .name(category.getName())
                .depthLevel(category.getDepthLevel())
                .build();
    }
    public static CategoryLightDto toLightDto(Category category, FileLightDto image) {
        return CategoryLightDto.builder()
                .id(category.getId())
                .name(category.getName())
                .depthLevel(category.getDepthLevel())
                .image(image)
                .build();
    }

    public static CategoryTreeDto toTreeDto(Category category) {
        return CategoryTreeDto.builder()
                .id(category.getId())
                .name(category.getName())
                .depthLevel(category.getDepthLevel())
                .build();
    }

    public static CategoryTreeDto toTreeDto(Category category, FileLightDto image) {
        return CategoryTreeDto.builder()
                .id(category.getId())
                .name(category.getName())
                .depthLevel(category.getDepthLevel())
                .image(image)
                .build();
    }
}
