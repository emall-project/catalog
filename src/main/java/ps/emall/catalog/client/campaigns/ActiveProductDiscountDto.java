package ps.emall.catalog.client.campaigns;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActiveProductDiscountDto {
    private Long productId;
    private Long offerId;
    private DiscountType discountType;
    private BigDecimal discountValue;
}