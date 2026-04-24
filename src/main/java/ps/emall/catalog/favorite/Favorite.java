package ps.emall.catalog.favorite;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import ps.emall.catalog.product.Product;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "favorites",
        schema = "catalog",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_favorites_user_product",
                        columnNames = {"user", "product_id"}
                )
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "favorites_audit", schema = "audit")
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "favorites_seq")
    @SequenceGenerator(
            name = "favorites_seq",
            sequenceName = "favorites_seq",
            schema = "catalog",
            allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @Column(name = "\"user\"", nullable = false, length = 50)
    private String user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDateTime addedAt;

    @PrePersist
    public void prePersist() {
        this.addedAt = LocalDateTime.now();
    }
}