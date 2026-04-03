package ps.emall.catalog.product;

import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ps.emall.catalog.common.page.PaginatedResponse;
import ps.emall.catalog.common.response.EMallsResponseEntity;
import ps.emall.catalog.common.validation.OnCreate;
import ps.emall.catalog.common.validation.OnUpdate;
import ps.emall.catalog.product.light.ProductLightDto;
import ps.emall.catalog.product.summary.ProductSummary;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public EMallsResponseEntity<PaginatedResponse<ProductDto>> getAll(@RequestBody ProductFilter filter, Pageable pageable) {
        PaginatedResponse<ProductDto> products = productService.getAll(filter, pageable);
        return EMallsResponseEntity.ok(products);
    }

    @GetMapping("/light")
    public EMallsResponseEntity<PaginatedResponse<ProductLightDto>> getAllLight(@RequestBody ProductFilter filter, Pageable pageable) {
        PaginatedResponse<ProductLightDto> products = productService.getAllLight(filter, pageable);
        return EMallsResponseEntity.ok(products);
    }

    @GetMapping("/summary")
    public EMallsResponseEntity<ProductSummary> getSummary(@RequestBody ProductFilter filter) {
        ProductSummary productsSummary = productService.getSummary(filter);
        return EMallsResponseEntity.ok(productsSummary);
    }

    @GetMapping("/all")
    public EMallsResponseEntity<List<ProductDto>> getAllList(@RequestBody ProductFilter filter) {
        List<ProductDto> products = productService.getAllProductList(filter);
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

    @GetMapping("/{id}/info")
    public EMallsResponseEntity<ProductInfoDto> getProductInfo(@PathVariable Long id) {
        return EMallsResponseEntity.ok(productService.getProductInfo(id));
    }

    @PostMapping("{mallId}/{storeId}")
    public EMallsResponseEntity<ProductDto> create(
            @RequestBody @Validated({Default.class, OnCreate.class}) ProductDto dto,
            @PathVariable Long mallId,
            @PathVariable Long storeId
    ) {
        ProductDto created = productService.create(dto, mallId, storeId);
        return EMallsResponseEntity.created(created);
    }

    @PutMapping
    public EMallsResponseEntity<ProductDto> update(
            @RequestBody @Validated({Default.class, OnUpdate.class}) ProductDto dto,
            @PathVariable Long mallId,
            @PathVariable Long storeId
    ) {
        ProductDto updated = productService.update(dto, mallId, storeId);
        return EMallsResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public EMallsResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return EMallsResponseEntity.noContent(null);
    }
}
