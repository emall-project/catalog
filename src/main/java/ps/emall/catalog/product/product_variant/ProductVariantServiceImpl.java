package ps.emall.catalog.product.product_variant;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ps.emall.catalog.attribute.Attribute;
import ps.emall.catalog.attribute.AttributeExceptions;
import ps.emall.catalog.attribute.AttributeRepository;
import ps.emall.catalog.attribute.attribute_options.AttributeOption;
import ps.emall.catalog.attribute.attribute_options.AttributeOptionRepository;
import ps.emall.catalog.attribute.attribute_options.AttributeOptionsExceptions;
import ps.emall.catalog.category.CategoryExceptions;
import ps.emall.catalog.client.media_manager.FileDto;
import ps.emall.catalog.client.media_manager.MediaManagerClient;
import ps.emall.catalog.client.media_manager.MediaResponse;
import ps.emall.catalog.product.Product;
import ps.emall.catalog.product.ProductExceptions;
import ps.emall.catalog.product.ProductRepository;
import ps.emall.catalog.product.product_media.ProductMediumMapper;
import ps.emall.catalog.product.product_media.ProductMediumDto;
import ps.emall.catalog.product.product_variant.variant_attribute.VariantAttribute;
import ps.emall.catalog.product.product_variant.variant_attribute.VariantAttributeDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductVariantServiceImpl implements ProductVariantService {
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final AttributeRepository attributeRepository;
    private final AttributeOptionRepository attributeOptionRepository;
    private final MediaManagerClient mediaManagerClient;

    @Override
    public ProductVariantDto create(Long productId, ProductVariantDto dto) {
        // Fetch product or throw
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductExceptions::productNotFound);

        validateMedia(dto.getMedia());

        // Map DTO to entity
        ProductVariant variant = ProductVariantMapper.toEntity(dto, product);

        // Add Media
        if (dto.getMedia() != null) {
            dto.getMedia().stream()
                    .map(ProductMediumMapper::toEntity)
                    .forEach(variant::addMedium);
        }

        // Add attributes, checking duplicates
        if (dto.getAttributes() != null && !dto.getAttributes().isEmpty()) {
            Set<Long> attributeIds = new HashSet<>();
            for (VariantAttributeDto va : dto.getAttributes()) {
                Attribute attribute = attributeRepository.findById(va.getAttributeId())
                        .orElseThrow(AttributeExceptions::attributeNotFound);

                // Check for duplicate attributes
                if (!attributeIds.add(attribute.getId())) {
                    throw ProductVariantExceptions.duplicateAttribute();
                }

                AttributeOption option = attributeOptionRepository.findById(va.getOptionId())
                        .orElseThrow(AttributeOptionsExceptions::optionNotFound);

                variant.addVariantAttribute(attribute, option);
            }
        }

        // Save the variant
        ProductVariant saved = productVariantRepository.saveAndFlush(variant);

        // Log creation summary
        if (!saved.getVariantAttributes().isEmpty()) {
            VariantAttribute first = saved.getVariantAttributes().get(0);
            log.info("Created variant id={} for product id={} with optionId={} and attributeId={}",
                    saved.getId(),
                    productId,
                    first.getOption().getId(),
                    first.getAttribute().getId());
        }

        // Convert to DTO and inject media
        ProductVariantDto savedDto = ProductVariantMapper.toDto(saved);
        return injectMedia(savedDto);
    }

    private boolean validMediumType(String mimeType) {
        return mimeType != null && (mimeType.startsWith("image/") || mimeType.startsWith("video/"));
    }

    private void validateMedia(List<ProductMediumDto> media) {
        // Validate media limit
        if (media == null || media.isEmpty()) {
            throw ProductVariantExceptions.atLeastOneMediaRequired();
        } else if (media.size() > 10) {
            throw ProductVariantExceptions.mediaLimitExceeded();
        }

        // Validate media orders & existence
        Set<Integer> orders = new HashSet<>();
        media.forEach(medium -> {
            if (!orders.add(medium.getSortOrder())) {
                throw ProductVariantExceptions.duplicateMediumSort();
            }
            try {
                MediaResponse<FileDto> response = mediaManagerClient.getById(medium.getMediumId());
                // validate response not empty
                if (response == null || response.getData() == null) {
                    throw ProductVariantExceptions.mediumCouldNotBeValidated();
                }

                FileDto fileDto = response.getData();

                if (!validMediumType(fileDto.getMimeType())) {
                    throw ProductVariantExceptions.mediumTypeInvalid();
                }

            } catch (FeignException e) {
                log.info("Could not validate mediaId File from MediaManager mediaId={}, status={}, message={}",
                        medium.getMediumId(), e.status(), e.getMessage()
                );
                if (e.status() == 404) {
                    throw ProductVariantExceptions.mediumNotFound();
                }
                throw ProductVariantExceptions.mediumCouldNotBeValidated();
            }
        });
    }


    public ProductVariantDto injectMedia(ProductVariantDto dto) {
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
}
