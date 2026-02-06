package ps.emall.catalog.category;

import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ps.emall.catalog.common.response.EMallsResponseEntity;
import ps.emall.catalog.common.validation.OnCreate;
import ps.emall.catalog.common.validation.OnUpdate;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public EMallsResponseEntity<List<CategoryDto>> getAll(CategorySpec spec, Pageable pageable) {
        Page<CategoryDto> categories = categoryService.getAll(spec, pageable);
        return EMallsResponseEntity.ok(categories);
    }



    @GetMapping("/all")
    public EMallsResponseEntity<List<CategoryDto>> getCategories(CategorySpec spec) {
        List<CategoryDto> getAllCategories = categoryService.getAllCategoryList(spec);
        return EMallsResponseEntity.ok(getAllCategories);
    }

    @GetMapping("/{id}")
    public EMallsResponseEntity<CategoryDto> getById(@PathVariable Long id) {
        CategoryDto dto = categoryService.getById(id);
        return EMallsResponseEntity.ok(dto);
    }

    @GetMapping("/slug/{slug}")
    public EMallsResponseEntity<CategoryDto> getBySlug(@PathVariable String slug) {
        CategoryDto dto = categoryService.getBySlug(slug);
        return EMallsResponseEntity.ok(dto);
    }

    @PostMapping
    public EMallsResponseEntity<CategoryDto> create(@RequestBody @Validated({Default.class, OnCreate.class}) CategoryDto dto) {
        CategoryDto created = categoryService.create(dto);
        return EMallsResponseEntity.created(created);
    }

    @PutMapping("/{id}")
    public EMallsResponseEntity<CategoryDto> update(@PathVariable Long id,
                                                    @RequestBody @Validated({Default.class, OnUpdate.class}) CategoryDto dto) {
        dto.setId(id);
        CategoryDto updated = categoryService.update(dto);
        return EMallsResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public EMallsResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return EMallsResponseEntity.noContent(null);
    }

    @GetMapping("/roots")
    public EMallsResponseEntity<List<CategoryDto>> getRoots() {
        List<CategoryDto> roots = categoryService.getRoots();
        return EMallsResponseEntity.ok(roots);
    }

    @GetMapping("/{parentId}/children")
    public EMallsResponseEntity<List<CategoryDto>> getChildren(@PathVariable Long parentId) {
        List<CategoryDto> children = categoryService.getChildren(parentId);
        return EMallsResponseEntity.ok(children);
    }

}
