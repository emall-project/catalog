package ps.emall.catalog.category;

import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ps.emall.catalog.category.audience_config.CategoryAudienceConfigDto;
import ps.emall.catalog.common.page.PaginatedResponse;
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
    public EMallsResponseEntity<PaginatedResponse<CategoryDto>> getAll(CategorySpec spec, Pageable pageable) {
        PaginatedResponse<CategoryDto> categories = categoryService.getAll(spec, pageable);
        return EMallsResponseEntity.ok(categories);
    }
    @GetMapping("/light")
    public EMallsResponseEntity<PaginatedResponse<CategoryLightDto>> getAllLight(CategorySpec spec, Pageable pageable) {
        PaginatedResponse<CategoryLightDto> categories = categoryService.getAllLight(spec, pageable);
        return EMallsResponseEntity.ok(categories);
    }

    @GetMapping("/all")
    public EMallsResponseEntity<List<CategoryDto>> getCategories(CategorySpec spec) {
        List<CategoryDto> categories = categoryService.getAllCategoryList(spec);
        return EMallsResponseEntity.ok(categories);
    }

    @GetMapping("/tree")
    public EMallsResponseEntity<List<CategoryTreeDto>> getCategories() {
        List<CategoryTreeDto> categories = categoryService.getTree();
        return EMallsResponseEntity.ok(categories);
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

    @PutMapping
    public EMallsResponseEntity<CategoryDto> update(@RequestBody @Validated({Default.class, OnUpdate.class}) CategoryDto dto) {
        CategoryDto updated = categoryService.update(dto);
        return EMallsResponseEntity.ok(updated);
    }

    @PutMapping("/{categoryId}/audience")
    public EMallsResponseEntity<CategoryDto> addAudienceConfig(@PathVariable Long categoryId, @RequestBody @Validated({Default.class, OnCreate.class}) CategoryAudienceConfigDto dto) {
        CategoryDto created = categoryService.addAudienceConfig(categoryId, dto);
        return EMallsResponseEntity.created(created);
    }

    @DeleteMapping("/{id}")
    public EMallsResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return EMallsResponseEntity.noContent(null);
    }

    @DeleteMapping("/{categoryId}/audience/{id}")
    public EMallsResponseEntity<CategoryDto> removeAudienceConfig(@PathVariable Long categoryId, @PathVariable Long id) {
        categoryService.removeAudienceConfig(categoryId, id);
        return EMallsResponseEntity.noContent(null);
    }
}
