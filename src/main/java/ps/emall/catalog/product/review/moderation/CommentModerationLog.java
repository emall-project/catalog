package ps.emall.catalog.product.review.moderation;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import ps.emall.catalog.product.review.comment.ProductComment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "comment_moderation_log", schema = "catalog")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "comment_moderation_log_audit", schema = "audit")
public class CommentModerationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_moderation_log_seq")
    @SequenceGenerator(
            name = "comment_moderation_log_seq",
            sequenceName = "comment_moderation_log_seq",
            schema = "catalog",
            allocationSize = 1
    )
    @Column(name = "log_id")
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private ProductComment comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 20)
    private ModerationProvider provider;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision", nullable = false, length = 20)
    private ModerationDecision decision;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "confidence", precision = 5, scale = 4)
    private BigDecimal confidence;

    @Column(name = "raw_response", columnDefinition = "TEXT")
    private String rawResponse;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}