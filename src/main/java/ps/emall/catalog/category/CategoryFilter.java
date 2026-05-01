package ps.emall.catalog.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ps.emall.catalog.common.audience.AgeGroup;
import ps.emall.catalog.common.audience.TargetedAudience;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryFilter {
    private String name;
    private String slug;
    private Long parentId;
    private Boolean isActive;
    private TargetedAudience targetedAudience;
    private TargetedAudience excludedAudience;
    private AgeGroup ageGroup;
    private Integer depthLevel;
    private Boolean isRoot;

}
