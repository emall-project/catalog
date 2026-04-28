package ps.emall.catalog.client.interaction.product_similarity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimilarProductsQuery {
    private Long productId;
    private Integer topK;
    private Boolean sameMallOnly;
    private Boolean inStockOnly;
    private Boolean activeOnly;
}