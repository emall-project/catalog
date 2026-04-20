package ps.emall.catalog.product.product_variant.variant_attribute;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariantAttributeRepository extends JpaRepository<VariantAttribute, Long> {
    boolean existsByVariant_Id(Long variantId);
    boolean existsByAttribute_Id(Long attributeId);
}
