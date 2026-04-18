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
@RequestMapping("stores/{storeId}/products")
//   @PreAuthorize("@auth.isAdminOrShopOwnerOf()")
@RequiredArgsConstructor
public class StoreProductController {

    private final ProductService productService;

    @PostMapping("/all")
    public EMallsResponseEntity<PaginatedResponse<ProductDto>> getAll(@PathVariable Long storeId, @RequestBody ProductFilter filter, Pageable pageable) {
        filter.setStoreId(storeId);
        PaginatedResponse<ProductDto> products = productService.getAll(filter, pageable);
        return EMallsResponseEntity.ok(products);
    }

    @PostMapping("/light")
    public EMallsResponseEntity<PaginatedResponse<ProductLightDto>> getAllLight(@PathVariable Long storeId, @RequestBody ProductFilter filter, Pageable pageable) {
        filter.setStoreId(storeId);
        PaginatedResponse<ProductLightDto> products = productService.getAllLight(filter, pageable);
        return EMallsResponseEntity.ok(products);
    }

    @PostMapping("/summary")
    public EMallsResponseEntity<ProductSummary> getSummary(@PathVariable Long storeId, @RequestBody ProductFilter filter) {
        filter.setStoreId(storeId);
        ProductSummary productsSummary = productService.getSummary(filter);
        return EMallsResponseEntity.ok(productsSummary);
    }

    @PostMapping("/all/list")
    public EMallsResponseEntity<List<ProductDto>> getAllList(@PathVariable Long storeId, @RequestBody ProductFilter filter) {
        filter.setStoreId(storeId);
        List<ProductDto> products = productService.getAllProductList(filter);
        return EMallsResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public EMallsResponseEntity<ProductDto> getById(@PathVariable Long storeId, @PathVariable Long id) {
        ProductDto dto = productService.getByStoreIdAndId(storeId, id);
        return EMallsResponseEntity.ok(dto);
    }

    @GetMapping("/slug/{slug}")
    public EMallsResponseEntity<ProductDto> getBySlug(@PathVariable Long storeId, @PathVariable String slug) {
        ProductDto dto = productService.getByStoreIdAndSlug(storeId, slug);
        return EMallsResponseEntity.ok(dto);
    }

    @PostMapping
    public EMallsResponseEntity<ProductDto> create(@PathVariable Long storeId, @RequestBody @Validated({Default.class, OnCreate.class}) ProductDto dto) {
        // TODO: git mallId from token
        dto.setStoreId(storeId);
        ProductDto created = productService.create(dto, 1L, storeId);
        return EMallsResponseEntity.created(created);
    }

    @PutMapping
    public EMallsResponseEntity<ProductDto> update(@PathVariable Long storeId, @RequestBody @Validated({Default.class, OnUpdate.class}) ProductDto dto) {
        // TODO: git mallId from token
        dto.setStoreId(storeId);
        ProductDto updated = productService.update(dto, 1L, storeId);
        return EMallsResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public EMallsResponseEntity<Void> delete(@PathVariable Long storeId, @PathVariable Long id) {
        productService.delete(id, storeId);
        return EMallsResponseEntity.noContent(null);
    }
}
