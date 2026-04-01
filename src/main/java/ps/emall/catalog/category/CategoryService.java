package ps.emall.catalog.category;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ps.emall.catalog.category.audience_config.CategoryAudienceConfigDto;
import ps.emall.catalog.common.page.PaginatedResponse;

import java.util.List;

public interface CategoryService {

    PaginatedResponse<CategoryDto> getAll(Specification<Category> spec, Pageable pageable);

    PaginatedResponse<CategoryLightDto> getAllLight(Specification<Category> spec, Pageable pageable);

    List<CategoryDto> getAllCategoryList(CategorySpec spec);

    CategoryDto getById(Long id);

    CategoryDto getBySlug(String slug);

    List<CategoryDto> getRoots();

    List<CategoryDto> getChildren(Long parentId);

    CategoryDto create(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto);

    CategoryDto addAudienceConfig(Long categoryId, CategoryAudienceConfigDto categoryAudienceConfigDto);

    void delete(Long id);

    void removeAudienceConfig(Long categoryId, Long id);
    boolean slugExists(String slug);
}
