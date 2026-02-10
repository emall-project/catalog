package ps.emall.catalog.brand;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import ps.emall.catalog.common.audience.AgeGroup;
import ps.emall.catalog.common.audience.TargetedAudience;
import ps.emall.catalog.common.base.EMallsBaseEntity;
import java.util.UUID;

@Entity
@Table(name = "brands", schema = "catalog")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
//@Audited
//@AuditTable(value = "brands_audit", schema = "audit")
public class Brand extends EMallsBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "brands_seq")
    @SequenceGenerator(
            name = "brands_seq",
            sequenceName = "brands_seq",
            schema = "catalog",
            allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", unique = true, nullable = false)
    private String slug;

    @Column(name = "targeted_audience", nullable = false)
    @Enumerated(EnumType.STRING)
    private TargetedAudience targetedAudience;

    @Column(name = "age_group", nullable = false)
    @Enumerated(EnumType.STRING)
    private AgeGroup ageGroup;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "image_file_key", nullable = false)
    private UUID imageFileKey;

}
