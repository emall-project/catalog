package ps.emall.catalog.product.light;

import ps.emall.catalog.client.campaigns.ActiveProductDiscountDto;
import ps.emall.catalog.client.media_manager.FileLightDto;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public record ProductLightLookup(
        Map<Long, ProductLightRow> rowMap,
        Map<UUID, FileLightDto> mediaMap,
        Map<Long, ActiveProductDiscountDto> discountMap
) {
    public static ProductLightLookup empty() {
        return new ProductLightLookup(
                Collections.emptyMap(),
                Collections.emptyMap(),
                Collections.emptyMap()
        );
    }
}
