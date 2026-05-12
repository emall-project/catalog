package ps.emall.catalog.category.audience_config;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface CategoryAudienceConfigRepository extends JpaRepository<CategoryAudienceConfig, Long>,
        JpaSpecificationExecutor<CategoryAudienceConfig> {
    List<CategoryAudienceConfig> findByImageId(UUID imageId);

    List<CategoryAudienceConfig> findByCategory_IdIn(Collection<Long> categoryIds);
}
