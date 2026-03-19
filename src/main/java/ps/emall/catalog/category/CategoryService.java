package ps.emall.catalog.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ps.emall.catalog.common.page.PaginatedResponse;

import java.util.List;

public interface CategoryService {

    PaginatedResponse<CategoryDto> getAll(Specification<Category> spec, Pageable pageable);

    List<CategoryDto> getAllCategoryList(CategorySpec spec);

    CategoryDto getById(Long id);

    CategoryDto getBySlug(String slug);

    CategoryDto create(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto);

    void delete(Long id);

    List<CategoryDto> getRoots();

    List<CategoryDto> getChildren(Long parentId);

    boolean slugExists(String slug);
}
