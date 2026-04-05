package ps.emall.catalog.product.review.rating;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import ps.emall.catalog.common.base.EMallsBaseEntity;
import ps.emall.catalog.product.Product;

@Entity
@Table(
        name = "product_reviews",
        schema = "catalog",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_review_user_product",
                columnNames = {"product_id", "user_id"}
        )
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "product_reviews_audit", schema = "audit")
public class ProductReview extends EMallsBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_reviews_seq")
    @SequenceGenerator(
            name = "product_reviews_seq",
            sequenceName = "product_reviews_seq",
            schema = "catalog",
            allocationSize = 1
    )
    @Column(name = "review_id")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "rating", nullable = false)
    private Short rating;
}