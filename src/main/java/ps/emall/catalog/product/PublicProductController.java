package ps.emall.catalog.product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ps.emall.catalog.common.page.PaginatedResponse;
import ps.emall.catalog.common.response.EMallsResponseEntity;
import ps.emall.catalog.product.info.ProductInfoDto;
import ps.emall.catalog.product.light.ProductLightDto;
import ps.emall.catalog.product.summary.ProductSummary;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class PublicProductController {

    private final ProductService productService;

    @PostMapping("/all")
    public EMallsResponseEntity<PaginatedResponse<ProductLightDto>> getAllLight(@RequestBody ProductFilter filter, Pageable pageable) {
        filter.setIsActive(true);
        PaginatedResponse<ProductLightDto> products = productService.getAllLight(filter, pageable);
        return EMallsResponseEntity.ok(products);
    }

    @PostMapping("/summary")
    public EMallsResponseEntity<ProductSummary> getSummary(@RequestBody ProductFilter filter) {
        filter.setIsActive(true);
        ProductSummary productsSummary = productService.getSummary(filter);
        return EMallsResponseEntity.ok(productsSummary);
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
}
