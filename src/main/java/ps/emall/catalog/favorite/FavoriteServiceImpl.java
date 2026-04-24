package ps.emall.catalog.favorite;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ps.emall.catalog.common.page.PaginatedResponse;
import ps.emall.catalog.product.Product;
import ps.emall.catalog.product.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<FavoriteDto> getMyFavorites(String user, Pageable pageable) {

        Page<FavoriteDto> page = favoriteRepository.findByUser(user, pageable)
                .map(FavoriteMapper::toDto);

        return PaginatedResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavoriteDto> getMyFavoritesList(String user) {

        return favoriteRepository.findByUser(user)
                .stream()
                .map(FavoriteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FavoriteDto findMyFavoriteById(String user, Long id) {
        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(FavoriteExceptions::favoriteNotFound);

        if (!favorite.getUser().equals(user)) {
            throw FavoriteExceptions.favoriteAccessDenied();
        }

        return FavoriteMapper.toDto(favorite);
    }

    @Override
    public FavoriteDto create(String user, FavoriteDto dto) {
        if (favoriteRepository.existsByUserAndProduct_Id(user, dto.getProductId())) {
            throw FavoriteExceptions.alreadyFavorited();
        }

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(FavoriteExceptions::productNotFound);

        Favorite favorite = Favorite.builder()
                .user(user)
                .product(product)
                .build();

        Favorite saved = favoriteRepository.save(favorite);
        return FavoriteMapper.toDto(saved);
    }

    @Override
    public void deleteMyFavoriteById(String user, Long id) {
        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(FavoriteExceptions::favoriteNotFound);

        if (!favorite.getUser().equals(user)) {
            throw FavoriteExceptions.favoriteAccessDenied();
        }

        favoriteRepository.delete(favorite);
    }

    @Override
    public void deleteMyFavoriteByProductId(String user, Long productId) {
        Favorite favorite = favoriteRepository.findByUserAndProduct_Id(user, productId)
                .orElseThrow(FavoriteExceptions::favoriteNotFound);

        favoriteRepository.delete(favorite);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavorite(String user, Long productId) {
        return favoriteRepository.existsByUserAndProduct_Id(user, productId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countMyFavorites(String user) {
        return favoriteRepository.countByUser(user);
    }

    private Specification<Favorite> userSpec(String user) {
        return (root, query, cb) -> cb.equal(root.get("user"), user);
    }
}