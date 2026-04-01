package ps.emall.catalog.category;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ps.emall.catalog.category.audience_config.CategoryAudienceConfig;
import ps.emall.catalog.category.audience_config.CategoryAudienceConfigDto;
import ps.emall.catalog.category.audience_config.CategoryAudienceConfigMapper;
import ps.emall.catalog.category.audience_config.CategoryAudienceConfigRepository;
import ps.emall.catalog.client.media_manager.FileDto;
import ps.emall.catalog.client.media_manager.FileLightDto;
import ps.emall.catalog.client.media_manager.MediaManagerClient;
import ps.emall.catalog.client.media_manager.MediaResponse;
import ps.emall.catalog.common.audience.AgeGroup;
import ps.emall.catalog.common.audience.TargetedAudience;
import ps.emall.catalog.common.page.PaginatedResponse;
import ps.emall.catalog.product.ProductRepository;
import ps.emall.catalog.product.light.ProductLightDto;
import ps.emall.catalog.product.product_variant.ProductVariantExceptions;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryAudienceConfigRepository categoryAudienceConfigRepository;
    private final ProductRepository productRepository;
    private final MediaManagerClient mediaManagerClient;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<CategoryDto> getAll(Specification<Category> spec, Pageable pageable) {
        Page<CategoryDto> page = categoryRepository.findAll(spec, pageable)
                .map(CategoryMapper::toDto)
                .map(this::injectImages);
        return PaginatedResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<CategoryLightDto> getAllLight(Specification<Category> spec, Pageable pageable) {
        Page<Category> categoryPage = categoryRepository.findAll(spec, pageable);

        List<UUID> imageIds = categoryPage.getContent().stream()
                .map(Category::getImageId)
                .toList();

        Map<UUID, FileLightDto> fileLightDtoMap = getImages(imageIds);

        Page<CategoryLightDto> page = categoryPage.map(category -> {
            FileLightDto fileLightDto = fileLightDtoMap.get(category.getImageId());

            return CategoryLightDto.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .slug(category.getSlug())
                    .imageId(category.getImageId())
                    .image(fileLightDto)
                    .build();
        });

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
                .map(this::injectImages)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryExceptions::categoryNotFound);
        return injectImageUrl(CategoryMapper.toDto(category));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(CategoryExceptions::categoryNotFound);
        return injectImageUrl(CategoryMapper.toDto(category));
    }

    // TODO: remove these tow method, cause get all can do the same functionality
    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getRoots() {
        return categoryRepository.findByParentIsNull().stream()
                .map(CategoryMapper::toDto)
                .map(this::injectImages)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getChildren(Long parentId) {
        return categoryRepository.findByParentId(parentId).stream()
                .map(CategoryMapper::toDto)
                .map(this::injectImages)
                .toList();
    }

    @Override
    public CategoryDto create(CategoryDto dto) {
        if (slugExists(dto.getSlug())) {
            throw CategoryExceptions.slugExists();
        }

        validateCategoryParent(dto);
        validateAudienceConfigAllowed(dto);
        validateAudienceConfig(dto);

        FileDto categoryImage = getAndValidatedImage(dto.getImageId());
        validateAudienceConfigImages(dto.getAudienceConfig());

        Category category = CategoryMapper.toEntity(dto);
        Category saved = categoryRepository.save(category);

        return injectImages(CategoryMapper.toDto(saved, categoryImage));
    }

    @Override
    public CategoryDto update(CategoryDto dto) {
        Category existing = categoryRepository.findById(dto.getId())
                .orElseThrow(CategoryExceptions::categoryNotFound);

        if (!existing.getSlug().equals(dto.getSlug()) && slugExists(dto.getSlug())) {
            throw CategoryExceptions.slugExists();
        }

        validateCategoryParent(dto);
        validateAudienceConfigAllowed(dto);
        validateAudienceConfig(dto);
//  TODO : consider the existing config in validation
        FileDto categoryImage = getAndValidatedImage(dto.getImageId());
        validateAudienceConfigImages(dto.getAudienceConfig());

        if (Boolean.TRUE.equals(existing.getIsActive()) && Boolean.FALSE.equals(dto.getIsActive())) {
            deactivation(existing);
        }
        if (Boolean.FALSE.equals(existing.getIsActive()) && Boolean.TRUE.equals(dto.getIsActive())) {
            activation(existing);
        }

        existing.setName(dto.getName());
        existing.setSlug(dto.getSlug());
        existing.setImageId(dto.getImageId());
        existing.setTargetedAudience(dto.getTargetedAudience());
        existing.setAgeGroup(dto.getAgeGroup());
        existing.setIsActive(dto.getIsActive());
        existing.setParent(dto.getParentId() != null
                ? Category.builder().id(dto.getParentId()).build()
                : null);

        syncAudienceConfigs(existing, dto.getAudienceConfig());

        Category saved = categoryRepository.save(existing);
        return injectImages(CategoryMapper.toDto(saved, categoryImage));
    }

    @Override
    public CategoryDto addAudienceConfig(Long categoryId, CategoryAudienceConfigDto categoryAudienceConfigDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(CategoryExceptions::categoryNotFound);

        CategoryDto categoryDto = CategoryMapper.toDto(category);
        categoryDto.getAudienceConfig().add(categoryAudienceConfigDto);

        if (!isAudienceConfigAllowed(categoryDto)) {
            throw CategoryExceptions.audienceConfigNotAllowed();
        }
        validateAudienceConfig(categoryDto);
        getAndValidatedImage(categoryAudienceConfigDto.getImageId());

        CategoryAudienceConfig config = CategoryAudienceConfigMapper.toEntity(categoryAudienceConfigDto, category);
        category.getAudienceConfig().add(config);
        Category saved = categoryRepository.save(category);

        return CategoryMapper.toDto(saved);
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

    @Override
    public void removeAudienceConfig(Long categoryId, Long id) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(CategoryExceptions::categoryNotFound);
        boolean removed = category.getAudienceConfig().removeIf(config -> config.getId().equals(id));

        if (!removed) {
            throw CategoryExceptions.categoryAudienceConfigNotFound();
        }
        categoryRepository.save(category);
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


    // Audience Validation

    private void syncAudienceConfigs(Category category, Set<CategoryAudienceConfigDto> dtos) {
        if (dtos == null) {
            category.getAudienceConfig().clear();
            return;
        }

        category.getAudienceConfig().removeIf(existingConfig ->
                dtos.stream().noneMatch(dto -> dto.getId() != null && dto.getId().equals(existingConfig.getId()))
        );

        for (CategoryAudienceConfigDto dto : dtos) {
            if (dto.getId() == null) {
                var newConfig = CategoryAudienceConfigMapper.toEntity(dto, category);
                category.getAudienceConfig().add(newConfig);
            } else {
                var existingConfig = category.getAudienceConfig().stream()
                        .filter(config -> dto.getId().equals(config.getId()))
                        .findFirst()
                        .orElseThrow(CategoryExceptions::categoryAudienceConfigNotFound);

                existingConfig.setAgeGroup(dto.getAgeGroup());
                existingConfig.setTargetedAudience(dto.getTargetedAudience());
                existingConfig.setImageId(dto.getImageId());
            }
        }
    }

    private void validateAudienceConfigAllowed(CategoryDto dto) {
        boolean fullySpecific = !isAudienceConfigAllowed(dto);

        if (fullySpecific && dto.getAudienceConfig() != null && !dto.getAudienceConfig().isEmpty()) {
            throw CategoryExceptions.audienceConfigNotAllowed();
        }
    }

    private boolean isAudienceConfigAllowed(CategoryDto dto) {
        return !(dto.getAgeGroup() != AgeGroup.ALL
                && dto.getTargetedAudience() != TargetedAudience.ALL);
    }

    private void validateAudienceConfig(CategoryDto dto) {
        Set<CategoryAudienceConfigDto> configs = dto.getAudienceConfig();
        if (configs == null || configs.isEmpty()) {
            return;
        }

        Set<String> keys = new HashSet<>();
        keys.add(dto.getTargetedAudience().name() + ":" + dto.getAgeGroup().name());

        for (var config : configs) {
            if (!isAudienceSubset(dto, config)) {
                throw CategoryExceptions.audienceConfigOutsideCategoryScope();
            }
            String key = config.getTargetedAudience().name() + ":" + config.getAgeGroup().name();
            if (!keys.add(key)) {
                throw CategoryExceptions.duplicateAudienceConfig();
            }
        }
    }

    private boolean isAudienceSubset(CategoryDto dto, CategoryAudienceConfigDto config) {
        boolean audienceMatches =
                dto.getTargetedAudience() == TargetedAudience.ALL
                        || dto.getTargetedAudience() == config.getTargetedAudience();

        boolean ageMatches =
                dto.getAgeGroup() == AgeGroup.ALL
                        || dto.getAgeGroup() == config.getAgeGroup();

        boolean bothGeneral =
                config.getTargetedAudience() == TargetedAudience.ALL
                        && config.getAgeGroup() == AgeGroup.ALL;

        return audienceMatches && ageMatches && !bothGeneral;
    }


    // Image Injection

    private CategoryDto injectImages(CategoryDto dto) {
        dto = injectImageUrl(dto);

        if (dto.getAudienceConfig() != null && !dto.getAudienceConfig().isEmpty()) {
            dto.setAudienceConfig(
                    dto.getAudienceConfig().stream()
                            .map(this::injectAudienceConfigImage)
                            .collect(Collectors.toSet())
            );
        }

        return dto;
    }

    private CategoryAudienceConfigDto injectAudienceConfigImage(
            CategoryAudienceConfigDto dto
    ) {
        try {
            MediaResponse<FileDto> response = mediaManagerClient.getById(dto.getImageId());
            dto.setImage(response.getData());
            return dto;
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw CategoryExceptions.imageNotFound();
            }
            log.error("Could not fetch audience config image from MediaManager imageId={}, status={}, message={}",
                    dto.getImageId(), e.status(), e.getMessage());
            throw e;
        }
    }

    private CategoryDto injectImageUrl(CategoryDto dto) {
        try {
            MediaResponse<FileDto> response = mediaManagerClient.getById(dto.getImageId());
            dto.setImage(response.getData());
            return dto;

        } catch (FeignException e) {
            if (e.status() == 404) {
                throw CategoryExceptions.imageNotFound();
            }
            log.error("Could not fetch image File from MediaManager imageId={}, status={}, message={}",
                    dto.getImageId(), e.status(), e.getMessage()
            );
            throw e;
        }
    }

    private CategoryLightDto injectLightImageUrl(CategoryLightDto dto) {
        try {
            MediaResponse<FileDto> response = mediaManagerClient.getById(dto.getImageId());
            FileLightDto lightDto = new FileLightDto();
            lightDto.setId(response.getData().getId());
            lightDto.setSmallFileUrl(response.getData().getSmallFileUrl());
            dto.setImage(lightDto);
            return dto;

        } catch (FeignException e) {
            if (e.status() == 404) {
                throw CategoryExceptions.imageNotFound();
            }
            log.error("Could not fetch image File  from MediaManager imageId={}, status={}, message={}",
                    dto.getImageId(), e.status(), e.getMessage()
            );
            throw e;
        }
    }

    private Map<UUID, FileLightDto> getImages(List<UUID> imageIds) {

        if (imageIds == null || imageIds.isEmpty()) {
            return null;
        }

        try {
            // TODO REPLACE WITH endpoint that's return FileLightDto
            MediaResponse<List<FileDto>> response = mediaManagerClient.getById(imageIds);

            // validate response not empty
            if (response == null || response.getData() == null) {
                throw CategoryExceptions.imageNotFound();
            }
            //inject image File
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


    // Image Validation
    private boolean isImage(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }

    private FileDto getAndValidatedImage(UUID imageId) {
        try {
            MediaResponse<FileDto> response = mediaManagerClient.getById(imageId);
            // validate response not empty
            if (response == null || response.getData() == null) {
                throw CategoryExceptions.imageCouldNotBeValidated();
            }

            FileDto fileDto = response.getData();

            // validate file type
            if (!isImage(fileDto.getMimeType())) {
                throw CategoryExceptions.invalidFileType();
            }

            return response.getData();

        } catch (FeignException e) {
            if (e.status() == 404) {
                throw CategoryExceptions.imageNotFound();
            }
            throw CategoryExceptions.imageCouldNotBeValidated();
        }
    }

    private void validateAudienceConfigImages(Set<CategoryAudienceConfigDto> configs) {
        if (configs == null || configs.isEmpty()) {
            return;
        }

        for (var config : configs) {
            getAndValidatedImage(config.getImageId());
        }
    }

}
