package ps.emall.catalog.brand;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long>,
        JpaSpecificationExecutor<Brand> {

    boolean existsBySlug(String slug);
    Optional<Brand> findBySlug(String slug);
    long countByIsActive(boolean isActive);
}
