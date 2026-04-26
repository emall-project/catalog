package ps.emall.catalog.client.campaigns;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import java.util.List;

@FeignClient(
        name = "campaigns-service",
        url = "${services.campaigns.host}:${services.campaigns.port}"
)
public interface CampaignsClient {

    @GetMapping("/api/offers/product/{productId}/active-price")
    CampaignsResponse getActiveOfferForProduct(@PathVariable("productId") Long productId);

    @GetMapping("/api/subscriptions/shop/{shopId}/write-access")
    Boolean hasWriteAccess(@PathVariable("shopId") Long shopId);

    @PostMapping("/api/offers/products/active-discounts")
    CampaignsResponse<List<ActiveProductDiscountDto>> getActiveDiscountsForProducts(
            @RequestBody ActiveDiscountsRequest request
    );
}