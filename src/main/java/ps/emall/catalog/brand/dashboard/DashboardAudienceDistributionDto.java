package ps.emall.catalog.brand.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardAudienceDistributionDto {
    private long male;
    private long female;
    private long all;
}
