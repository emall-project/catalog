package ps.emall.catalog.attribute.attribute_options;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeOptionRepository extends JpaRepository<AttributeOption, Long>,
        JpaSpecificationExecutor<AttributeOption> {
}
