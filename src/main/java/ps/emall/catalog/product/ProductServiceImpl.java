package ps.emall.catalog.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ps.emall.catalog.brand.Brand;
import ps.emall.catalog.brand.BrandRepository;
import ps.emall.catalog.category.Category;
import ps.emall.catalog.category.CategoryRepository;
import ps.emall.catalog.product.product_variant.ProductVariant;
import ps.emall.catalog.product.product_variant.ProductVariantDto;
import ps.emall.catalog.product.product_variant.ProductVariantService;
import ps.emall.catalog.tag.Tag;
import ps.emall.catalog.tag.TagService;
import ps.emall.catalog.tag.TagMapper;

import java.util.List;
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

    @Override
    public Page<ProductDto> getAll(ProductSpec spec, Pageable pageable) {
        return productRepository.findAll(spec, pageable)
                .map(ProductMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getAllProductList(ProductSpec spec) {
        return productRepository.findAll(spec)
                .stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(ProductExceptions::productNotFound);
        return ProductMapper.toDto(product);
    }

    @Override
    public ProductDto getBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(ProductExceptions::productNotFound);
        return ProductMapper.toDto(product);
    }

    @Override
    public ProductDto create(ProductDto dto) {

        // Fetch category and brand
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(ProductExceptions::categoryNotFound);

        Brand brand = brandRepository.findById(dto.getBrandId())
                .orElseThrow(ProductExceptions::brandNotFound);

        // Resolve tags
        List<Tag> tags = tagService.resolveTags(dto.getTags())
                .stream().map(TagMapper::toEntity).toList();

        // Create product
        Product product = Product.builder()
                .name(dto.getName())
                .slug(dto.getSlug())
                .targetedAudience(dto.getTargetedAudience())
                .ageGroup(dto.getAgeGroup())
                .isActive(dto.getIsActive())
                .shortDescription(dto.getShortDescription())
                .description(dto.getDescription())
                .category(category)
                .brand(brand)
                .mallId(dto.getMallId())
                .storeId(dto.getStoreId())
                .tags(tags)
                .build();

        Product saved = productRepository.save(product);
        log.info("Created product id={}", saved.getId());

        // Create variants
        if (dto.getVariants() != null) {
//            dto.getVariants().forEach(v -> productVariantService.create(saved.getId(), v));
            for(ProductVariantDto v : dto.getVariants()) {
                ProductVariantDto savedVariant = productVariantService.create(saved.getId(), v);
                log.info("Created product variant id={}", savedVariant.getId());
                log.info("Created product variant name={}",  savedVariant.getName());
            }
        }

        return ProductMapper.toDto(saved);
    }

    @Override
    public ProductDto update(ProductDto dto) {

        Product product = productRepository.findById(dto.getId())
                .orElseThrow(ProductExceptions::productNotFound);

        // Fetch category and brand
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(ProductExceptions::categoryNotFound);

        Brand brand = brandRepository.findById(dto.getBrandId())
                .orElseThrow(ProductExceptions::brandNotFound);

        // Resolve tags
        List<Tag> tags = tagService.resolveTags(dto.getTags())
                .stream().map(TagMapper::toEntity).toList();

        // Update product
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

        Product saved = productRepository.save(product);
        log.info("Updated product id={}", saved.getId());

        // Update variants
        if (dto.getVariants() != null) {
            for (var variantDto : dto.getVariants()) {
                if (variantDto.getId() != null) {
                    // Update existing variant
                    productVariantService.update(saved.getId(), variantDto.getId(), variantDto);
                } else {
                    // Create new variant
                    productVariantService.create(saved.getId(), variantDto);
                }
            }
        }

        return ProductMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(ProductExceptions::productNotFound);
        productRepository.delete(product);
        log.info("Deleted product id={}", id);
    }
}
