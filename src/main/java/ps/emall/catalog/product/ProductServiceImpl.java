package ps.emall.catalog.product;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ps.emall.catalog.brand.Brand;
import ps.emall.catalog.brand.BrandRepository;
import ps.emall.catalog.category.Category;
import ps.emall.catalog.category.CategoryExceptions;
import ps.emall.catalog.category.CategoryRepository;
import ps.emall.catalog.client.campaigns.ActiveProductDiscountDto;
import ps.emall.catalog.client.interaction.InteractionClient;
import ps.emall.catalog.client.interaction.InteractionResponse;
import ps.emall.catalog.client.interaction.product_similarity.SimilarProductsQuery;
import ps.emall.catalog.client.interaction.product_similarity.SimilarProductsResult;
import ps.emall.catalog.client.media_manager.FileLightDto;
import ps.emall.catalog.product.info.ProductInfoDto;
import ps.emall.catalog.product.info.ProductInfoMapper;
import ps.emall.catalog.product.light.ProductLightMapper;
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
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final TagService tagService;
    private final ProductVariantService productVariantService;
    private final ProductSpecificationBuilder productSpecificationBuilder;
    private final ProductServiceHelper productServiceHelper;
    private final InteractionClient interactionClient;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ProductLightDto> getAllLight(ProductFilter filter, Pageable pageable) {
        Specification<Product> spec = productSpecificationBuilder.build(filter);
        Page<Long> productPage = productRepository.findIdsBySpecification(spec, pageable);

        List<Long> productIds = productPage.getContent()
                .stream()
                .toList();

        Map<Long, ProductLightDto> dtoMap = getLightByIds(productIds).stream()
                .collect(Collectors.toMap(
                        ProductLightDto::getId,
                        dto -> dto,
                        (oldValue, newValue) -> oldValue
                ));

        return PaginatedResponse.of(productPage.map(dtoMap::get));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductSummary getSummary(ProductFilter filter) {
        Specification<Product> spec = productSpecificationBuilder.build(filter);
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
    public List<ProductLightDto> getAllProductList(ProductFilter filter) {
        Specification<Product> spec = productSpecificationBuilder.build(filter);
        List<Long> productIds = productRepository.findIdsBySpecification(spec);

        return getLightByIds(productIds);
    }

    private List<ProductLightDto> getLightByIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return List.of();
        }

        Map<Long, ProductLightRow> rowMap = new HashMap<>();
        List<UUID> mediaIds = new ArrayList<>();

        for (ProductLightRow row : productRepository.findLightRowsByProductIds(productIds)) {
            rowMap.put(row.getProductId(), row);
            if (row.getMediumId() != null) {
                mediaIds.add(row.getMediumId());
            }
        }

        Map<UUID, FileLightDto> mediaMap = productServiceHelper.getMedia(mediaIds);
        Map<Long, ActiveProductDiscountDto> discountMap =
                productServiceHelper.getActiveDiscounts(productIds);

        return productIds.stream()
                .filter(rowMap::containsKey)
                .map(productId -> ProductLightMapper.toProductLightDto(productId, rowMap, mediaMap))
                .map(dto -> productServiceHelper.injectLightDiscount(dto, discountMap))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getById(Long id) {
        // todo you might make it more general and restrict from the controller or make other function
        Product product = productRepository.findById(id)
                .orElseThrow(ProductExceptions::productNotFound);

        if (product.getIsActive().equals(Boolean.FALSE)) {
            throw ProductExceptions.productNotActive();
        }

        return productServiceHelper.injectDiscount(productServiceHelper.injectMedium(ProductMapper.toDto(product)));
    }

    @Override
    public ProductDto getByStoreIdAndId(Long storeId, Long id) {
        Product product = productRepository.findByStoreIdAndId(storeId, id)
                .orElseThrow(ProductExceptions::productNotFound);

        return productServiceHelper.injectDiscount(productServiceHelper.injectMedium(ProductMapper.toDto(product)));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(ProductExceptions::productNotFound);

        if (product.getIsActive().equals(Boolean.FALSE)) {
            throw ProductExceptions.productNotActive();
        }

        return productServiceHelper.injectDiscount(productServiceHelper.injectMedium(ProductMapper.toDto(product)));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getByStoreIdAndSlug(Long storeId, String slug) {
        log.info("storeId : {}, slug : {}", storeId, slug);
        Product product = productRepository.findByStoreIdAndSlug(storeId, slug)
                .orElseThrow(ProductExceptions::productNotFound);

        return productServiceHelper.injectDiscount(productServiceHelper.injectMedium(ProductMapper.toDto(product)));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductInfoDto getProductInfo(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(ProductExceptions::productNotFound);

        if (product.getIsActive().equals(Boolean.FALSE)) {
            throw ProductExceptions.productNotActive();
        }

        return ProductInfoMapper.toInfoDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductLightDto> getSimilar(Long id, Integer topK) {
        Product product = productRepository.findById(id)
                .filter(item -> Boolean.TRUE.equals(item.getIsActive()))
                .orElseThrow(ProductExceptions::productNotFound);

        SimilarProductsQuery query = SimilarProductsQuery.builder()
                .productId(id)
                .activeOnly(Boolean.TRUE)
                .sameMallOnly(Boolean.TRUE)
                .topK(topK)
                .build();
        try {
            InteractionResponse<SimilarProductsResult> response = interactionClient.getSimilarProducts(query);
            if (response == null || response.getData() == null || response.getData().isSuccess() == false) {
                log.warn("Could not fetch similar products from interaction productId={}. Using catalog fallback.", id);
                return getFallbackSimilarProducts(product, topK);
            }
            SimilarProductsResult similarProductsResult = response.getData();
            return getLightByIds(similarProductsResult.getProductIds());


        } catch (FeignException e) {
            log.warn("Could not fetch similar products from interaction productId={}, status={}. Using catalog fallback.",
                    id, e.status()
            );
            return getFallbackSimilarProducts(product, topK);
        }

    }

    private List<ProductLightDto> getFallbackSimilarProducts(Product product, Integer topK) {
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

        return getLightByIds(productIds);
    }

    @Override
    public ProductDto create(Long mallId, Long storeId, ProductDto dto) {

        // validation
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(ProductExceptions::categoryNotFound);

        Brand brand = brandRepository.findById(dto.getBrandId())
                .orElseThrow(ProductExceptions::brandNotFound);

        productServiceHelper.audienceValidation(dto, category, brand);

        if (productServiceHelper.slugExistsInTheSameStore(dto.getSlug(), storeId)) {
            throw ProductExceptions.slugExistsInTheSameStore();
        }

        productServiceHelper.validateSingleDefaultVariant(dto);
        if (dto.getVariants().size() > 1) {
            productServiceHelper.validateVariantsHaveAttributes(dto.getVariants());
        }

        List<Tag> tags = null;
        if (dto.getTags() != null) {
            tags = tagService.resolveTags(dto.getTags())
                    .stream().map(TagMapper::toEntity).toList();
        }

        Product product = ProductMapper.toEntity(dto, category, brand, tags);

        product.setMallId(mallId);
        product.setStoreId(storeId);

        Product saved = productRepository.save(product);

        List<ProductVariantDto> variantDtos = new ArrayList<>();
        ProductVariant defaultVariant = null;
        for (ProductVariantDto v : dto.getVariants()) {
            ProductVariantDto savedVariant = productVariantService.create(saved.getId(), v);
            variantDtos.add(savedVariant);
            if (savedVariant.getIsDefault()) {
                defaultVariant = ProductVariantMapper.toEntity(savedVariant, product);
            }
        }
        saved.setDefaultVariant(defaultVariant);
        ProductDto result = ProductMapper.toDto(productRepository.save(saved));

        result.setVariants(variantDtos);
        productServiceHelper.publishCreatedJob(product);
        return result;
    }

    @Override
    // update only product basic info
    public ProductDto update(Long mallId, Long storeId, ProductDto dto) {

        Product existing = productRepository.findById(dto.getId())
                .orElseThrow(ProductExceptions::productNotFound);

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(ProductExceptions::categoryNotFound);

        Brand brand = brandRepository.findById(dto.getBrandId())
                .orElseThrow(ProductExceptions::brandNotFound);

        productServiceHelper.audienceValidation(dto, category, brand);

        if (!existing.getSlug().equals(dto.getSlug()) &&
                productServiceHelper.slugExistsInTheSameStore(dto.getSlug(), existing.getStoreId())) {
            log.warn("Slug {} already exists", dto.getSlug());
            throw ProductExceptions.slugExistsInTheSameStore();
        }

        if (!existing.getMallId().equals(mallId)) {
            throw ProductExceptions.productDoesNotBelongToMall();
        }

        if (!existing.getStoreId().equals(storeId)) {
            throw ProductExceptions.productDoesNotBelongToStore();
        }

        // Resolve tags

        List<Tag> tags = new ArrayList<>();
        if (dto.getTags() != null) {
            tags = tagService.resolveTags(dto.getTags())
                    .stream()
                    .map(TagMapper::toEntity)
                    .collect(Collectors.toCollection(ArrayList::new));
        }


        if (Boolean.TRUE.equals(existing.getIsActive()) && Boolean.FALSE.equals(dto.getIsActive())) {
            deactivation(existing);
        }
        if (Boolean.FALSE.equals(existing.getIsActive()) && Boolean.TRUE.equals(dto.getIsActive())) {
            activation(existing);
        }
        // Update basic fields
        existing.setName(dto.getName());
        existing.setSlug(dto.getSlug());
        existing.setTargetedAudience(dto.getTargetedAudience());
        existing.setAgeGroup(dto.getAgeGroup());
        existing.setIsActive(dto.getIsActive());
        existing.setShortDescription(dto.getShortDescription());
        existing.setDescription(dto.getDescription());
        existing.setCategory(category);
        existing.setBrand(brand);

        existing.getTags().clear();
        existing.getTags().addAll(tags);

        Product savedProduct = productRepository.save(existing);

        productServiceHelper.publishUpdatedJob(savedProduct);
        return ProductMapper.toDto(savedProduct);
    }

    @Override
    public void delete(Long storeId, Long id) {
        Product product = productRepository.findByStoreIdAndId(storeId, id)
                .orElseThrow(ProductExceptions::productNotFound);

        // todo check if there any order, discount, variant
        productServiceHelper.publishDeletedJob(product.getId());
        productRepository.delete(product);
    }

    public void activation(Product product) {
    }

    public void deactivation(Product product) {
    }


}
