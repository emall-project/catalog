package ps.emall.catalog.product.product_variant.variant_attribute;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VariantAttributeRepository extends JpaRepository<VariantAttribute, Long> {
    boolean existsByVariant_Id(Long variantId);
    boolean existsByAttribute_Id(Long attributeId);

    @Query("""
            SELECT COUNT(DISTINCT variant.product.id)
            FROM VariantAttribute variantAttribute
            JOIN variantAttribute.variant variant
            WHERE variantAttribute.attribute.id = :attributeId
            """)
    long countDistinctProductsByAttributeId(Long attributeId);
}
