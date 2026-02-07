package ps.emall.catalog.product.product_variant;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import ps.emall.catalog.attribute.Attribute;
import ps.emall.catalog.attribute.attribute_options.AttributeOption;
import ps.emall.catalog.common.base.EMallsBaseEntity;
import ps.emall.catalog.product.Product;
import ps.emall.catalog.product.product_image.ProductImage;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_variants", schema = "catalog")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable(value = "product_variants_audit", schema = "audit")
public class ProductVariant extends EMallsBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_variants_seq")
    @SequenceGenerator(
            name = "product_variants_seq",
            sequenceName = "product_variants_seq",
            schema = "catalog",
            allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "base_price")
    private double basePrice;

    @Column(name = "is_defualt")
    private Boolean isDefault;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    private Product product;


    @ManyToMany
    @JoinTable(
            name = "variant_attributes",
            joinColumns = @JoinColumn(name = "variant_id"),
            inverseJoinColumns = @JoinColumn(name = "attribute_id")
    )
    private List<Attribute> attributes = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "variant_attributes",
            joinColumns = @JoinColumn(name = "variant_id"),
            inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private List<AttributeOption> attribute_options = new ArrayList<>();

    @OneToMany(mappedBy = "product_variants", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    public void addImage(ProductImage image) {
        if (this.images == null) {
            this.images = new ArrayList<>();
        }
        images.add(image);
        image.setVariant(this);
    }
}
