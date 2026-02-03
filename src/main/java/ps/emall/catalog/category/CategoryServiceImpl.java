package ps.emall.catalog.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;


    @Override
    public CategoryDto create(CategoryDto dto) {
        if (slugExists(dto.getSlug())) {
            throw CategoryExceptions.slugExists();
        }

        validateCategoryParent(dto);

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

        existing.setName(dto.getName());
        existing.setSlug(dto.getSlug());
        existing.setParent(dto.getParentId() != null ?
                Category.builder().id(dto.getParentId()).build() : null);

        Category saved = categoryRepository.save(existing);
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
        categoryRepository.delete(category);
    }

    @Override
    public void deactivate(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryExceptions::categoryNotFound);

        long childCount = categoryRepository.countByParentId(id);
        if (childCount > 0) {
            // TODO: check if you ganna have to make all product inactive when deactivate their category
        }
        if(Boolean.FALSE.equals(category.isActive())){
            return;
        }
        category.setActive(Boolean.FALSE);
        categoryRepository.save(category);
    }

    @Override
    public void activate(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryExceptions::categoryNotFound);

        long childCount = categoryRepository.countByParentId(id);
        if (childCount > 0) {
            // TODO: check if you ganna have to make all product inactive when deactivate their category
        }
        if(Boolean.TRUE.equals(category.isActive())){
            return;
        }
        category.setActive(Boolean.TRUE);
        categoryRepository.save(category);

    }

    @Override
    public CategoryDto getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryExceptions::categoryNotFound);
        return CategoryMapper.toDto(category);
    }

    @Override
    public CategoryDto getBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(CategoryExceptions::categoryNotFound);
        return CategoryMapper.toDto(category);
    }

    @Override
    public List<CategoryDto> getAll() {
        return categoryRepository.findAll().stream()
                .map(CategoryMapper::toDto)
                .toList();
    }

    @Override
    public List<CategoryDto> getRoots() {
        return categoryRepository.findByParentIsNull().stream()
                .map(CategoryMapper::toDto)
                .toList();
    }

    @Override
    public List<CategoryDto> getChildren(Long parentId) {
        return categoryRepository.findByParentId(parentId).stream()
                .map(CategoryMapper::toDto)
                .toList();
    }

    @Override
    public List<CategoryDto> search(CategorySpec spec) {
        return categoryRepository.findAll(spec).stream()
                .map(CategoryMapper::toDto)
                .toList();
    }

    @Override
    public boolean slugExists(String slug) {
        return categoryRepository.existsBySlug(slug);
    }
    private void validateCategoryParent(CategoryDto dto) {
        if (dto.getParentId() != null) {
            Category parent = categoryRepository.findById(dto.getParentId())
                    .orElseThrow(CategoryExceptions::parentNotFound);

            if (dto.getParentId().equals(dto.getId())) {
                throw CategoryExceptions.selfParenting();
            }

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
