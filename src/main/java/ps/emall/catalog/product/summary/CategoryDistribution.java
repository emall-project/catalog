package ps.emall.catalog.product.summary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDistribution {
    private Long id;
    private String name;
    private Long totalProduct;
}
