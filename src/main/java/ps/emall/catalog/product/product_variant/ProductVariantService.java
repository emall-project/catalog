package ps.emall.catalog.product.product_variant;

import org.springframework.stereotype.Service;

@Service
public interface ProductVariantService {

    ProductVariantDto create(Long productId, ProductVariantDto dto);

    ProductVariantDto add(Long storeId, Long productId, ProductVariantDto dto);

    ProductVariantDto update(Long storeId, Long productId, ProductVariantDto dto);

    void delete(Long storeId, Long productId, Long id);

}
