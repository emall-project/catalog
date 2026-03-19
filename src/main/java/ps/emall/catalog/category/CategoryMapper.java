package ps.emall.catalog.category;

import ps.emall.catalog.client.media_manager.FileDto;

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
                .build();
        if (category.getParent() != null) {
            categoryDto.setParentId(category.getParent().getId());
        }
        return categoryDto;
    }
}
