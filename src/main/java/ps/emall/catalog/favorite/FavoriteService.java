package ps.emall.catalog.favorite;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ps.emall.catalog.common.page.PaginatedResponse;

import java.util.List;

public interface FavoriteService {

    PaginatedResponse<FavoriteDto> getMyFavorites(String user, Pageable pageable);

    List<FavoriteDto> getMyFavoritesList(String user);

    FavoriteDto findMyFavoriteById(String user, Long id);

    FavoriteDto create(String user, FavoriteDto dto);

    void deleteMyFavoriteById(String user, Long id);

    void deleteMyFavoriteByProductId(String user, Long productId);

    boolean isFavorite(String user, Long productId);

    long countMyFavorites(String user);
}