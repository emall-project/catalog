package ps.emall.catalog.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ps.emall.catalog.common.page.PaginatedResponse;

import java.util.List;

public interface ProductService {

    PaginatedResponse<ProductDto> getAll(ProductSpec spec, Pageable pageable);

    List<ProductDto> getAllProductList(ProductSpec spec);

    ProductDto create(ProductDto productDto);

    ProductDto update(ProductDto productDto);

    ProductDto getById(Long id);

    ProductDto getBySlug(String slug);

    void delete(Long id);

    ProductInfoDto getProductInfo(Long id);
}
