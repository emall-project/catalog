package ps.emall.catalog.product.product_variant;

import java.util.List;

public interface ProductVariantService {

    ProductVariantDto create(Long productId, ProductVariantDto dto);

    ProductVariantDto update(Long productId, Long variantId, ProductVariantDto dto);

    ProductVariantDto getById(Long productId, Long variantId);

    List<ProductVariantDto> getByProductId(Long productId);

    void delete(Long productId, Long variantId);

}
