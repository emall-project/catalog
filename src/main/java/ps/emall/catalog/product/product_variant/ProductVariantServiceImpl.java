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
import ps.emall.catalog.client.media_manager.FileDto;
import ps.emall.catalog.client.media_manager.MediaManagerClient;
import ps.emall.catalog.client.media_manager.MediaResponse;
import ps.emall.catalog.product.Product;
import ps.emall.catalog.product.ProductExceptions;
import ps.emall.catalog.product.ProductRepository;
import ps.emall.catalog.product.ProductServiceHelper;
import ps.emall.catalog.product.product_media.ProductMediumDto;
import ps.emall.catalog.product.product_media.ProductMediumMapper;
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
    private final ProductServiceHelper productServiceHelper;

    @Override
    public ProductVariantDto add(Long storeId, Long productId, ProductVariantDto dto) {
        Product product = productRepository.findByStoreIdAndId(storeId, productId)
                .orElseThrow(ProductExceptions::productNotFound);

        validateMedia(dto.getMedia());

        ProductVariant variant = ProductVariantMapper.toEntity(dto, product);

        loadAndValidateMedia(dto, variant);
        loadAndValidateAttribute(dto, variant);

        ProductVariant saved = productVariantRepository.saveAndFlush(variant);

        if (saved.getIsDefault()) {
            productVariantRepository.clearDefaultForProduct(variant.getProduct().getId());
            productRepository.updateDefaultVariant(variant.getProduct().getId(), variant.getId());
        }

        ProductVariantDto savedDto = ProductVariantMapper.toDto(saved);
        return productServiceHelper.injectMedium(savedDto);
    }

    @Override
    public ProductVariantDto create(Long productId, ProductVariantDto dto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductExceptions::productNotFound);

        validateMedia(dto.getMedia());

        ProductVariant variant = ProductVariantMapper.toEntity(dto, product);

        loadAndValidateMedia(dto, variant);
        loadAndValidateAttribute(dto, variant);

        ProductVariant saved = productVariantRepository.saveAndFlush(variant);

        ProductVariantDto savedDto = ProductVariantMapper.toDto(saved);
        return productServiceHelper.injectMedium(savedDto);
    }

    @Override
    public ProductVariantDto update(Long storeId, Long productId, ProductVariantDto dto) {
        ProductVariant existing = productVariantRepository.findByStoreIdAndProductIdAndId(storeId, productId, dto.getId()).orElseThrow(
                ProductVariantExceptions::variantNotFound
        );

        validateMedia(dto.getMedia());

        existing.setName(dto.getName());
        existing.setBasePrice(dto.getBasePrice());

        existing.getMedia().clear();
        loadAndValidateMedia(dto, existing);

        existing.getVariantAttributes().clear();
        loadAndValidateAttribute(dto, existing);

        loadAndUpdateDefaultVariant(dto, existing);

        ProductVariant saved = productVariantRepository.save(existing);

        ProductVariantDto savedDto = ProductVariantMapper.toDto(saved);
        return productServiceHelper.injectMedium(savedDto);
    }

    @Override
    public void delete(Long storeId, Long productId, Long id) {
        ProductVariant variant = productVariantRepository.findByStoreIdAndProductIdAndId(storeId, productId, id).orElseThrow(
                ProductVariantExceptions::variantNotFound
        );
        if (variant.getIsDefault().equals(Boolean.TRUE)) {
            throw ProductVariantExceptions.defaultVariantDeletionNotAllowed();
        }
        productVariantRepository.delete(variant);
    }

    private boolean validMediumType(String mimeType) {
        return mimeType != null && (mimeType.startsWith("image/") || mimeType.startsWith("video/"));
    }

    private void validateMedia(List<ProductMediumDto> media) {
        if (media == null || media.isEmpty()) {
            throw ProductVariantExceptions.atLeastOneMediaRequired();
        } else if (media.size() > 10) {
            throw ProductVariantExceptions.mediaLimitExceeded();
        }

        Set<Integer> orders = new HashSet<>();
        media.forEach(medium -> {
            if (!orders.add(medium.getSortOrder())) {
                throw ProductVariantExceptions.duplicateMediumSort();
            }
            try {
                MediaResponse<FileDto> response = mediaManagerClient.getById(medium.getMediumId());
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

    private void loadAndValidateAttribute(ProductVariantDto dto, ProductVariant variant) {
        if (dto.getAttributes() != null && !dto.getAttributes().isEmpty()) {
            Set<Long> attributeIds = new HashSet<>();
            for (VariantAttributeDto va : dto.getAttributes()) {

                Attribute attribute = attributeRepository.findById(va.getAttributeId())
                        .orElseThrow(AttributeExceptions::attributeNotFound);

                if (!attributeIds.add(attribute.getId())) {
                    throw ProductVariantExceptions.duplicateAttribute();
                }

                AttributeOption option = attributeOptionRepository.findByAttribute_IdAndId(va.getAttributeId(), va.getOptionId())
                        .orElseThrow(AttributeOptionsExceptions::optionNotFound);

                variant.addVariantAttribute(attribute, option);
            }
        }
    }

    private void loadAndValidateMedia(ProductVariantDto dto, ProductVariant variant) {
        dto.getMedia().stream()
                .map(ProductMediumMapper::toEntity)
                .forEach(variant::addMedium);
    }

    private void loadAndUpdateDefaultVariant(ProductVariantDto dto, ProductVariant variant) {
        if (dto.getIsDefault().equals(variant.getIsDefault())) return;

        if (dto.getIsDefault().equals(Boolean.FALSE)) {
            throw ProductExceptions.defaultVariantRequired();
        }

        productVariantRepository.clearDefaultForProduct(variant.getProduct().getId());
        productRepository.updateDefaultVariant(variant.getProduct().getId(), variant.getId());

        variant.setIsDefault(dto.getIsDefault());
    }
}
