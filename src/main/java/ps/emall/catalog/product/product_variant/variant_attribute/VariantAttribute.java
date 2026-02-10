package ps.emall.catalog.product.product_variant.variant_attribute;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import ps.emall.catalog.attribute.Attribute;
import ps.emall.catalog.attribute.attribute_options.AttributeOption;
import ps.emall.catalog.product.product_variant.ProductVariant;

@Entity
@Table(name = "variant_attributes", schema = "catalog")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@Audited
//@AuditTable(value = "variant_attributes_audit", schema = "audit")
public class VariantAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "variant_attributes_seq")
    @SequenceGenerator(name = "variant_attributes_seq", sequenceName = "variant_attributes_seq", schema = "catalog", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id")
    private Attribute attribute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private AttributeOption option;
}
