package ps.emall.catalog.client.interaction;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ps.emall.catalog.client.interaction.product_similarity.SimilarProductsQuery;
import ps.emall.catalog.client.interaction.product_similarity.SimilarProductsResult;

@FeignClient(name = "interaction", url = "${services.interaction.host}:${services.interaction.port}")
public interface InteractionClient {

    @PostMapping("ai/product-similarity/similar-products")
    InteractionResponse<SimilarProductsResult> getSimilarProducts(
            @RequestBody SimilarProductsQuery query
    );
}
