package ps.emall.catalog.product;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ps.emall.catalog.brand.Brand;
import ps.emall.catalog.brand.BrandRepository;
import ps.emall.catalog.category.Category;
import ps.emall.catalog.category.CategoryExceptions;
import ps.emall.catalog.category.CategoryRepository;
import ps.emall.catalog.client.campaigns.ActiveOfferDto;
import ps.emall.catalog.client.campaigns.CampaignsClient;
import ps.emall.catalog.client.media_manager.FileDto;
import ps.emall.catalog.client.media_manager.FileLightDto;
import ps.emall.catalog.client.media_manager.MediaManagerClient;
import ps.emall.catalog.client.media_manager.MediaResponse;
import ps.emall.catalog.common.audience.AgeGroup;
import ps.emall.catalog.common.audience.TargetedAudience;
import ps.emall.catalog.product.summary.*;
import ps.emall.catalog.common.page.PaginatedResponse;
import ps.emall.catalog.product.light.ProductLightDto;
import ps.emall.catalog.product.light.ProductLightRow;
import ps.emall.catalog.product.product_variant.*;
import ps.emall.catalog.tag.Tag;
import ps.emall.catalog.tag.TagService;
import ps.emall.catalog.tag.TagMapper;

import java.util.*;
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
    private final MediaManagerClient mediaManagerClient;
    private final ProductSpecificationBuilder productSpecificationBuilder;
    @Override
    public PaginatedResponse<ProductDto> getAll(ProductFilter filter, Pageable pageable) {
        Specification<Product> spec =  productSpecificationBuilder.build(filter);
        Page<ProductDto> page = productRepository.findAll(spec, pageable)
                .map(ProductMapper::toDto)
                .map(this::injectMedia)
                .map(this::injectDiscount);

        return PaginatedResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ProductLightDto> getAllLight(ProductFilter filter, Pageable pageable) {
        Specification<Product> spec =  productSpecificationBuilder.build(filter);
        Page<Long> productPage = productRepository.findIdsBySpecification(spec, pageable);

        List<Long> productIds = productPage.getContent()
                .stream()
                .toList();

        List<ProductLightRow> rows = productRepository.findLightRowsByProductIds(productIds);

        Map<Long, ProductLightRow> rowMap = new HashMap<>();
        List<UUID> mediaIds = new ArrayList<>();

        for (ProductLightRow row : rows) {
            rowMap.put(row.getProductId(), row);
            mediaIds.add(row.getMediumId());
        }

        Map<UUID, FileLightDto> mediaMap = getMedia(mediaIds);

        Page<ProductLightDto> dtoPage = productPage.map(productId -> {
            ProductLightRow row = rowMap.get(productId);
            FileLightDto fileLightDto = mediaMap.get(row.getMediumId());

            ProductLightDto dto = ProductLightDto.builder()
                    .id(row.getProductId())
                    .build();

            if (row != null) {
                dto.setDefaultVariantId(row.getDefaultVariantId());
                dto.setBasePrice(row.getBasePrice());
                dto.setName(row.getProductName());
                dto.setSlug(row.getProductSlug());
                if (row.getMediumId() != null) {
                    FileLightDto medium = new FileLightDto();
                    medium.setId(row.getMediumId());
                    dto.setMedium(medium);
                }
            }
            if (fileLightDto != null) {
                dto.setMedium(fileLightDto);
            }

            return dto;
        });

        return PaginatedResponse.of(dtoPage);
    }


    //    @Cacheable
    public ProductSummary getSummary(ProductFilter filter) {
        Specification<Product> spec =  productSpecificationBuilder.build(filter);
        long productCount = productRepository.count(spec);
        AudienceDistribution audienceDistribution = productRepository.getAudienceDistribution(spec);
        AgeDistribution ageDistribution = productRepository.getAgeDistribution(spec);
        List<CategoryDistribution> categoryDistributions = productRepository.getCategoryDistribution(spec);
        List<BrandDistribution> brandDistributions = productRepository.getBrandDistribution(spec);
        PriceRange priceRange = productRepository.getPriceRange(spec);
        List<AttributeSummary> attributeSummary = productRepository.getAttributeSummary(spec);

        return new ProductSummary(
                productCount,
                audienceDistribution,
                ageDistribution,
                categoryDistributions,
                brandDistributions,
                priceRange,
                attributeSummary
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getAllProductList(ProductFilter filter) {
        Specification<Product> spec =  productSpecificationBuilder.build(filter);
        List<Product> products = (spec == null)
                ? productRepository.findAll()
                : productRepository.findAll(spec);

        return products
                .stream()
                .map(ProductMapper::toDto)
                .map(this::injectDiscount)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public ProductDto getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(ProductExceptions::productNotFound);
        return injectDiscount(injectMedia(ProductMapper.toDto(product)));
    }

    @Override
    @Transactional(readOnly = true)
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
    public ProductDto create(ProductDto dto, Long mallId, Long storeId) {

        // validation
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(ProductExceptions::categoryNotFound);

        Brand brand = brandRepository.findById(dto.getBrandId())
                .orElseThrow(ProductExceptions::brandNotFound);

        List<Tag> tags = tagService.resolveTags(dto.getTags())
                .stream().map(TagMapper::toEntity).toList();


        if (slugExistsInTheSameStore(dto.getSlug(), 1L)) {
            throw ProductExceptions.slugExistsInTheSameStore();
        }
        validateSingleDefaultVariant(dto);
        if (dto.getVariants().size() > 1) {
            validateVariantsHaveAttributes(dto.getVariants());
        }

        validateTargetedAudience(dto.getTargetedAudience(), category.getTargetedAudience());
        validateAgeGroup(dto.getAgeGroup(), category.getAgeGroup());


        Product product = ProductMapper.toEntity(dto, category, brand, tags);
        // TODO : replace it with the actual storeId and mallId from the token
        product.setMallId(mallId);
        product.setStoreId(storeId);

        Product saved = productRepository.save(product);


        List<ProductVariantDto> variantDtos = new ArrayList<>();
        ProductVariant defaultVariant = null;
        for (ProductVariantDto v : dto.getVariants()) {
            ProductVariantDto savedVariant = productVariantService.create(saved.getId(), v);
            variantDtos.add(savedVariant);
            if (savedVariant.isDefault()) {
                defaultVariant = ProductVariantMapper.toEntity(savedVariant, product);
            }
        }
        saved.setDefaultVariant(defaultVariant);
        ProductDto result = ProductMapper.toDto(productRepository.save(saved));

        result.setVariants(variantDtos);
        return result;
    }

    @Override
    public ProductDto update(ProductDto dto, Long mallId, Long storeId) {

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

        if(product.getMallId() != mallId){
            throw ProductExceptions.productDoesNotBelongToMall();
        }

        if(product.getStoreId() != storeId){
            throw ProductExceptions.productDoesNotBelongToStore();
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

        ProductVariant defaultVariant = null;
        for (ProductVariantDto variantDto : dto.getVariants()) {
            ProductVariantDto savedVariant =
                    productVariantService.create(savedProduct.getId(), variantDto);
            variantDtos.add(savedVariant);
            if (savedVariant.isDefault()) {
                defaultVariant = ProductVariantMapper.toEntity(savedVariant, product);
            }
        }
        savedProduct.setDefaultVariant(defaultVariant);
        ProductDto result = ProductMapper.toDto(productRepository.save(savedProduct));

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


    private ProductLightDto injectLightDiscount(ProductLightDto dto) {
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

    private ProductDto injectMedia(ProductDto dto) {
        for (ProductVariantDto v : dto.getVariants()) {
            productVariantService.injectMedia(v);
        }
        return dto;
    }

    private Map<UUID, FileLightDto> getMedia(List<UUID> mediaIds) {
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

    private void validateTargetedAudience(TargetedAudience productTargetedAudience, TargetedAudience categoryTargetedAudience) {
        if (categoryTargetedAudience == TargetedAudience.ALL) return;
        if (productTargetedAudience == categoryTargetedAudience) return;
        throw ProductExceptions.invalidProductAudienceForCategory();
    }

    private void validateAgeGroup(AgeGroup productAgeGroup, AgeGroup categoryAgeGroup) {
        if (categoryAgeGroup == AgeGroup.ALL) return;
        if (productAgeGroup == categoryAgeGroup) return;
        throw ProductExceptions.invalidProductAgeGroupForCategory();
    }
}
