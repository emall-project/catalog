package ps.emall.catalog.category;

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
import ps.emall.catalog.common.exception.EMallsException;
import ps.emall.catalog.common.page.PaginatedResponse;
import ps.emall.catalog.product.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final MediaManagerClient mediaManagerClient;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<CategoryDto> getAll(Specification<Category> spec, Pageable pageable) {
        Page<CategoryDto> page =  categoryRepository.findAll(spec, pageable)
                .map(CategoryMapper::toDto)
                .map(this::injectImageUrl);
        return PaginatedResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategoryList(CategorySpec spec) {
        List<Category> categories = (spec == null)
                ? categoryRepository.findAll()
                : categoryRepository.findAll(spec);


        return categories.stream()
                .map(CategoryMapper::toDto)
                .map(this::injectImageUrl)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryExceptions::categoryNotFound);
        return injectImageUrl(CategoryMapper.toDto(category));
    }

    // TODO: remove these tow method, cause get all can do the same functionality
    @Override
    @Transactional(readOnly = true)
    public CategoryDto getBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(CategoryExceptions::categoryNotFound);
        return injectImageUrl(CategoryMapper.toDto(category));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getRoots() {
        return categoryRepository.findByParentIsNull().stream()
                .map(CategoryMapper::toDto)
                .map(this::injectImageUrl)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getChildren(Long parentId) {
        return categoryRepository.findByParentId(parentId).stream()
                .map(CategoryMapper::toDto)
                .map(this::injectImageUrl)
                .toList();
    }

    @Override
    public CategoryDto create(CategoryDto dto) {
        if (slugExists(dto.getSlug())) {
            throw CategoryExceptions.slugExists();
        }
        validateCategoryParent(dto);
        FileDto image = getAndValidatedImage(dto.getImageId());

        Category category = CategoryMapper.toEntity(dto);
        Category saved = categoryRepository.save(category);
        return CategoryMapper.toDto(saved, image);
    }

    @Override
    public CategoryDto update(CategoryDto dto) {
        Category existing = categoryRepository.findById(dto.getId())
                .orElseThrow(CategoryExceptions::categoryNotFound);

        if (!existing.getSlug().equals(dto.getSlug()) && slugExists(dto.getSlug())) {
            throw CategoryExceptions.slugExists();
        }
        validateCategoryParent(dto);

        FileDto image = getAndValidatedImage(dto.getImageId());

        if (existing.getIsActive().equals(true) && dto.getIsActive().equals(false)) {
            deactivation(existing);// apply deactivation side effects
        }
        if (existing.getIsActive().equals(false) && dto.getIsActive().equals(true)) {
            activation(existing);// apply activation side effects
        }

        existing.setName(dto.getName());
        existing.setSlug(dto.getSlug());
        existing.setImageId(dto.getImageId());
        existing.setTargetedAudience(dto.getTargetedAudience());
        existing.setAgeGroup(dto.getAgeGroup());
        existing.setIsActive(dto.getIsActive());
        existing.setParent(dto.getParentId() != null ?
                Category.builder().id(dto.getParentId()).build() : null);

        Category saved = categoryRepository.save(existing);
        return CategoryMapper.toDto(saved, image);
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryExceptions::categoryNotFound);

        long childCount = categoryRepository.countByParentId(id);
        if (childCount > 0) {
            throw CategoryExceptions.categoryHasChildren();
        }
        long productCount = productRepository.countByCategory_Id(id);
        if (productCount > 0) {
            throw CategoryExceptions.categoryHasProducts();
        }
        categoryRepository.delete(category);
    }




    //-----------------------HELPER-------------------------------------

    private void deactivation(Category category) {

        List<Category> children = categoryRepository.findByParentId(category.getId());
        // TODO: make sure this wont cause any stack issues
        for (Category child : children) {
            deactivation(child);
        }

        if (Boolean.FALSE.equals(category.getIsActive())) {
            return;
        }

        // TODO:  notify vendor who have products attached to this Category
        category.setIsActive(Boolean.FALSE);
        categoryRepository.save(category);
    }

    private void activation(Category category) {

        List<Category> children = categoryRepository.findByParentId(category.getId());

        for (Category child : children) {
            activation(child);
        }
        // TODO:  notify vendor how have products attached to this Category
        if (Boolean.TRUE.equals(category.getIsActive())) {
            return;
        }
        category.setIsActive(Boolean.TRUE);
        categoryRepository.save(category);

    }

    @Override
    public boolean slugExists(String slug) {
        return categoryRepository.existsBySlug(slug);
    }

    private void validateCategoryParent(CategoryDto dto) {
        if (dto.getParentId() != null) {

            if (!categoryRepository.existsById(dto.getParentId())) {
                throw CategoryExceptions.parentNotFound();
            }

            if (dto.getId() != null) {
                if (dto.getParentId().equals(dto.getId())) {
                    throw CategoryExceptions.selfParenting();
                }
                if (isCircularHierarchy(dto.getId(), dto.getParentId())) {
                    throw CategoryExceptions.circularHierarchy();
                }
            }
        }
    }

    private boolean isCircularHierarchy(Long categoryId, Long parentId) {
        Long currentParentId = parentId;
        while (currentParentId != null) {
            if (currentParentId.equals(categoryId)) {
                return true;
            }
            Optional<Category> parent = categoryRepository.findById(currentParentId);
            if (parent.isEmpty()) break;
            currentParentId = parent.get().getParent() != null ? parent.get().getParent().getId() : null;
        }
        return false;
    }

    private CategoryDto injectImageUrl(CategoryDto dto) {
        MediaResponse<FileDto> response = mediaManagerClient.getById(dto.getImageId());
        if (response.getErrorCodes() != null) {
            throw new EMallsException(response.getErrorCodes(), null, response.getStatus(), response.getMessage());
        }
        dto.setImage(response.getData());
        return dto;
    }

    private boolean isImage(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }

    private FileDto getAndValidatedImage(UUID imageId) {
            MediaResponse<FileDto> response;
        try {
            response = mediaManagerClient.getById(imageId);
        } catch (FeignException e) {
            log.info("Could not validate imageId File from MediaManager imageId={}, status={}, message={}",
                    imageId, e.status(), e.getMessage()
            );
            throw CategoryExceptions.imageCouldNotBeValidated();
        }

        if (response.getErrorCodes() != null && !response.getErrorCodes().isEmpty()) {
            throw CategoryExceptions.imageNotFound();
        }
        FileDto fileDto = response.getData();
        log.info("fetching file with name {}", fileDto.getName());
        if (!isImage(fileDto.getMimeType())) {
            throw CategoryExceptions.invalidFileType();
        }
        return response.getData();
    }
}
