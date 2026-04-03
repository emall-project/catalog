package ps.emall.catalog.product.product_variant;


public interface ProductVariantService {

    ProductVariantDto create(Long productId, ProductVariantDto dto);

    ProductVariantDto injectMedia(ProductVariantDto dto);

}
