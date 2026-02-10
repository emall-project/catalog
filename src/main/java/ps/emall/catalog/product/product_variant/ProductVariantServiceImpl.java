package ps.emall.catalog.product.product_variant;

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
import ps.emall.catalog.product.Product;
import ps.emall.catalog.product.ProductDto;
import ps.emall.catalog.product.ProductExceptions;
import ps.emall.catalog.product.ProductRepository;
import ps.emall.catalog.product.product_image.ProductImageMapper;
import ps.emall.catalog.product.product_variant.variant_attribute.VariantAttributeDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
    public class ProductVariantServiceImpl implements ProductVariantService {
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final AttributeRepository attributeRepository;
    private final AttributeOptionRepository attributeOptionRepository;

    public ProductVariantDto create(ProductVariantDto dto, Product product) {


        ProductVariant productVariant = ProductVariant.builder()
                .name(dto.getName())
                .basePrice(dto.getBasePrice())
                .isDefault(dto.isDefault())
                .product(product)
                .build();

        dto.getImages()
                .stream()
                .map(ProductImageMapper::toEntity)
                .forEach(productVariant::addImage);

        for (VariantAttributeDto vaDto : dto.getAttributes()) {

            Attribute attribute = attributeRepository.findById(vaDto.getAttributeId())
                    .orElseThrow(AttributeExceptions::attributeNotFound);

            AttributeOption option = attributeOptionRepository.findById(vaDto.getOptionId())
                    .orElseThrow(AttributeExceptions::attributeNotFound);

            productVariant.addVariantAttribute(attribute, option);
        }

        ProductVariant saved = productVariantRepository.save(productVariant);
        log.info("Created product variant with id={}", saved.getId());
        log.info("Created product variant with VariantAttributes={}", saved.getVariantAttributes().getFirst().getAttribute().getId());
        return ProductVariantMapper.toDto(saved);
    }


        @Override
        public ProductVariantDto create(Long productId, ProductVariantDto dto) {

            Product product = productRepository.findById(productId)
                    .orElseThrow(ProductExceptions::productNotFound);

//            if (dto.isDefault()) {
//                productVariantRepository.clearDefaultForProduct(productId);
//            }

            ProductVariant variant = ProductVariant.builder()
                    .name(dto.getName())
                    .basePrice(dto.getBasePrice())
                    .isDefault(dto.isDefault())
                    .product(product)
                    .build();


            variant = productVariantRepository.saveAndFlush(variant);

            // images
            if (dto.getImages() != null) {
                dto.getImages()
                        .stream()
                        .map(ProductImageMapper::toEntity)
                        .forEach(variant::addImage);
            }

            // attributes
            if (dto.getAttributes() != null) {
                for (VariantAttributeDto va : dto.getAttributes()) {

                    log.info("looking for attribute with id={}", va.getAttributeId());
                    Attribute attribute = attributeRepository.findById(va.getAttributeId())
                            .orElseThrow(AttributeExceptions::attributeNotFound);

                    log.info("looking for attribute Option with id={}", va.getOptionId());
                    AttributeOption option = attributeOptionRepository.findById(va.getOptionId())
                            .orElseThrow(AttributeOptionsExceptions::optionNotFound);

                    variant.addVariantAttribute(attribute, option);
                }
            }

            ProductVariant saved = productVariantRepository.save(variant);


            log.info("Created variant id={} for product id={} with option={} ", saved.getId(), productId, saved.getVariantAttributes().getFirst().getOption().getId(), saved.getVariantAttributes().getFirst().getAttribute());

            ProductVariantDto savedDto = ProductVariantMapper.toDto(saved);
            return savedDto;
        }



    @Override
    public ProductVariantDto update(
            Long productId,
            Long variantId,
            ProductVariantDto dto
    ) {

        log.info("Updating product variant with id={} and product id={}", variantId, productId);
        ProductVariant variant = productVariantRepository
                .findById(variantId)
                .orElseThrow(ProductVariantExceptions::variantNotFound);
        log.info("==========================");

        // default variant rule
//        if (dto.isDefault()) {
//            productVariantRepository.clearDefaultForProduct(productId);
//            variant.setIsDefault(true);
//        } else {
//            variant.setIsDefault(false);
//        }

        variant.setName(dto.getName());
        variant.setBasePrice(dto.getBasePrice());

        variant = productVariantRepository.saveAndFlush(variant);
        log.info("flush Save for variant with id={}", variant.getId());
        /* ---------- IMAGES ---------- */
        variant.getImages().clear();
        if (dto.getImages() != null) {
            dto.getImages()
                    .stream()
                    .map(ProductImageMapper::toEntity)
                    .forEach(variant::addImage);
        }

        variant = productVariantRepository.saveAndFlush(variant);

        /* ---------- ATTRIBUTES ---------- */
        variant.getVariantAttributes().clear();
        if (dto.getAttributes() != null) {
            for (VariantAttributeDto va : dto.getAttributes()) {

                Attribute attribute = attributeRepository.findById(va.getAttributeId())
                        .orElseThrow(AttributeExceptions::attributeNotFound);

                AttributeOption option = attributeOptionRepository.findById(va.getOptionId())
                        .orElseThrow(AttributeOptionsExceptions::optionNotFound);

                variant.addVariantAttribute(attribute, option);
            }
        }

        ProductVariant saved = productVariantRepository.save(variant);

        log.info(
                "Updated variant id={} for product id={}",
                saved.getId(),
                productId
        );

        return ProductVariantMapper.toDto(saved);
    }


    @Override
    @Transactional(readOnly = true)
    public ProductVariantDto getById(Long productId, Long variantId) {

        ProductVariant variant = productVariantRepository
                .findByIdAndProductId(variantId, productId)
                .orElseThrow(ProductVariantExceptions::variantNotFound);

        return ProductVariantMapper.toDto(variant);
    }


    @Override
    @Transactional(readOnly = true)
    public List<ProductVariantDto> getByProductId(Long productId) {

        if (!productRepository.existsById(productId)) {
            throw ProductExceptions.productNotFound();
        }

        return productVariantRepository.findByProductId(productId)
                .stream()
                .map(ProductVariantMapper::toDto)
                .toList();
    }

    @Override
    public void delete(Long productId, Long variantId) {

        ProductVariant variant = productVariantRepository
                .findByIdAndProductId(variantId, productId)
                .orElseThrow(ProductVariantExceptions::variantNotFound);

        productVariantRepository.delete(variant);

        log.info(
                "Deleted variant id={} from product id={}",
                variantId,
                productId
        );
    }

}
