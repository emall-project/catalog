package ps.emall.catalog.product.light;

import java.util.List;

public interface ProductLightRepository {
    List<ProductLightRow> findLightRowsByProductIds(List<Long> productIds);
}