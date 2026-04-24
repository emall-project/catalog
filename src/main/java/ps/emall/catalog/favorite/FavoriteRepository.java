package ps.emall.catalog.favorite;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long>,
        JpaSpecificationExecutor<Favorite> {

    boolean existsByUserAndProduct_Id(String user, Long productId);

    Optional<Favorite> findByUserAndProduct_Id(String user, Long productId);

    long countByUser(String user);

    List<Favorite> findByUser(String user);

    Page<Favorite> findByUser(String user, Pageable pageable);

}