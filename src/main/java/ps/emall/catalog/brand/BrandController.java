package ps.emall.catalog.brand;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ps.emall.catalog.common.audience.TargetedAudience;
import ps.emall.catalog.common.page.PaginatedResponse;
import ps.emall.catalog.common.response.EMallsResponseEntity;
import ps.emall.catalog.common.validation.OnCreate;
import ps.emall.catalog.common.validation.OnUpdate;
import ps.emall.catalog.security.SecurityContextUtilBean;
import ps.emall.catalog.security.userdetails.Gender;

import java.util.List;

@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;
    private final SecurityContextUtilBean auth;

    @GetMapping
    public EMallsResponseEntity<PaginatedResponse<BrandDto>> getAll(BrandFilter filter, Pageable pageable) {
        if (!auth.isAdmin()) {
            filter.setIsActive(true);
        }
        boolean isMale = !auth.getCurrentGender().equals(Gender.FEMALE);
        if (isMale) {
            filter.setTargetedAudience(TargetedAudience.MALE);
        }
        PaginatedResponse<BrandDto> page = brandService.getAll(filter, pageable);
        return EMallsResponseEntity.ok(page);
    }

    @GetMapping("/all")
    public EMallsResponseEntity<List<BrandDto>> getBrands(BrandFilter filter) {
        boolean isMale = !auth.getCurrentGender().equals(Gender.FEMALE);
        if (isMale) {
            filter.setTargetedAudience(TargetedAudience.MALE);
        }
        if (!auth.isAdmin()) {
            filter.setIsActive(true);
        }
        List<BrandDto> brands = brandService.getAllBrandsList(filter);
        return EMallsResponseEntity.ok(brands);
    }


    @GetMapping("/{id}")
    public EMallsResponseEntity<BrandDto> getById(@PathVariable Long id) {
        boolean isAdmin = auth.isAdmin();

        BrandDto dto = isAdmin ? brandService.getById(id) : brandService.getActiveById(id);
        return EMallsResponseEntity.ok(dto);
    }

    @GetMapping("/slug/{slug}")
    public EMallsResponseEntity<BrandDto> getBySlug(@PathVariable String slug) {
        boolean isAdmin = auth.isAdmin();

        BrandDto dto = isAdmin? brandService.getBySlug(slug):  brandService.getActiveBySlug(slug);
        return EMallsResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<BrandDto> create(@Validated(OnCreate.class) @RequestBody BrandDto dto) {
        BrandDto created = brandService.create(dto);
        return EMallsResponseEntity.created(created);
    }

    @PutMapping
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<BrandDto> update(@Validated(OnUpdate.class) @RequestBody BrandDto dto) {
        BrandDto updated = brandService.update(dto);
        return EMallsResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<Void> delete(@PathVariable Long id) {
        brandService.delete(id);
        return EMallsResponseEntity.noContent(null);
    }
}
