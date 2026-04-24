package ps.emall.catalog.brand;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ps.emall.catalog.common.audience.AgeGroup;
import ps.emall.catalog.common.audience.TargetedAudience;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandFilter {

    private String name;
    private String slug;
    private Boolean isActive;
    private TargetedAudience targetedAudience;
    private AgeGroup ageGroup;
}
