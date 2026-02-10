package ps.emall.catalog.product.product_variant;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import ps.emall.catalog.attribute.Attribute;
import ps.emall.catalog.attribute.attribute_options.AttributeOption;
import ps.emall.catalog.common.base.EMallsBaseEntity;
import ps.emall.catalog.product.Product;
import ps.emall.catalog.product.product_image.ProductImage;
import ps.emall.catalog.product.product_variant.variant_attribute.VariantAttribute;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Entity
@Table(name = "product_variants", schema = "catalog")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
//@Audited
//@AuditTable(value = "product_variants_audit", schema = "audit")
public class ProductVariant extends EMallsBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_variants_sequence")
    @SequenceGenerator(
            name = "product_variants_sequence",
            sequenceName = "product_variants_sequence",
            schema = "catalog",
            allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    @Column(name = "is_default")
    private Boolean isDefault;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;


    @OneToMany(
            mappedBy = "variant",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<VariantAttribute> variantAttributes = new ArrayList<>();


    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    public void addImage(ProductImage image) {
        if (this.images == null) {
            this.images = new ArrayList<>();
        }
        images.add(image);
        image.setProduct(this.getProduct());
        image.setVariant(this);
        log.info("adding Variant Image: with ImageId={} and VariantId={}", image.getId(),  image.getVariant().getId());
    }

    public void addVariantAttribute(Attribute attribute, AttributeOption option) {
        if (this.variantAttributes == null) {
            this.variantAttributes = new ArrayList<>();
        }
        VariantAttribute va = VariantAttribute.builder()
                .variant(this)
                .attribute(attribute)
                .option(option)
                .build();
        variantAttributes.add(va);
        log.info("adding Variant Attribute: with variantId={} and attributeId={}, and optionId={}", va.getVariant().getId(), va.getAttribute().getId(), option.getId() );
    }

}
