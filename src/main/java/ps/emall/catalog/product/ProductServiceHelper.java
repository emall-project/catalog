package ps.emall.catalog.product;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ps.emall.catalog.brand.Brand;
import ps.emall.catalog.category.Category;
import ps.emall.catalog.category.CategoryExceptions;
import ps.emall.catalog.client.campaigns.ActiveOfferDto;
import ps.emall.catalog.client.campaigns.CampaignsClient;
import ps.emall.catalog.client.media_manager.FileDto;
import ps.emall.catalog.client.media_manager.FileLightDto;
import ps.emall.catalog.client.media_manager.MediaManagerClient;
import ps.emall.catalog.client.media_manager.MediaResponse;
import ps.emall.catalog.common.audience.AgeGroup;
import ps.emall.catalog.common.audience.TargetedAudience;
import ps.emall.catalog.product.light.ProductLightDto;
import ps.emall.catalog.product.product_media.ProductMediumDto;
import ps.emall.catalog.product.product_variant.ProductVariantDto;
import ps.emall.catalog.product.product_variant.ProductVariantExceptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductServiceHelper {
    private final ProductRepository productRepository;
    private final CampaignsClient campaignsClient;
    private final MediaManagerClient mediaManagerClient;


    boolean slugExistsInTheSameStore(String slug, Long storeId) {
        boolean result = productRepository.existsBySlugIgnoreCaseAndStoreId(slug, storeId);
        return result;
    }

    public static void validateSingleDefaultVariant(ProductDto dto) {
        long defaults =
                dto.getVariants().stream()
                        .filter(ProductVariantDto::getIsDefault)
                        .count();

        if (defaults > 1)
            throw ProductExceptions.multipleDefaultVariants();
        if(defaults == 0){
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

    public ProductDto injectDiscount(ProductDto dto) {
        if (dto == null || dto.getVariants() == null || dto.getVariants().isEmpty()) {
            return dto;
        }

        try {
            var response = campaignsClient.getActiveOfferForProduct(dto.getId());
            if (response == null || response.getData() == null) { // No active Offer for this product
                return dto;
            }

            ActiveOfferDto offer = response.getData();
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


    public ProductLightDto injectLightDiscount(ProductLightDto dto) {
        if (dto == null || dto.getDefaultVariantId() == null) {
            return dto;
        }

        try {
            //TODO : replace it with endpoint that's reutrn the minimal required data
            var response = campaignsClient.getActiveOfferForProduct(dto.getId());
            if (response == null || response.getData() == null) { // No active Offer for this product
                return dto;
            }

            ActiveOfferDto offer = response.getData();
            Map<Long, ActiveOfferDto.VariantDiscountDto> priceMap = offer.getVariantPrices()
                    .stream()
                    .collect(Collectors.toMap(
                            ActiveOfferDto.VariantDiscountDto::getVariantId,
                            vp -> vp
                    ));

            ActiveOfferDto.VariantDiscountDto discount = priceMap.get(dto.getDefaultVariantId());
            if (discount != null) {
                dto.setHasDiscount(true);
                dto.setDiscountedPrice(discount.getDiscountedPrice());
            } else {
                dto.setHasDiscount(false);
            }

        } catch (FeignException.NotFound e) {
            log.debug("No Active offer for productId={}", dto.getId());
        } catch (FeignException e) {
            log.debug("Campaigns Service unreachable for productId={}. " +
                    "Returning product without discount info. Status={}", dto.getId(), e.status());
        } catch (Exception e) {
            log.debug("Could Not Fetch discount for productId={}: {}", dto.getId(), e.getMessage());
        }

        return dto;
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

                // validate response not empty
                if (response == null || response.getData() == null) {
                    throw ProductVariantExceptions.mediumNotFound();
                }
                //inject medium File
                medium.setMediumFile(response.getData());

            } catch (FeignException e) {
                if (e.status() == 404) {
                    throw CategoryExceptions.imageNotFound();
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
            return null;
        }

        try {
            // TODO REPLACE WITH endpoint that's return FileLightDto
            MediaResponse<List<FileDto>> response = mediaManagerClient.getById(mediaIds);

            // validate response not empty
            if (response == null || response.getData() == null) {
                throw ProductVariantExceptions.mediumNotFound();
            }
            //inject medium File
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
}
