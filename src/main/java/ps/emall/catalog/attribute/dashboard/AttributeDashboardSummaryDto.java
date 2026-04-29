package ps.emall.catalog.attribute.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeDashboardSummaryDto {
    private long totalAttributes;
    private long activeAttributes;
    private long inactiveAttributes;
    private long totalOptions;
    private double averageOptionsPerAttribute;
    private List<AttributeTypeDistributionDto> attributeTypeDistribution;
    private List<ProductAttributeDistributionDto> productDistributionByAttribute;
}
