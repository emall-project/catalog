package ps.emall.catalog.brand;

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
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public EMallsResponseEntity<Page<BrandDto>> getAll(BrandSpec spec, Pageable pageable) {
        Page<BrandDto> page = brandService.getAll(spec, pageable);
        return EMallsResponseEntity.ok(page);
    }

    @GetMapping("/all")
    public EMallsResponseEntity<List<BrandDto>> getBrands(BrandSpec spec) {
        List<BrandDto> brands = brandService.getAllBrandsList(spec);
        return EMallsResponseEntity.ok(brands);
    }


    @GetMapping("/{id}")
    public EMallsResponseEntity<BrandDto> getById(@PathVariable Long id) {
        BrandDto dto = brandService.findById(id);
        return EMallsResponseEntity.ok(dto);
    }

    @GetMapping("/slug/{slug}")
    public EMallsResponseEntity<BrandDto> getBySlug(@PathVariable String slug) {
        BrandDto dto = brandService.findBySlug(slug);
        return EMallsResponseEntity.ok(dto);
    }

    @PostMapping
    public EMallsResponseEntity<BrandDto> create(@Validated(OnCreate.class) @RequestBody BrandDto dto) {
        BrandDto created = brandService.create(dto);
        return EMallsResponseEntity.created(created);
    }

    @PutMapping
    public EMallsResponseEntity<BrandDto> update(@Validated(OnUpdate.class) @RequestBody BrandDto dto) {
        BrandDto updated = brandService.update(dto);
        return EMallsResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public EMallsResponseEntity<Void> delete(@PathVariable Long id) {
        brandService.delete(id);
        return EMallsResponseEntity.noContent(null);
    }
}
