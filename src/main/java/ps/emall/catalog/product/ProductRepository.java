package ps.emall.catalog.product;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ps.emall.catalog.product.light.ProductLightRepository;
import ps.emall.catalog.product.summary.ProductSummaryRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product>,
        ProductLightRepository,
        ProductSummaryRepository {
    Optional<Product> findBySlug(String slug);

    boolean existsBySlugAndStoreId(String slug, Long storeId);

    long countByCategory_Id(Long categoryId);

    long countByBrand_Id(Long brandId);

    @Modifying
    @Query("UPDATE Product p SET p.isActive = false WHERE p.brand.id = :brandId")
    void deactivateByBrandId(Long brandId);

    @Modifying
    @Query("UPDATE Product p SET p.isActive = false WHERE p.category.id = :categoryId")
    void deactivateByCategoryId(Long categoryId);

    @Modifying
    @Query("UPDATE Product p SET p.isActive = false WHERE p.storeId = :storeId")
    void deactivateByStoreId(Long storeId);

    boolean existsByTags_Id(Long tagsId);

    boolean existsBySlugIgnoreCaseAndStoreId(String slug, Long storeId);

    @Override
    long count(Specification<Product> spec);


    List<Long> findIdsBySpecification(Specification<Product> spec);

    Optional<Product> findByStoreIdAndSlug(Long storeId, String slug);

    Optional<Product> findByStoreIdAndId(Long storeId, Long id);

    @Modifying
    @Query("""
                UPDATE Product p
                SET p.isActive = true
                WHERE p.category.id = :categoryId
            """)
    void activateProductsByCategoryId(Long categoryId);

    @Modifying
    @Query("""
                UPDATE Product p
                SET p.isActive = false
                WHERE p.category.id = :categoryId
            """)
    void deactivateProductsByCategoryId(Long categoryId);

    @Modifying
    @Query("""
                UPDATE Product p
                SET p.isActive = true
                WHERE p.brand.id = :brandId
            """)
    void activateProductsByBrandId(Long brandId);

    @Modifying
    @Query("""
                UPDATE Product p
                SET p.isActive = false
                WHERE p.brand.id = :brandId
            """)
    void deactivateProductsByBrandId(Long brandId);

    @Modifying
    @Query("""
                UPDATE Product p
                SET p.defaultVariant.id = :defaultVariantId
                WHERE p.id = :id
            """)
    void updateDefaultVariant(Long id, Long defaultVariantId);


}

