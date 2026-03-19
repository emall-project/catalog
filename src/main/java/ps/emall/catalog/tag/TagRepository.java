package ps.emall.catalog.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository
        extends JpaRepository<Tag, Long>, JpaSpecificationExecutor<Tag> {

    boolean existsByNameIgnoreCase(String name);

    Optional<Tag> findByNameIgnoreCase(String name);

    Optional<Tag> findByName(String name);
}
