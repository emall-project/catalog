package ps.emall.catalog.brand;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ps.emall.catalog.media_manager.MediaManagerService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final MediaManagerService mediaManagerService;
    @Override
    @Transactional(readOnly = true)
    public Page<BrandDto> getAll(Specification<Brand> spec, Pageable pageable) {
        return brandRepository.findAll(spec, pageable)
                .map(BrandMapper::toDto);
    }

    @Override
    public List<BrandDto> getAllBrandsList(Specification<Brand> spec) {
        List<Brand> brands = (spec == null)
                ? brandRepository.findAll()
                : brandRepository.findAll(spec);


        return brands.stream()
                .map(BrandMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BrandDto create(BrandDto dto) {
        if (brandRepository.existsBySlug(dto.getSlug())) {
            throw BrandExceptions.slugExists();
        }
        //  TODO: uncomment when adding implement isFileExist in Media-manager
//        if (!mediaManagerService.isFileExist(dto.getImageFileKey())) {
//            throw BrandExceptions.imageNotFound();
//        }

        Brand brand = BrandMapper.toEntity(dto);
        Brand saved = brandRepository.save(brand);
        return BrandMapper.toDto(saved);
    }

    @Override
    public BrandDto update(BrandDto dto) {
        Brand existing = brandRepository.findById(dto.getId())
                .orElseThrow(BrandExceptions::brandNotFound);
        //  TODO: uncomment when adding implement isFileExist in Media-manager
//        if (!mediaManagerService.isFileExist(dto.getImageFileKey())) {
//            throw BrandExceptions.imageNotFound();
//        }
        if (!existing.getSlug().equals(dto.getSlug())
                && brandRepository.existsBySlug(dto.getSlug())) {
            throw BrandExceptions.slugExists();
        }
        if(existing.getIsActive().equals(true) &&dto.getIsActive().equals(false)) {
            deactivation(existing);
        }
        if(existing.getIsActive().equals(false) &&dto.getIsActive().equals(true)) {
            activation(existing);
        }
        existing.setName(dto.getName());
        existing.setSlug(dto.getSlug());
        existing.setTargetedAudience(dto.getTargetedAudience());
        existing.setAgeGroup(dto.getAgeGroup());
        existing.setImageFileKey(dto.getImageFileKey());
        existing.setIsActive(dto.getIsActive());
        Brand saved = brandRepository.save(existing);
        return BrandMapper.toDto(saved);
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

        // TODO:  throw error if there products attached to this brand
//        if(){
//            throw BrandExceptions.brandHasProducts();
//        }
        brandRepository.delete(brand);
    }


    private void activation(Brand brand) {

        // TODO:  make all product Active when Activate their brand
        // TODO:  notify vendor how have products attached to this brand
    }


    private void deactivation(Brand brand) {

        // TODO:  make all product inActive when Deactivate their brand
        // TODO:  notify vendor how have products attached to this brand
    }

}
