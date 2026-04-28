package ps.emall.catalog.client.interaction.product_similarity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimilarProductsResult {
    private boolean success;
    private String provider;
    private List<Long> productIds;
}
