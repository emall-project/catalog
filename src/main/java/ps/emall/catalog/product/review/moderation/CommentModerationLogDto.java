package ps.emall.catalog.product.review.moderation;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentModerationLogDto {
    private Long logId;
    private Long commentId;
    private ModerationProvider provider;
    private ModerationDecision decision;
    private String reason;
    private BigDecimal confidence;
    private LocalDateTime createdAt;
}