package ps.emall.catalog.category;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ps.emall.catalog.category.audience_config.CategoryAudienceConfigDto;
import ps.emall.catalog.category.audience_config.CategoryAudienceConfigMapper;
import ps.emall.catalog.client.media_manager.FileDto;
import ps.emall.catalog.client.media_manager.FileLightDto;
import ps.emall.catalog.client.media_manager.MediaManagerClient;
import ps.emall.catalog.client.media_manager.MediaResponse;
import ps.emall.catalog.common.audience.AgeGroup;
import ps.emall.catalog.common.audience.TargetedAudience;
import ps.emall.catalog.common.util.MediaManagerHelper;
import ps.emall.catalog.product.ProductRepository;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceHelper {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final MediaManagerClient mediaManagerClient;
    private final MediaManagerHelper mediaManagerHelper;
    public void deactivation(Category category) {

        List<Category> children = categoryRepository.findByParentId(category.getId());
        // TODO: make sure this wont cause any stack issues
        for (Category child : children) {
            deactivation(child);
        }

        if (Boolean.FALSE.equals(category.getIsActive())) {
            return;
        }

        // TODO:  notify vendor who have products attached to this Category
        productRepository.deactivateProductsByCategoryId(category.getId());
        category.setIsActive(Boolean.FALSE);
        categoryRepository.save(category);
    }

    public void activation(Category category) {

        List<Category> children = categoryRepository.findByParentId(category.getId());

        for (Category child : children) {
            activation(child);
        }
        // TODO:  notify vendor how have products attached to this Category
        if (Boolean.TRUE.equals(category.getIsActive())) {
            return;
        }

        productRepository.activateProductsByCategoryId(category.getId());
        category.setIsActive(Boolean.TRUE);
        categoryRepository.save(category);

    }

    public void validateCategoryParent(CategoryDto dto) {
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

    public boolean isCircularHierarchy(Long categoryId, Long parentId) {
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

    public void syncAudienceConfigs(Category category, Set<CategoryAudienceConfigDto> dtos) {
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

    public void validateAudienceConfigAllowed(CategoryDto dto) {
        boolean fullySpecific = !isAudienceConfigAllowed(dto);

        if (fullySpecific && dto.getAudienceConfig() != null && !dto.getAudienceConfig().isEmpty()) {
            throw CategoryExceptions.audienceConfigNotAllowed();
        }
    }

    public boolean isAudienceConfigAllowed(CategoryDto dto) {
        return !(dto.getAgeGroup() != AgeGroup.ALL
                && dto.getTargetedAudience() != TargetedAudience.ALL);
    }

    public void validateAudienceConfig(CategoryDto dto) {
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

    public boolean isAudienceSubset(CategoryDto dto, CategoryAudienceConfigDto config) {
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

    public CategoryDto injectImages(CategoryDto dto) {
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

    public CategoryAudienceConfigDto injectAudienceConfigImage(
            CategoryAudienceConfigDto dto
    ) {
        FileDto image = mediaManagerHelper.getAndValidatedImage(dto.getImageId());
        dto.setImage(image);
        return dto;
    }

    public CategoryDto injectImageUrl(CategoryDto dto) {
        FileDto image = mediaManagerHelper.getAndValidatedImage(dto.getImageId());
        dto.setImage(image);
        return dto;
    }

    // Image Validation

    public void validateAudienceConfigImages(Set<CategoryAudienceConfigDto> configs) {
        if (configs == null || configs.isEmpty()) {
            return;
        }

        for (var config : configs) {
            mediaManagerHelper.getAndValidatedImage(config.getImageId());
        }
    }

}
