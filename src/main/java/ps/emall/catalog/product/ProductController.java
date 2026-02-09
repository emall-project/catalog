package ps.emall.catalog.product;

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
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public EMallsResponseEntity<Page<ProductDto>> getAll(ProductSpec spec, Pageable pageable) {
        Page<ProductDto> products = productService.getAll(spec, pageable);
        return EMallsResponseEntity.ok(products);
    }

    @GetMapping("/all")
    public EMallsResponseEntity<List<ProductDto>> getAllList(ProductSpec spec) {
        List<ProductDto> products = productService.getAllProductList(spec);
        return EMallsResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public EMallsResponseEntity<ProductDto> getById(@PathVariable Long id) {
        ProductDto dto = productService.getById(id);
        return EMallsResponseEntity.ok(dto);
    }

    @GetMapping("/slug/{slug}")
    public EMallsResponseEntity<ProductDto> getBySlug(@PathVariable String slug) {
        ProductDto dto = productService.getBySlug(slug);
        return EMallsResponseEntity.ok(dto);
    }

    @PostMapping
    public EMallsResponseEntity<ProductDto> create(
            @RequestBody @Validated({Default.class, OnCreate.class}) ProductDto dto
    ) {
        ProductDto created = productService.create(dto);
        return EMallsResponseEntity.created(created);
    }

    @PutMapping
    public EMallsResponseEntity<ProductDto> update(
            @RequestBody @Validated({Default.class, OnUpdate.class}) ProductDto dto
    ) {
        ProductDto updated = productService.update(dto);
        return EMallsResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public EMallsResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return EMallsResponseEntity.noContent(null);
    }
}
