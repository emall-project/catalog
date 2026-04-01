package ps.emall.catalog.attribute.attribute_options;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import ps.emall.catalog.attribute.Attribute;
import ps.emall.catalog.attribute.AttributeType;
import ps.emall.catalog.common.base.EMallsBaseEntity;

@Entity
@Table(
        name = "attribute_options",
        schema = "catalog",
        indexes = {
                @Index(
                        name = "idx_option_attribute_id",
                        columnList = "attribute_id"
                ),
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "attribute_options_audit", schema = "audit")
public class AttributeOption extends EMallsBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attribute_options_seq")
    @SequenceGenerator(
            name = "attribute_options_seq",
            sequenceName = "attribute_options_seq",
            schema = "catalog",
            allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @Column(name = "value", nullable = false)
    private String value;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    private Attribute attribute;

}