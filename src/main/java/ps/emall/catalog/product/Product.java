package ps.emall.catalog.product;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import ps.emall.catalog.brand.Brand;
import ps.emall.catalog.category.Category;
import ps.emall.catalog.common.audience.AgeGroup;
import ps.emall.catalog.common.audience.TargetedAudience;
import ps.emall.catalog.common.base.EMallsBaseEntity;
import ps.emall.catalog.product.product_variant.ProductVariant;
import ps.emall.catalog.tag.Tag;

import java.util.*;

@Entity
@Table(name = "products", schema = "catalog")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "products_audit", schema = "audit")
public class Product extends EMallsBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_sequence")
    @SequenceGenerator(
            name = "products_sequence",
            sequenceName = "products_sequence",
            schema = "catalog",
            allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "targeted_audience", nullable = false)
    @Enumerated(EnumType.STRING)
    private TargetedAudience targetedAudience;

    @Column(name = "age_group", nullable = false)
    @Enumerated(EnumType.STRING)
    private AgeGroup ageGroup;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "short_description", nullable = false)
    private String shortDescription;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Column(name = "mall_id", nullable = false)
    private Long mallId;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @ManyToMany
   @Audited
    @JoinTable(
            name = "product_tags",
            schema = "catalog",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
   @AuditJoinTable(
           name = "product_tags_audit",
           schema = "audit"
   )
    private List<Tag> tags = new ArrayList<>();


    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductVariant> variants = new ArrayList<>();

    public void addVariant(ProductVariant variant) {
        if (this.variants == null) {
            this.variants = new ArrayList<>();
        }
        variants.add(variant);
        variant.setProduct(this);
    }
}
