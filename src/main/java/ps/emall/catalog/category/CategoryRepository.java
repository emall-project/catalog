package ps.emall.catalog.category;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>,
        JpaSpecificationExecutor<Category> {

    List<Category> findByParentIsNull();
    List<Category> findByParentId(Long parentId);
    boolean existsBySlug(String slug);
    Optional<Category> findBySlug(String slug);
    long countByParentId(Long parentId);

}

