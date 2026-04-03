package ps.emall.catalog.product.summary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgeDistribution {
    Long productSForNewborn;
    Long productSForInfant;
    Long productSForToddler;
    Long productSForChild;
    Long productSForTeenager;
    Long productSForYouth;
    Long productSForAdult;
    Long productSForAll;
}
