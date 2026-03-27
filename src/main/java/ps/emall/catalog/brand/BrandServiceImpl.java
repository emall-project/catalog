package ps.emall.catalog.brand;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ps.emall.catalog.client.media_manager.FileDto;
import ps.emall.catalog.client.media_manager.MediaManagerClient;
import ps.emall.catalog.client.media_manager.MediaResponse;
import ps.emall.catalog.common.page.PaginatedResponse;
import ps.emall.catalog.product.ProductRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final MediaManagerClient mediaManagerClient;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<BrandDto> getAll(Specification<Brand> spec, Pageable pageable) {
        Page<BrandDto> page = brandRepository.findAll(spec, pageable)
                .map(BrandMapper::toDto)
                .map(this::injectImageUrl);
        return PaginatedResponse.of(page);
    }

    @Override
    public List<BrandDto> getAllBrandsList(Specification<Brand> spec) {
        List<Brand> brands = (spec == null)
                ? brandRepository.findAll()
                : brandRepository.findAll(spec);


        return brands.stream()
                .map(BrandMapper::toDto)
                .map(this::injectImageUrl)
                .collect(Collectors.toList());
    }

    @Override
    public BrandDto create(BrandDto dto) {
        if (brandRepository.existsBySlug(dto.getSlug())) {
            throw BrandExceptions.slugExists();
        }

        FileDto image = getAndValidatedImage(dto.getImageId());

        Brand brand = BrandMapper.toEntity(dto);
        Brand saved = brandRepository.save(brand);
        return BrandMapper.toDto(saved, image);
    }

    @Override
    public BrandDto update(BrandDto dto) {
        Brand existing = brandRepository.findById(dto.getId())
                .orElseThrow(BrandExceptions::brandNotFound);

        FileDto image = getAndValidatedImage(dto.getImageId());

        if (!existing.getSlug().equals(dto.getSlug())
                && brandRepository.existsBySlug(dto.getSlug())) {
            throw BrandExceptions.slugExists();
        }
        if (existing.getIsActive().equals(true) && dto.getIsActive().equals(false)) {
            deactivation(existing);
        }
        if (existing.getIsActive().equals(false) && dto.getIsActive().equals(true)) {
            activation(existing);
        }
        existing.setName(dto.getName());
        existing.setSlug(dto.getSlug());
        existing.setTargetedAudience(dto.getTargetedAudience());
        existing.setAgeGroup(dto.getAgeGroup());
        existing.setImageId(dto.getImageId());
        existing.setIsActive(dto.getIsActive());
        Brand saved = brandRepository.save(existing);
        return BrandMapper.toDto(saved, image);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandDto findById(Long id) {
        return brandRepository.findById(id)
                .map(BrandMapper::toDto)
                .orElseThrow(BrandExceptions::brandNotFound);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandDto findBySlug(String slug) {
        return brandRepository.findBySlug(slug)
                .map(BrandMapper::toDto)
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

    private void activation(Brand brand) {
        // TODO:  notify  vendor how have products attached to this brand
        brandRepository.activateById(brand.getId());
    }


    private void deactivation(Brand brand) {
        // TODO:  notify vendor how have products attached to this brand
        brandRepository.deactivateById(brand.getId());
    }


    private BrandDto injectImageUrl(BrandDto dto) {
        try {
            MediaResponse<FileDto> response = mediaManagerClient.getById(dto.getImageId());
            dto.setImage(response.getData());
            return dto;

        } catch (FeignException e) {
            if (e.status() == 404) {
                throw BrandExceptions.imageNotFound();
            }
            throw e;
        }
    }

    private boolean isImage(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }

    private FileDto getAndValidatedImage(UUID imageId) {
        try {
            MediaResponse<FileDto> response = mediaManagerClient.getById(imageId);
            // validate response not empty
            if (response == null || response.getData() == null) {
                throw BrandExceptions.imageCouldNotBeValidated();
            }

            FileDto fileDto = response.getData();

            // validate file type
            if (!isImage(fileDto.getMimeType())) {
                throw BrandExceptions.invalidFileType();
            }

            return response.getData();

        } catch (FeignException e) {
            if (e.status() == 404) {
                throw BrandExceptions.imageNotFound();
            }
            throw BrandExceptions.imageCouldNotBeValidated();
        }
    }
}
