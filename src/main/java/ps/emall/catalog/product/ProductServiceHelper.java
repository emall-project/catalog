package ps.emall.catalog.product;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import ps.emall.catalog.brand.Brand;
import ps.emall.catalog.category.Category;
import ps.emall.catalog.category.CategoryExceptions;
import ps.emall.catalog.client.campaigns.ActiveDiscountsRequest;
import ps.emall.catalog.client.campaigns.ActiveOfferDto;
import ps.emall.catalog.client.campaigns.ActiveProductDiscountDto;
import ps.emall.catalog.client.campaigns.CampaignsClient;
import ps.emall.catalog.client.campaigns.DiscountType;
import ps.emall.catalog.client.media_manager.FileDto;
import ps.emall.catalog.client.media_manager.FileLightDto;
import ps.emall.catalog.client.media_manager.MediaManagerClient;
import ps.emall.catalog.client.media_manager.MediaResponse;
import ps.emall.catalog.common.audience.AgeGroup;
import ps.emall.catalog.common.audience.TargetedAudience;
import ps.emall.catalog.job.ProductJob;
import ps.emall.catalog.product.light.ProductLightDto;
import ps.emall.catalog.product.light.ProductLightLookup;
import ps.emall.catalog.product.light.ProductLightMapper;
import ps.emall.catalog.product.light.ProductLightRow;
import ps.emall.catalog.product.product_media.ProductMediumDto;
import ps.emall.catalog.product.product_variant.ProductVariantDto;
import ps.emall.catalog.product.product_variant.ProductVariantExceptions;
import ps.emall.catalog.publisher.JobPublisher;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductServiceHelper {
    private final ProductRepository productRepository;
    private final CampaignsClient campaignsClient;
    private final MediaManagerClient mediaManagerClient;
    private final JobPublisher jobPublisher;

    boolean slugExistsInTheSameStore(String slug, Long storeId) {
        return productRepository.existsBySlugIgnoreCaseAndStoreId(slug, storeId);
    }

    public void validateTargetedAudience(TargetedAudience productTargetedAudience, TargetedAudience categoryTargetedAudience) {
        if (categoryTargetedAudience == TargetedAudience.ALL) return;
        if (productTargetedAudience == categoryTargetedAudience) return;
        throw ProductExceptions.invalidProductAudienceForCategory();
    }

    public void validateAgeGroup(AgeGroup productAgeGroup, AgeGroup categoryAgeGroup) {
        if (categoryAgeGroup == AgeGroup.ALL) return;
        if (productAgeGroup == categoryAgeGroup) return;
        throw ProductExceptions.invalidProductAgeGroupForCategory();
    }

    public void audienceValidation(ProductDto dto, Category category, Brand brand) {
        validateTargetedAudience(dto.getTargetedAudience(), category.getTargetedAudience());
        validateAgeGroup(dto.getAgeGroup(), category.getAgeGroup());

        validateTargetedAudience(dto.getTargetedAudience(), brand.getTargetedAudience());
        validateAgeGroup(dto.getAgeGroup(), brand.getAgeGroup());
    }

    public static void validateSingleDefaultVariant(ProductDto dto) {
        long defaults =
                dto.getVariants().stream()
                        .filter(ProductVariantDto::getIsDefault)
                        .count();

        if (defaults > 1)
            throw ProductExceptions.multipleDefaultVariants();
        if (defaults == 0) {
            throw ProductExceptions.defaultVariantRequired();
        }
    }

    public static void validateVariantsHaveAttributes(List<ProductVariantDto> variants) {
        for (ProductVariantDto variant : variants) {
            if (variant.getAttributes() == null || variant.getAttributes().isEmpty()) {
                throw ProductExceptions.variantShouldHasAttribute();
            }
        }
    }

    public ProductDto injectMedium(ProductDto dto) {
        for (ProductVariantDto v : dto.getVariants()) {
            injectMedium(v);
        }
        return dto;
    }

    public ProductVariantDto injectMedium(ProductVariantDto dto) {
        if (dto.getMedia() == null || dto.getMedia().isEmpty()) {
            return dto;
        }

        for (ProductMediumDto medium : dto.getMedia()) {
            try {
                MediaResponse<FileDto> response = mediaManagerClient.getById(medium.getMediumId());

                if (response == null || response.getData() == null) {
                    throw ProductVariantExceptions.mediumNotFound();
                }
                medium.setMediumFile(response.getData());

            } catch (FeignException e) {
                if (e.status() == 404) {
                    throw ProductVariantExceptions.mediumNotFound();
                }
                log.error("Could not fetch media File from MediaManager mediaId={}, status={}, message={}",
                        medium.getMediumId(), e.status(), e.getMessage()
                );
                throw e;
            }

        }
        return dto;
    }

    public Map<UUID, FileLightDto> getMedia(List<UUID> mediaIds) {
        if (mediaIds == null || mediaIds.isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            MediaResponse<List<FileDto>> response = mediaManagerClient.getByIds(mediaIds);

            if (response == null || response.getData() == null) {
                throw ProductVariantExceptions.mediumNotFound();
            }

            Map<UUID, FileLightDto> fileDtoMap = new HashMap<>();
            for (FileDto fileDto : response.getData()) {
                FileLightDto fileLightDto = new FileLightDto();
                fileLightDto.setId(fileDto.getId());
                fileLightDto.setSmallFileUrl(fileDto.getSmallFileUrl());
                fileDtoMap.put(fileDto.getId(), fileLightDto);
            }
            return fileDtoMap;
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw CategoryExceptions.imageNotFound();
            }
            log.error("Could not fetch media File from MediaManager status={}, message={}",
                    e.status(), e.getMessage()
            );
            throw e;
        }

    }

    public ProductDto injectDiscount(ProductDto dto) {
        log.info("Inject discount");
        if (dto == null || dto.getVariants() == null || dto.getVariants().isEmpty()) {
            return dto;
        }

        try {
            log.info("fetching discount");
            long startTime = System.currentTimeMillis();
            var response = campaignsClient.getActiveOfferForProduct(dto.getId());

            long endTime = System.currentTimeMillis();
            log.info("fetching discount: {} s", (endTime - startTime) / 1000);

            if (response == null || response.getData() == null) { // No active Offer for this product
                log.warn("fetching discount: {} s", (endTime - startTime) / 1000);
                return dto;
            }
            log.info("response: {}", response.getData());

            ActiveOfferDto offer = (ActiveOfferDto) response.getData();
            log.info("offer: {}, {}", offer.getDiscountType(), offer.getDiscountValue());
            Map<Long, ActiveOfferDto.VariantDiscountDto> priceMap = offer.getVariantPrices()
                    .stream()
                    .collect(Collectors.toMap(
                            ActiveOfferDto.VariantDiscountDto::getVariantId,
                            vp -> vp
                    ));

            dto.getVariants().forEach(variant -> {
                ActiveOfferDto.VariantDiscountDto discount = priceMap.get(variant.getId());
                if (discount != null) {
                    variant.setHasDiscount(true);
                    variant.setDiscountedPrice(discount.getDiscountedPrice());
                    variant.setDiscountType(discount.getDiscountType());
                    variant.setDiscountValue(discount.getDiscountValue());
                    variant.setOfferId(offer.getOfferId());
                } else {
                    variant.setHasDiscount(false);
                }
            });

        } catch (FeignException.NotFound e) {
            log.debug("No active offer for productId={}", dto.getId());
        } catch (FeignException e) {
            log.debug("Campaigns service unreachable for productId={}. " +
                    "Returning product without discount info. Status={}", dto.getId(), e.status());
        } catch (Exception e) {
            log.debug("Could not fetch discount for productId={}: {}", dto.getId(), e.getMessage());
        }

        return dto;
    }

    public Map<Long, ActiveProductDiscountDto> getActiveDiscounts(List<Long> productIds) {
        log.info("Getting active discount for productIds={}", productIds);
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> sanitizedIds = productIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (sanitizedIds.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            long startTime = System.currentTimeMillis();
            var response = campaignsClient.getActiveDiscountsForProducts(
                    ActiveDiscountsRequest.builder()
                            .productIds(sanitizedIds)
                            .build()
            );
            long endTime = System.currentTimeMillis();
            long requestTime = endTime - startTime;
            requestTime = requestTime / 1000;
            log.info("data featch from campaigns with time {}", requestTime);
            if (response == null || response.getData() == null || response.getData().isEmpty()) {
                return Collections.emptyMap();
            }

            log.info("data featch from campaigns with time {}", response.getData().size());
            return response.getData().stream()
                    .filter(Objects::nonNull)
                    .filter(dto -> dto.getProductId() != null)
                    .collect(Collectors.toMap(
                            ActiveProductDiscountDto::getProductId,
                            Function.identity(),
                            (oldValue, newValue) -> oldValue,
                            HashMap::new
                    ));

        } catch (FeignException.NotFound e) {
            log.debug("No active offers found for productIds={}", sanitizedIds);
            return Collections.emptyMap();
        } catch (FeignException e) {
            log.debug("Campaigns service unreachable for productIds={}. Returning products without discount info. Status={}",
                    sanitizedIds, e.status());
            return Collections.emptyMap();
        } catch (Exception e) {
            log.debug("Could not fetch discounts for productIds={}: {}", sanitizedIds, e.getMessage());
            return Collections.emptyMap();
        }
    }

    public ProductLightDto injectLightDiscount(
            ProductLightDto dto,
            Map<Long, ActiveProductDiscountDto> discountMap
    ) {
        if (dto == null || dto.getId() == null || dto.getDefaultVariantId() == null) {
            return dto;
        }

        if (discountMap == null || discountMap.isEmpty()) {
            dto.setHasDiscount(false);
            return dto;
        }

        ActiveProductDiscountDto discount = discountMap.get(dto.getId());
        if (discount == null) {
            dto.setHasDiscount(false);
            return dto;
        }

        dto.setHasDiscount(true);
        dto.setDiscountedPrice(
                computeDiscountedPrice(
                        discount.getDiscountType(),
                        discount.getDiscountValue(),
                        dto.getBasePrice()
                )
        );

        return dto;
    }

    private BigDecimal computeDiscountedPrice(
            DiscountType type,
            BigDecimal value,
            BigDecimal basePrice
    ) {
        if (type == null || value == null || basePrice == null) {
            return basePrice;
        }

        return switch (type) {
            case PERCENT -> basePrice
                    .multiply(BigDecimal.ONE.subtract(
                            value.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)))
                    .setScale(2, RoundingMode.HALF_UP);

            case FIXED_PRICE -> basePrice.subtract(value)
                    .setScale(2, RoundingMode.HALF_UP);
        };
    }

    public ProductLightLookup loadProductLightLookup(List<Long> productIds, boolean includeDiscounts) {
        if (productIds == null || productIds.isEmpty()) {
            return ProductLightLookup.empty();
        }

        List<ProductLightRow> rows = productRepository.findLightRowsByProductIds(productIds);

        Map<Long, ProductLightRow> rowMap = new HashMap<>();
        List<UUID> mediaIds = new ArrayList<>();

        for (ProductLightRow row : rows) {
            rowMap.put(row.getProductId(), row);
            if (row.getMediumId() != null) {
                mediaIds.add(row.getMediumId());
            }
        }

        Map<UUID, FileLightDto> mediaMap = getMedia(mediaIds);
        Map<Long, ActiveProductDiscountDto> discountMap = includeDiscounts
                ? getActiveDiscounts(productIds)
                : Collections.emptyMap();

        return new ProductLightLookup(rowMap, mediaMap, discountMap);
    }

    public ProductLightDto toProductLightDto(
            Long productId,
            ProductLightLookup lightLookup,
            boolean includeDiscounts
    ) {
        ProductLightDto dto = ProductLightMapper.toProductLightDto(
                productId,
                lightLookup.rowMap(),
                lightLookup.mediaMap()
        );

        if (!includeDiscounts) {
            return dto;
        }

        return injectLightDiscount(dto, lightLookup.discountMap());
    }

    public List<ProductLightDto> toActiveProductLightDtos(
            List<Long> productIds,
            ProductLightLookup lightLookup,
            boolean includeDiscounts
    ) {
        return productIds.stream()
                .map(productId -> toProductLightDto(productId, lightLookup, includeDiscounts))
                .filter(dto -> dto.getName() != null)
                .filter(dto -> Boolean.TRUE.equals(dto.getIsActive()))
                .toList();
    }

    public List<Long> sanitizeProductIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return List.of();
        }

        return productIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    public List<ProductLightDto> getFallbackSimilarProducts(Product product, Integer topK) {
        int limit = topK == null || topK < 1 ? 8 : topK;
        List<Long> productIds = productRepository.findFallbackSimilarProducts(
                        product.getId(),
                        product.getMallId(),
                        product.getCategory().getId(),
                        product.getBrand().getId(),
                        PageRequest.of(0, limit)
                )
                .stream()
                .map(Product::getId)
                .toList();

        ProductLightLookup lightLookup = loadProductLightLookup(productIds, true);
        return toActiveProductLightDtos(productIds, lightLookup, true);
    }

    public List<ProductLightDto> loadActiveProductLightDtos(List<Long> productIds) {
        List<Long> sanitizedIds = sanitizeProductIds(productIds);
        if (sanitizedIds.isEmpty()) {
            return List.of();
        }

        ProductLightLookup lightLookup = loadProductLightLookup(sanitizedIds, true);
        return toActiveProductLightDtos(sanitizedIds, lightLookup, true);
    }

    public void publishCreatedJob(Product product) {
        ProductJob productJob = ProductMapper.toProductJob(product);
        jobPublisher.publishProductCreatedJob(productJob);
    }

    public void publishUpdatedJob(Product product) {
        ProductJob productJob = ProductMapper.toProductJob(product);
        jobPublisher.publishProductUpdatedJob(productJob);
    }

    public void publishDeletedJob(Long productId) {
        jobPublisher.publishProductDeletedJob(productId);
    }
}
