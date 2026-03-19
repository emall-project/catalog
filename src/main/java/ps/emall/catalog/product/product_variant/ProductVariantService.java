package ps.emall.catalog.product.product_variant;

import java.util.List;

public interface ProductVariantService {

    ProductVariantDto create(Long productId, ProductVariantDto dto);

    ProductVariantDto injectMedia(ProductVariantDto dto);

}
