package ps.emall.catalog.product;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ps.emall.catalog.common.page.PaginatedResponse;
import ps.emall.catalog.product.info.ProductInfoDto;
import ps.emall.catalog.product.light.ProductLightDto;
import ps.emall.catalog.product.summary.ProductSummary;

import java.util.List;

@Service
public interface ProductService {


    PaginatedResponse<ProductLightDto> getAllLight(ProductFilter filter, Pageable pageable);

    List<ProductLightDto> getAllProductList(ProductFilter filter);

    ProductDto create(Long mallId, Long storeId, ProductDto productDto);

    ProductDto update(Long mallId, Long storeId, ProductDto productDto);

    ProductDto getById(Long id);

    ProductDto getByStoreIdAndId(Long storeId, Long id);

    ProductDto getBySlug(String slug);

    ProductDto getByStoreIdAndSlug(Long storeId, String slug);

    void delete(Long storeId, Long id);

    ProductInfoDto getProductInfo(Long id);

    ProductSummary getSummary(ProductFilter filter);

    List<ProductLightDto> getSimilar(Long id, Integer topK);
}
