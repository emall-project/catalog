package ps.emall.catalog.attribute.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ps.emall.catalog.attribute.AttributeType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeTypeDistributionDto {
    private AttributeType attributeType;
    private Long totalAttributes;
}
