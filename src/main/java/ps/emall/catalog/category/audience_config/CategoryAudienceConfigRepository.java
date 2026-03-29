package ps.emall.catalog.category.audience_config;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoryAudienceConfigRepository extends JpaRepository<CategoryAudienceConfig, Long>,
        JpaSpecificationExecutor<CategoryAudienceConfig> {
}
