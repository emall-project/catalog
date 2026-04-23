package ps.emall.catalog.product;

import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ps.emall.catalog.common.page.PaginatedResponse;
import ps.emall.catalog.common.response.EMallsResponseEntity;
import ps.emall.catalog.common.validation.OnCreate;
import ps.emall.catalog.common.validation.OnUpdate;
import ps.emall.catalog.product.light.ProductLightDto;
import ps.emall.catalog.product.product_variant.ProductVariantDto;
import ps.emall.catalog.product.product_variant.ProductVariantService;
import ps.emall.catalog.product.summary.ProductSummary;

import java.util.List;

@RestController
@RequestMapping("stores/{storeId}/products")
@PreAuthorize("@auth.isAdminOrShopOwnerOf(#storeId)")
@RequiredArgsConstructor
public class StoreProductController {

    private final ProductService productService;
    private final ProductVariantService productVariantService;

    @PostMapping("/all")
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
    public EMallsResponseEntity<List<ProductLightDto>> getAllList(@PathVariable Long storeId, @RequestBody ProductFilter filter) {
        filter.setStoreId(storeId);
        List<ProductLightDto> products = productService.getAllProductList(filter);
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
        ProductDto created = productService.create(storeId, dto, 1L);
        return EMallsResponseEntity.created(created);
    }

    @PutMapping
    public EMallsResponseEntity<ProductDto> update(@PathVariable Long storeId, @RequestBody @Validated({Default.class, OnUpdate.class}) ProductDto dto) {
        // TODO: git mallId from token
        ProductDto updated = productService.update(1L, dto, storeId);
        return EMallsResponseEntity.ok(updated);
    }

    @PutMapping("{productId}/variants")
    public EMallsResponseEntity<ProductVariantDto> updateVariant(@PathVariable Long storeId, @PathVariable Long productId, @RequestBody @Validated({Default.class, OnUpdate.class}) ProductVariantDto dto) {
        ProductVariantDto updated = productVariantService.update(storeId, productId, dto);
        return EMallsResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public EMallsResponseEntity<Void> delete(@PathVariable Long storeId, @PathVariable Long id) {
        productService.delete(storeId, id);
        return EMallsResponseEntity.noContent(null);
    }

    @DeleteMapping("/{productId}/variants/{id}")
    public EMallsResponseEntity<Void> deleteVariant(@PathVariable Long storeId, @PathVariable Long productId, @PathVariable Long id) {
        productVariantService.delete(storeId, productId, id);
        return EMallsResponseEntity.noContent(null);
    }
}
