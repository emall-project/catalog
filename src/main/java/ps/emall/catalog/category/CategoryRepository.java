package ps.emall.catalog.category;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>,
        JpaSpecificationExecutor<Category> {

    List<Category> findByParentId(Long parentId);

    boolean existsBySlug(String slug);

    Optional<Category> findBySlug(String slug);

    Optional<Category> findBySlugAndIsActiveTrue(String slug);

    Optional<Category> findByIdAndIsActiveTrue(Long id);

    long countByParentId(Long parentId);

    List<Category> findByImageId(UUID imageId);

    List<Category> findByDepthLevelInAndIsActiveOrderByDepthLevelAsc(Collection<Integer> depthLevels, Boolean isActive);
}

