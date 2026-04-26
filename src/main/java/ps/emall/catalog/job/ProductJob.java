package ps.emall.catalog.job;

import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductJob {

    private Long id;

    private String name;

    private String slug;

    private String targetedAudience;

    private String ageGroup;

    private Boolean isActive;

    private String shortDescription;

    private String description;

    private String category;

    private String brand;

    private Long mallId;

    private Long storeId;

    private List<String> tags;

    private Map<String, Set<String>> attributes;
}
