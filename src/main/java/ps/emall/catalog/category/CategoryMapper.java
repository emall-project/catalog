package ps.emall.catalog.category;

import ps.emall.catalog.category.audience_config.CategoryAudienceConfig;
import ps.emall.catalog.category.audience_config.CategoryAudienceConfigMapper;
import ps.emall.catalog.client.media_manager.FileDto;

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
        CategoryDto categoryDto = CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .targetedAudience(category.getTargetedAudience())
                .ageGroup(category.getAgeGroup())
                .isActive(category.getIsActive())
                .imageId(category.getImageId())
                .image(image)
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
}