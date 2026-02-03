package ps.emall.catalog.category;

import java.util.List;

public interface CategoryService {

    CategoryDto create(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto);

    void delete(Long id);

    void deactivate(Long id);

    void activate(Long id);

    CategoryDto getById(Long id);

    CategoryDto getBySlug(String slug);

    List<CategoryDto> getAll();

    List<CategoryDto> getRoots();

    List<CategoryDto> getChildren(Long parentId);

    List<CategoryDto> search(CategorySpec spec);

    boolean slugExists(String slug);
}
