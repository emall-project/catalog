package ps.emall.catalog.product.product_media;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import ps.emall.catalog.common.base.EMallsBaseEntity;
import ps.emall.catalog.product.Product;
import ps.emall.catalog.product.product_variant.ProductVariant;

import java.util.UUID;

@Entity
@Table(
        name = "product_media",
        schema = "catalog",
        indexes = {
                @Index(
                        name = "idx_product_media_variant",
                        columnList = "variant_id"
                ),
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "product_media_audit", schema = "audit")
public class ProductMedium extends EMallsBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_media_seq")
    @SequenceGenerator(
            name = "product_media_seq",
            sequenceName = "product_media_seq",
            schema = "catalog",
            allocationSize = 1
    )
    @Column(name = "id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "medium_id", nullable = false)
    private UUID mediumId;

}
