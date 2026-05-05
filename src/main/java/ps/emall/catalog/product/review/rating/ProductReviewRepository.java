package ps.emall.catalog.product.review.rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    List<ProductReview> findByProduct_Id(Long productId);

    Optional<ProductReview> findByProduct_IdAndUserId(Long productId, Long userId);

    boolean existsByProduct_IdAndUserId(Long productId, Long userId);

    long countByProduct_Id(Long productId);

    long countByProduct_StoreId(Long storeId);

    List<ProductReview> findTop5ByProduct_StoreIdOrderByCreatedAtDesc(Long storeId);

    @Query("SELECT AVG(r.rating) FROM ProductReview r WHERE r.product.id = :productId")
    Double findAverageRatingByProductId(@Param("productId") Long productId);

    @Query("SELECT AVG(r.rating) FROM ProductReview r WHERE r.product.storeId = :storeId")
    Double findAverageRatingByStoreId(@Param("storeId") Long storeId);

    @Query("SELECT COUNT(DISTINCT r.product.id) FROM ProductReview r WHERE r.product.storeId = :storeId")
    long countReviewedProductsByStoreId(@Param("storeId") Long storeId);
}
