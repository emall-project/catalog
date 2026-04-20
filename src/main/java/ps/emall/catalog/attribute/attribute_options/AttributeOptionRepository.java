package ps.emall.catalog.attribute.attribute_options;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttributeOptionRepository extends JpaRepository<AttributeOption, Long>,JpaSpecificationExecutor<AttributeOption> {
    Optional<AttributeOption> findByAttribute_IdAndId(Long attributeId, Long id);
}
