package ps.emall.catalog.product.summary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AudienceDistribution {
    Long productForMales;
    Long productForFemales;
    Long productForAll;
}
