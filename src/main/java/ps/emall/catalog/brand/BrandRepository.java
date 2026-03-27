package ps.emall.catalog.brand;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long>,
        JpaSpecificationExecutor<Brand> {

    boolean existsBySlug(String slug);
    Optional<Brand> findBySlug(String slug);
    long countByIsActive(boolean isActive);

    @Modifying
    @Query("UPDATE Brand b SET b.isActive = FALSE WHERE b.id = :id")
    void deactivateById(Long id);

    @Modifying
    @Query("UPDATE Brand b SET b.isActive = true WHERE b.id = :id")
    void activateById(Long id);

    List<Brand> findByImageId(UUID imageId);
}
