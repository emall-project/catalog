package ps.emall.catalog.product.review.comment;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import ps.emall.catalog.common.base.EMallsBaseEntity;
import ps.emall.catalog.product.Product;
import ps.emall.catalog.product.review.moderation.CommentModerationLog;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "product_comments",
        schema = "catalog",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_comment_user_product",
                columnNames = {"product_id", "user_id"}
        )
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "product_comments_audit", schema = "audit")
public class ProductComment extends EMallsBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_comments_seq")
    @SequenceGenerator(
            name = "product_comments_seq",
            sequenceName = "product_comments_seq",
            schema = "catalog",
            allocationSize = 1
    )
    @Column(name = "comment_id")
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private CommentStatus status = CommentStatus.PENDING_MODERATION;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "moderation_retry_count", nullable = false)
    @Builder.Default
    private int moderationRetryCount = 0;

    @Column(name = "last_moderation_attempt_at")
    private LocalDateTime lastModerationAttemptAt;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CommentModerationLog> moderationLogs = new ArrayList<>();

    @Column(name = "product_url")
    private String productUrl;
}