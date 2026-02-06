package ps.emall.catalog.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryDto> getAll(Specification<Category> spec, Pageable pageable) {
        return categoryRepository.findAll(spec, pageable)
                .map(CategoryMapper::toDto);
    }


    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategoryList(CategorySpec spec) {
        List<Category> categories = (spec == null)
                ? categoryRepository.findAll()
                : categoryRepository.findAll(spec);


        return categories.stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto create(CategoryDto dto) {
        if (slugExists(dto.getSlug())) {
            throw CategoryExceptions.slugExists();
        }
        validateCategoryParent(dto);

        //  TODO: uncomment when adding implement isFileExist in Media-manager
//        if (!mediaManagerService.isFileExist(dto.getImageFileKey())) {
//            throw CategoryExceptions.imageNotFound();
//        }

        Category category = CategoryMapper.toEntity(dto);
        Category saved = categoryRepository.save(category);
        return CategoryMapper.toDto(saved);
    }

    @Override
    public CategoryDto update(CategoryDto dto) {
        Category existing = categoryRepository.findById(dto.getId())
                .orElseThrow(CategoryExceptions::categoryNotFound);

        if (!existing.getSlug().equals(dto.getSlug()) && slugExists(dto.getSlug())) {
            throw CategoryExceptions.slugExists();
        }
        validateCategoryParent(dto);

        //  TODO: uncomment when adding implement isFileExist in Media-manager
//        if (!mediaManagerService.isFileExist(dto.getImageFileKey())) {
//            throw CategoryException.imageNotFound();
//        }

        if(existing.getIsActive().equals(true) &&dto.getIsActive().equals(false)) {
            deactivation(existing);// apply deactivation side effects
        }
        if(existing.getIsActive().equals(false) &&dto.getIsActive().equals(true)) {
            activate(existing);// apply activation side effects
        }

        existing.setName(dto.getName());
        existing.setSlug(dto.getSlug());
        existing.setImageFileKey(dto.getImageFileKey());
        existing.setTargetedAudience(dto.getTargetedAudience());
        existing.setAgeGroup(dto.getAgeGroup());
        existing.setIsActive(dto.getIsActive());
        existing.setParent(dto.getParentId() != null ?
                Category.builder().id(dto.getParentId()).build() : null);

        Category saved = categoryRepository.save(existing);
        return CategoryMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryExceptions::categoryNotFound);
        return CategoryMapper.toDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(CategoryExceptions::categoryNotFound);
        return CategoryMapper.toDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getRoots() {
        return categoryRepository.findByParentIsNull().stream()
                .map(CategoryMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getChildren(Long parentId) {
        return categoryRepository.findByParentId(parentId).stream()
                .map(CategoryMapper::toDto)
                .toList();
    }


    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryExceptions::categoryNotFound);

        long childCount = categoryRepository.countByParentId(id);
        if (childCount > 0) {
            throw CategoryExceptions.categoryHasChildren();
        }
//         TODO:  throw error if there products attached to this category
//        if(){
//            throw CategoryExceptions.categoryHasProducts();
//        }
        categoryRepository.delete(category);
    }

    private void deactivation(Category category) {

        List<Category> children = categoryRepository.findByParentId(category.getId());
        // TODO: make sure this wont cause any stack issues
        for (Category child : children) {
            deactivation(child);
        }
        // TODO:  make all product inactive when deactivate their category
        // TODO:  notify vendor how have products attached to this Category

        if(Boolean.FALSE.equals(category.getIsActive())){
            return;
        }
        category.setIsActive(Boolean.FALSE);
        categoryRepository.save(category);
    }

    private void activate(Category category) {

        long childCount = categoryRepository.countByParentId(category.getId());
        // TODO: Check where you need to activate all children or not
//        for (Category child : children) {
//            deactivation(child);
//        }
        // TODO:  make all product inactive when deactivate their category
        // TODO:  notify vendor how have products attached to this Category
        if(Boolean.TRUE.equals(category.getIsActive())){
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

        // TODO : this won't fix the problem cause the dto.getId() will return null so u ganna have to be creative
//            if (dto.getParentId().equals(dto.getId())) {
//                throw CategoryExceptions.selfParenting();
//            }

            if (isCircularHierarchy(dto.getId(), dto.getParentId())) {
                throw CategoryExceptions.circularHierarchy();
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
}
