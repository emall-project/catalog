package ps.emall.catalog.brand;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ps.emall.catalog.client.media_manager.FileDto;
import ps.emall.catalog.common.page.PaginatedResponse;
import ps.emall.catalog.product.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final BrandServiceHelper brandServiceHelper;
    private final BrandSpecificationBuilder brandSpecificationBuilder;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<BrandDto> getAll(BrandFilter filter, Pageable pageable) {
        Specification<Brand> spec = brandSpecificationBuilder.build(filter);

        Page<BrandDto> page = brandRepository.findAll(spec, pageable)
                .map(BrandMapper::toDto)
                .map(brandServiceHelper::injectImageUrl)
                .map(this::withProductsCount);
        return PaginatedResponse.of(page);
    }

    @Override
    public List<BrandDto> getAllBrandsList(BrandFilter filter) {
        Specification<Brand> spec = brandSpecificationBuilder.build(filter);

        List<Brand> brands = (spec == null)
                ? brandRepository.findAll()
                : brandRepository.findAll(spec);


        return brands.stream()
                .map(BrandMapper::toDto)
                .map(brandServiceHelper::injectImageUrl)
                .map(this::withProductsCount)
                .collect(Collectors.toList());
    }

    @Override
    public BrandDto create(BrandDto dto) {
        if (brandRepository.existsBySlug(dto.getSlug())) {
            throw BrandExceptions.slugExists();
        }

        FileDto image = brandServiceHelper.getAndValidatedImage(dto.getImageId());

        Brand brand = BrandMapper.toEntity(dto);
        Brand saved = brandRepository.save(brand);
        return withProductsCount(BrandMapper.toDto(saved, image));
    }

    @Override
    public BrandDto update(BrandDto dto) {
        Brand existing = brandRepository.findById(dto.getId())
                .orElseThrow(BrandExceptions::brandNotFound);

        FileDto image = brandServiceHelper.getAndValidatedImage(dto.getImageId());

        if (!existing.getSlug().equals(dto.getSlug())
                && brandRepository.existsBySlug(dto.getSlug())) {
            throw BrandExceptions.slugExists();
        }
        if (existing.getIsActive().equals(true) && dto.getIsActive().equals(false)) {
            brandServiceHelper.deactivation(existing);
        }
        if (existing.getIsActive().equals(false) && dto.getIsActive().equals(true)) {
            brandServiceHelper.activation(existing);
        }
        existing.setName(dto.getName());
        existing.setSlug(dto.getSlug());
        existing.setTargetedAudience(dto.getTargetedAudience());
        existing.setAgeGroup(dto.getAgeGroup());
        existing.setImageId(dto.getImageId());
        existing.setIsActive(dto.getIsActive());
        Brand saved = brandRepository.save(existing);
        return withProductsCount(BrandMapper.toDto(saved, image));
    }

    @Override
    @Transactional(readOnly = true)
    public BrandDto getById(Long id) {
        return brandRepository.findById(id)
                .map(BrandMapper::toDto)
                .map(this::withProductsCount)
                .orElseThrow(BrandExceptions::brandNotFound);
    }

    @Override
    public BrandDto getActiveById(Long id) {

        return brandRepository.findByIdAndIsActiveTrue(id)
                .map(BrandMapper::toDto)
                .map(this::withProductsCount)
                .orElseThrow(BrandExceptions::brandNotFound);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandDto getBySlug(String slug) {
        return brandRepository.findBySlug(slug)
                .map(BrandMapper::toDto)
                .map(this::withProductsCount)
                .orElseThrow(BrandExceptions::brandNotFound);
    }

    @Override
    public BrandDto getActiveBySlug(String slug) {
        return brandRepository.findBySlugAndIsActiveTrue(slug)
                .map(BrandMapper::toDto)
                .map(this::withProductsCount)
                .orElseThrow(BrandExceptions::brandNotFound);
    }

    @Override
    public void delete(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(BrandExceptions::brandNotFound);

        long productsCount = productRepository.countByBrand_Id(id);
        if (productsCount > 0) {
            throw BrandExceptions.brandHasProducts();
        }

        brandRepository.delete(brand);
    }

    private BrandDto withProductsCount(BrandDto dto) {
        dto.setProductsCount(productRepository.countByBrand_Id(dto.getId()));
        return dto;
    }
}
