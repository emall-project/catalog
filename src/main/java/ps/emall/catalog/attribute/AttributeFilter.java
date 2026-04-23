package ps.emall.catalog.attribute;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeFilter {
    private String name;
    private String slug;
    private Boolean isActive;
    private AttributeType type;
}
