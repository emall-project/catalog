package ps.emall.catalog.product.review.moderation;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModerationResult {
    private ModerationProvider provider;
    private ModerationDecision decision;
    private String reason;
    private BigDecimal confidence;
    private String rawResponse;
    private boolean apiError;
}