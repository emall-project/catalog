package ps.emall.catalog.product;

import org.springframework.data.domain.Pageable;
import ps.emall.catalog.common.page.PaginatedResponse;
import ps.emall.catalog.product.light.ProductLightDto;
import ps.emall.catalog.product.summary.ProductSummary;

import java.util.List;

public interface ProductService {

    PaginatedResponse<ProductDto> getAll(ProductFilter filter, Pageable pageable);

    PaginatedResponse<ProductLightDto> getAllLight(ProductFilter filter, Pageable pageable);

    List<ProductDto> getAllProductList(ProductFilter filter);

    ProductDto create(ProductDto productDto, Long mallId, Long storeId);

    ProductDto update(ProductDto productDto, Long mallId, Long storeId);

    ProductDto getById(Long id);

    ProductDto getBySlug(String slug);

    void delete(Long id);

    ProductInfoDto getProductInfo(Long id);

    ProductSummary getSummary(ProductFilter filter);
}
