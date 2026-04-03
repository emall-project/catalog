package ps.emall.catalog.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ps.emall.catalog.common.audience.AgeGroup;
import ps.emall.catalog.common.audience.TargetedAudience;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilter {
    private String q;
    private String slug;
    private Long categoryId;
    private Long brandId;
    private Boolean isActive;
    private TargetedAudience targetedAudience;
    private AgeGroup ageGroup;
    private Map<Long, List<Long>> selectedOptionsByAttribute;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}
