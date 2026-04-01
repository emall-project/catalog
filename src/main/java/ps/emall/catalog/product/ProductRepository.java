package ps.emall.catalog.product;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ps.emall.catalog.product.light.ProductLightRepository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product>,
        ProductLightRepository {
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

}

