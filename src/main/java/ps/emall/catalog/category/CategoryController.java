package ps.emall.catalog.category;

import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ps.emall.catalog.category.audience_config.CategoryAudienceConfigDto;
import ps.emall.catalog.common.audience.TargetedAudience;
import ps.emall.catalog.common.page.PaginatedResponse;
import ps.emall.catalog.common.response.EMallsResponseEntity;
import ps.emall.catalog.common.validation.OnCreate;
import ps.emall.catalog.common.validation.OnUpdate;
import ps.emall.catalog.security.SecurityContextUtilBean;
import ps.emall.catalog.security.userdetails.Gender;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final SecurityContextUtilBean auth;

    @GetMapping
    public EMallsResponseEntity<PaginatedResponse<CategoryDto>> getAll(@ModelAttribute CategoryFilter filter, Pageable pageable) {
        if (!auth.isAdmin()) {
            filter.setIsActive(true);
        }
        boolean isMale = !Gender.FEMALE.equals(auth.getCurrentGender());
        if (isMale) {
            filter.setTargetedAudience(TargetedAudience.MALE);
        }
        PaginatedResponse<CategoryDto> categories = categoryService.getAll(filter, pageable);
        return EMallsResponseEntity.ok(categories);
    }

    @GetMapping("/light")
    public EMallsResponseEntity<PaginatedResponse<CategoryLightDto>> getAllLight(@ModelAttribute CategoryFilter filter, Pageable pageable) {
        if (!auth.isAdmin()) {
            filter.setIsActive(true);
        }
        boolean isMale = !Gender.FEMALE.equals(auth.getCurrentGender());
        if (isMale) {
            filter.setTargetedAudience(TargetedAudience.MALE);
        }
        PaginatedResponse<CategoryLightDto> categories = categoryService.getAllLight(filter, pageable);
        return EMallsResponseEntity.ok(categories);
    }

    @GetMapping("/all")
    public EMallsResponseEntity<List<CategoryDto>> getCategories(@ModelAttribute CategoryFilter filter) {
        if (!auth.isAdmin()) {
            filter.setIsActive(true);
        }
        boolean isMale = !Gender.FEMALE.equals(auth.getCurrentGender());
        if (isMale) {
            filter.setTargetedAudience(TargetedAudience.MALE);
        }
        List<CategoryDto> categories = categoryService.getAllCategoryList(filter);
        return EMallsResponseEntity.ok(categories);
    }

    @GetMapping("/tree")
    public EMallsResponseEntity<List<CategoryTreeDto>> getCategories() {
        Boolean isActive = null;
        if (!auth.isAdmin()) {
            isActive = true;
        }
        List<CategoryTreeDto> categories = categoryService.getTree(isActive);
        return EMallsResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public EMallsResponseEntity<CategoryDto> getById(@PathVariable Long id) {
        boolean isAdmin = auth.isAdmin();

        CategoryDto dto = isAdmin ? categoryService.getById(id) : categoryService.getActiveById(id);
        return EMallsResponseEntity.ok(dto);
    }

    @GetMapping("/slug/{slug}")
    public EMallsResponseEntity<CategoryDto> getBySlug(@PathVariable String slug) {
        boolean isAdmin = auth.isAdmin();

        CategoryDto dto = isAdmin ? categoryService.getBySlug(slug) : categoryService.getActiveBySlug(slug);
        return EMallsResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<CategoryDto> create(@RequestBody @Validated({Default.class, OnCreate.class}) CategoryDto dto) {
        CategoryDto created = categoryService.create(dto);
        return EMallsResponseEntity.created(created);
    }

    @PutMapping
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<CategoryDto> update(@RequestBody @Validated({Default.class, OnUpdate.class}) CategoryDto dto) {
        CategoryDto updated = categoryService.update(dto);
        return EMallsResponseEntity.ok(updated);
    }

    @PutMapping("/{categoryId}/audience")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<CategoryDto> addAudienceConfig(@PathVariable Long categoryId, @RequestBody @Validated({Default.class, OnCreate.class}) CategoryAudienceConfigDto dto) {
        CategoryDto created = categoryService.addAudienceConfig(categoryId, dto);
        return EMallsResponseEntity.created(created);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return EMallsResponseEntity.noContent(null);
    }

    @DeleteMapping("/{categoryId}/audience/{id}")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<CategoryDto> removeAudienceConfig(@PathVariable Long categoryId, @PathVariable Long id) {
        categoryService.removeAudienceConfig(categoryId, id);
        return EMallsResponseEntity.noContent(null);
    }
}
