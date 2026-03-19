package ps.emall.catalog.attribute;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AttributeRepository extends JpaRepository<Attribute, Long>,
        JpaSpecificationExecutor<Attribute> {

    boolean existsBySlug(String slug);

    Optional<Attribute> findBySlug(String slug);
}
