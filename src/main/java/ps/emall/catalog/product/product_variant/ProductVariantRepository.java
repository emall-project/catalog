package ps.emall.catalog.product.product_variant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductVariantRepository
        extends JpaRepository<ProductVariant, Long> {

    List<ProductVariant> findByProductId(Long productId);

    Optional<ProductVariant> findByIdAndProductId(Long id, Long productId);

    @Modifying
    @Query("""
        UPDATE ProductVariant v
        SET v.isDefault = false
        WHERE v.product.id = :productId
    """)
    void clearDefaultForProduct(Long productId);

    void deleteByProductId(Long productId);

    @Query("""
        SELECT v 
        FROM ProductVariant v
        JOIN v.media m
        WHERE m.mediumId = :mediumId
    """)
    List<ProductVariant> findByMediumId(UUID mediumId);

}
