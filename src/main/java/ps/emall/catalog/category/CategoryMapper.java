package ps.emall.catalog.category;

public class CategoryMapper {
    public static Category toEntity(CategoryDto categoryDto) {
        Category category = Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .slug(categoryDto.getSlug())
                .isActive(categoryDto.isActive())
                .imageFileKey(categoryDto.getImageFileKey())
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
                .isActive(category.isActive())
                .imageFileKey(category.getImageFileKey())
                .build();
        if (category.getParent() != null) {
            categoryDto.setParentId(category.getParent().getId());
        }
        return categoryDto;
    }
}
