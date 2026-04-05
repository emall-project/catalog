package ps.emall.catalog.product.review.rating;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ps.emall.catalog.client.accounts.AccountsClient;
import ps.emall.catalog.common.exception.EMallsException;
import ps.emall.catalog.product.Product;
import ps.emall.catalog.product.ProductExceptions;
import ps.emall.catalog.product.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductReviewServiceImpl implements ProductReviewService {

    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final AccountsClient accountsClient;

    @Override
    @Transactional(readOnly = true)
    public List<ProductReviewDto> getByProductId(Long productId) {
        verifyProductExists(productId);

        Double avg = reviewRepository.findAverageRatingByProductId(productId);
        long total = reviewRepository.countByProduct_Id(productId);

        return reviewRepository.findByProduct_Id(productId).stream()
                .map(r -> {
                    ProductReviewDto dto = ProductReviewMapper.toDto(r);
                    dto.setAverageRating(avg);
                    dto.setTotalReviews(total);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductReviewDto getByProductIdAndUserId(Long productId, Long userId) {
        ProductReview review = reviewRepository
                .findByProduct_IdAndUserId(productId, userId)
                .orElseThrow(ReviewExceptions::reviewNotFound);

        Double avg = reviewRepository.findAverageRatingByProductId(productId);
        long total = reviewRepository.countByProduct_Id(productId);

        ProductReviewDto dto = ProductReviewMapper.toDto(review);
        dto.setAverageRating(avg);
        dto.setTotalReviews(total);
        return dto;
    }

    @Override
    @Transactional
    public ProductReviewDto create(Long productId, ProductReviewDto dto) {
        Product product = verifyProductExists(productId);

        // TODO: replace with security context after Spring Security is implemented
        validateUserExists(dto.getUserId());

        if (reviewRepository.existsByProduct_IdAndUserId(productId, dto.getUserId())) {
            throw ReviewExceptions.reviewAlreadyExists();
        }

        ProductReview review = ProductReviewMapper.toEntity(dto, product);
        ProductReview saved = reviewRepository.save(review);

        log.info("Review created: id={}, productId={}, userId={}, rating={}",
                saved.getReviewId(), productId, dto.getUserId(), dto.getRating());

        Double avg = reviewRepository.findAverageRatingByProductId(productId);
        long total = reviewRepository.countByProduct_Id(productId);

        ProductReviewDto result = ProductReviewMapper.toDto(saved);
        result.setAverageRating(avg);
        result.setTotalReviews(total);
        return result;
    }

    @Override
    @Transactional
    public ProductReviewDto update(Long productId, Long userId, ProductReviewDto dto) {
        ProductReview existing = reviewRepository
                .findByProduct_IdAndUserId(productId, userId)
                .orElseThrow(ReviewExceptions::reviewNotFound);

        // TODO: replace with security context after Spring Security is implemented
        if (!existing.getUserId().equals(userId)) {
            throw ReviewExceptions.cannotUpdateOtherUserReview();
        }

        existing.setRating(dto.getRating());
        ProductReview saved = reviewRepository.save(existing);

        log.info("Review updated: id={}, productId={}, userId={}, newRating={}",
                saved.getReviewId(), productId, userId, dto.getRating());

        Double avg = reviewRepository.findAverageRatingByProductId(productId);
        long total = reviewRepository.countByProduct_Id(productId);

        ProductReviewDto result = ProductReviewMapper.toDto(saved);
        result.setAverageRating(avg);
        result.setTotalReviews(total);
        return result;
    }

    @Override
    @Transactional
    public void delete(Long productId, Long userId) {
        ProductReview review = reviewRepository
                .findByProduct_IdAndUserId(productId, userId)
                .orElseThrow(ReviewExceptions::reviewNotFound);

        // TODO: replace with security context after Spring Security is implemented
        if (!review.getUserId().equals(userId)) {
            throw ReviewExceptions.cannotUpdateOtherUserReview();
        }

        reviewRepository.delete(review);
        log.info("Review deleted: productId={}, userId={}", productId, userId);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Product verifyProductExists(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(ProductExceptions::productNotFound);
    }

    private void validateUserExists(Long userId) {
        try {
            var response = accountsClient.getUserById(userId);
            var user = response != null ? response.getData() : null;

            if (user == null) throw ReviewExceptions.userNotFound();
            if (Boolean.FALSE.equals(user.getIsActive())) throw ReviewExceptions.userNotFound();

        } catch (FeignException.NotFound e) {
            log.warn("User {} not found in accounts service", userId);
            throw ReviewExceptions.userNotFound();
        } catch (FeignException e) {
            log.warn("Accounts service unreachable for userId={}, status={}. Allowing request.",
                    userId, e.status());
        } catch (EMallsException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Unexpected error validating userId={}. Allowing request.", userId);
        }
    }
}