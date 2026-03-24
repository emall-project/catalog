package ps.emall.catalog.client.campaigns;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActiveOfferDto {

    private Long offerItemId;
    private Long offerId;
    private Long productId;
    private String discountType;
    private BigDecimal discountValue;

    private List<VariantDiscountDto> variantPrices;

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VariantDiscountDto {
        private Long variantId;
        private BigDecimal originalPrice;
        private BigDecimal discountedPrice;
        private Boolean isDefault;
        private String discountType;
        private BigDecimal discountValue;
    }
}