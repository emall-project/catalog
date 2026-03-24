package ps.emall.catalog.product;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ps.emall.catalog.brand.Brand;
import ps.emall.catalog.brand.BrandRepository;
import ps.emall.catalog.category.Category;
import ps.emall.catalog.category.CategoryExceptions;
import ps.emall.catalog.category.CategoryRepository;
import ps.emall.catalog.client.campaigns.ActiveOfferDto;
import ps.emall.catalog.client.campaigns.CampaignsClient;
import ps.emall.catalog.common.page.PaginatedResponse;
import ps.emall.catalog.product.product_variant.ProductVariant;
import ps.emall.catalog.product.product_variant.ProductVariantDto;
import ps.emall.catalog.product.product_variant.ProductVariantRepository;
import ps.emall.catalog.product.product_variant.ProductVariantService;
import ps.emall.catalog.tag.Tag;
import ps.emall.catalog.tag.TagService;
import ps.emall.catalog.tag.TagMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final TagService tagService;
    private final ProductVariantService productVariantService;
    private final CampaignsClient campaignsClient;

    @Override
    public PaginatedResponse<ProductDto> getAll(ProductSpec spec, Pageable pageable) {
        Page<ProductDto> page = productRepository.findAll(spec, pageable)
                .map(ProductMapper::toDto)
                .map(this::injectMedia)
                .map(this::injectDiscount);
        return PaginatedResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getAllProductList(ProductSpec spec) {
        return productRepository.findAll(spec)
                .stream()
                .map(ProductMapper::toDto)
                .map(this::injectDiscount)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(ProductExceptions::productNotFound);
        return injectDiscount(injectMedia(ProductMapper.toDto(product)));
    }

    @Override
    public ProductDto getBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(ProductExceptions::productNotFound);
        return injectDiscount(injectMedia(ProductMapper.toDto(product)));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductInfoDto getProductInfo(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(ProductExceptions::productNotFound);

        if (Boolean.FALSE.equals(product.getIsActive())) {
            throw ProductExceptions.productNotFound();
        }

        return ProductInfoMapper.toInfoDto(product);
    }

    @Override
    public ProductDto create(ProductDto dto) {

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(ProductExceptions::categoryNotFound);

        Brand brand = brandRepository.findById(dto.getBrandId())
                .orElseThrow(ProductExceptions::brandNotFound);

        List<Tag> tags = tagService.resolveTags(dto.getTags())
                .stream().map(TagMapper::toEntity).toList();


        if (slugExistsInTheSameStore(dto.getSlug(), 1L)) {
            log.warn("Slug {} already exists", dto.getSlug());
            throw ProductExceptions.slugExistsInTheSameStore();
        }
        validateSingleDefaultVariant(dto);
        if (dto.getVariants().size() > 1) {
            validateVariantsHaveAttributes(dto.getVariants());
        }
        Product product = ProductMapper.toEntity(dto, category, brand, tags);

        // TODO : replace it with the actual storeId and mallId from the token
        product.setStoreId(1L);
        product.setMallId(1L);

        Product saved = productRepository.save(product);


        List<ProductVariantDto> variantDtos = new ArrayList<>();
        for (ProductVariantDto v : dto.getVariants()) {
            ProductVariantDto savedVariant = productVariantService.create(saved.getId(), v);
            variantDtos.add(savedVariant);

        }
        ProductDto result = ProductMapper.toDto(saved);
        result.setVariants(variantDtos);
        return result;
    }

    @Override
    public ProductDto update(ProductDto dto) {

        Product product = productRepository.findById(dto.getId())
                .orElseThrow(ProductExceptions::productNotFound);

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(ProductExceptions::categoryNotFound);

        Brand brand = brandRepository.findById(dto.getBrandId())
                .orElseThrow(ProductExceptions::brandNotFound);

        if (!product.getSlug().equals(dto.getSlug()) &&
                slugExistsInTheSameStore(dto.getSlug(), product.getStoreId())) {
            log.warn("Slug {} already exists", dto.getSlug());
            throw ProductExceptions.slugExistsInTheSameStore();
        }

        validateSingleDefaultVariant(dto);

        // Resolve tags
        List<Tag> tags = tagService.resolveTags(dto.getTags())
                .stream()
                .map(TagMapper::toEntity)
                .toList();

        // Update basic fields
        product.setName(dto.getName());
        product.setSlug(dto.getSlug());
        product.setTargetedAudience(dto.getTargetedAudience());
        product.setAgeGroup(dto.getAgeGroup());
        product.setIsActive(dto.getIsActive());
        product.setShortDescription(dto.getShortDescription());
        product.setDescription(dto.getDescription());
        product.setCategory(category);
        product.setBrand(brand);
        product.setTags(tags);

        Product savedProduct = productRepository.save(product);
        //
        productVariantRepository.deleteByProductId(savedProduct.getId());

        List<ProductVariantDto> variantDtos = new ArrayList<>();

        for (ProductVariantDto variantDto : dto.getVariants()) {
            ProductVariantDto savedVariant =
                    productVariantService.create(savedProduct.getId(), variantDto);
            variantDtos.add(savedVariant);
        }

        ProductDto result = ProductMapper.toDto(savedProduct);
        result.setVariants(variantDtos);

        return result;
    }

    @Override
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(ProductExceptions::productNotFound);
        productRepository.delete(product);
    }

    boolean slugExistsInTheSameStore(String slug, Long storeId) {
        boolean result = productRepository.existsBySlugIgnoreCaseAndStoreId(slug, storeId);
        return result;
    }

    private static void validateSingleDefaultVariant(ProductDto dto) {
        long defaults =
                dto.getVariants().stream()
                        .filter(ProductVariantDto::isDefault)
                        .count();

        if (defaults > 1)
            throw ProductExceptions.multipleDefaultVariants();
    }

    private static void validateVariantsHaveAttributes(List<ProductVariantDto> variants) {
        for (ProductVariantDto variant : variants) {
            if (variant.getAttributes() == null || variant.getAttributes().isEmpty()) {
                throw ProductExceptions.variantShouldHasAttribute();
            }
        }
    }

    private ProductDto injectDiscount(ProductDto dto) {
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

    private ProductDto injectMedia(ProductDto dto) {
        for (ProductVariantDto v : dto.getVariants()) {
            productVariantService.injectMedia(v);
        }
        return dto;
    }
}
