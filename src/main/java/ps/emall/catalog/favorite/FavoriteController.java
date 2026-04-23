package ps.emall.catalog.favorite;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ps.emall.catalog.common.page.PaginatedResponse;
import ps.emall.catalog.common.response.EMallsResponseEntity;
import ps.emall.catalog.common.validation.OnCreate;
import ps.emall.catalog.security.SecurityContextUtilBean;
//import ps.emall.catalog.security.SecurityContextUtilBean;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
@PreAuthorize("@auth.isCustomer()")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final SecurityContextUtilBean auth;

    @GetMapping
    public EMallsResponseEntity<PaginatedResponse<FavoriteDto>> getMyFavorites(Pageable pageable) {
        String user = auth.getCurrentUsername();
        PaginatedResponse<FavoriteDto> page = favoriteService.getMyFavorites(user, pageable);
        return EMallsResponseEntity.ok(page);
    }

    @GetMapping("/all")
    public EMallsResponseEntity<List<FavoriteDto>> getMyFavoritesList() {
        String user = auth.getCurrentUsername();
        List<FavoriteDto> favorites = favoriteService.getMyFavoritesList(user);
        return EMallsResponseEntity.ok(favorites);
    }

    @GetMapping("/{id}")
    public EMallsResponseEntity<FavoriteDto> getMyFavoriteById(@PathVariable Long id) {
        String user = auth.getCurrentUsername();
        FavoriteDto dto = favoriteService.findMyFavoriteById(user, id);
        return EMallsResponseEntity.ok(dto);
    }

    @GetMapping("/product/{productId}/exists")
    public EMallsResponseEntity<Map<String, Boolean>> isFavorite(@PathVariable Long productId) {
        String user = auth.getCurrentUsername();
        boolean exists = favoriteService.isFavorite(user, productId);
        return EMallsResponseEntity.ok(Map.of("favorite", exists));
    }

    @GetMapping("/count")
    public EMallsResponseEntity<Map<String, Long>> countMyFavorites() {
        String user = auth.getCurrentUsername();
        long count = favoriteService.countMyFavorites(user);
        return EMallsResponseEntity.ok(Map.of("count", count));
    }

    @PostMapping
    public EMallsResponseEntity<FavoriteDto> create(
            @Validated(OnCreate.class) @RequestBody FavoriteDto dto
    ) {
        String user = auth.getCurrentUsername();
        FavoriteDto created = favoriteService.create(user, dto);
        return EMallsResponseEntity.created(created);
    }

    @DeleteMapping("/{id}")
    public EMallsResponseEntity<Void> deleteById(@PathVariable Long id) {
        String user = auth.getCurrentUsername();
        favoriteService.deleteMyFavoriteById(user, id);
        return EMallsResponseEntity.noContent(null);
    }

    @DeleteMapping("/product/{productId}")
    public EMallsResponseEntity<Void> deleteByProductId(@PathVariable Long productId) {
        String user = auth.getCurrentUsername();
        favoriteService.deleteMyFavoriteByProductId(user, productId);
        return EMallsResponseEntity.noContent(null);
    }
}