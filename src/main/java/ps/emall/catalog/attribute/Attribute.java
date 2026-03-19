package ps.emall.catalog.attribute;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import ps.emall.catalog.attribute.attribute_options.AttributeOption;
import ps.emall.catalog.common.base.EMallsBaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "attributes", schema = "catalog")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "attributes_audit", schema = "audit")
public class Attribute extends EMallsBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attributes_seq")
    @SequenceGenerator(
            name = "attributes_seq",
            sequenceName = "attributes_seq",
            schema = "catalog",
            allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", unique = true, nullable = false)
    private String slug;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AttributeType attributeType;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "attribute", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AttributeOption> options = new ArrayList<>();

    public void addOption(AttributeOption option) {
        if (this.options == null) {
            this.options = new ArrayList<>();
        }
        options.add(option);
        option.setAttribute(this);
    }
}