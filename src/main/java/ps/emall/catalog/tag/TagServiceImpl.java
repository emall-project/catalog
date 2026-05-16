package ps.emall.catalog.tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ps.emall.catalog.common.page.PaginatedResponse;
import ps.emall.catalog.product.ProductRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final ProductRepository productRepository;
    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<TagDto> getAll(Specification<Tag> spec, Pageable pageable) {
        Page<TagDto> page =  tagRepository.findAll(spec, pageable)
                .map(TagMapper::toDto);
        return PaginatedResponse.of(page);
    }

    @Override
    public List<TagDto> getAllTagsList(Specification<Tag> spec) {
        List<Tag> tags = (spec == null)
                ? tagRepository.findAll()
                : tagRepository.findAll(spec);

        return tags.stream()
                .map(TagMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TagDto findById(Long id) {
        return tagRepository.findById(id)
                .map(TagMapper::toDto)
                .orElseThrow(TagExceptions::tagNotFound);
    }

    @Override
    @Transactional(readOnly = true)
    public TagDto findByName(String name) {
        return tagRepository.findByNameIgnoreCase(name)
                .map(TagMapper::toDto)
                .orElseThrow(TagExceptions::tagNotFound);
    }

    @Override
    public TagDto create(TagDto dto) {
        if (tagRepository.existsByNameIgnoreCase(dto.getName())) {
            throw TagExceptions.nameExists();
        }

        Tag tag = TagMapper.toEntity(dto);
        Tag saved = tagRepository.save(tag);
        return TagMapper.toDto(saved);
    }

    @Transactional
    public List<TagDto> resolveTags(List<TagDto> tagNames) {
        log.info("Resolving tags");
        return tagNames.stream()
                .map(tag -> tag.getName())
                .map(this::normalize)
                .map(this::findOrCreate)
                .map(TagMapper::toDto)
                .toList();
    }

    @Override
    public TagDto update(TagDto dto) {
        Tag existing = tagRepository.findById(dto.getId())
                .orElseThrow(TagExceptions::tagNotFound);

        if (!existing.getName().equalsIgnoreCase(dto.getName())
                && tagRepository.existsByNameIgnoreCase(dto.getName())) {
            throw TagExceptions.nameExists();
        }

        existing.setName(dto.getName());
        Tag saved = tagRepository.save(existing);
        return TagMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(TagExceptions::tagNotFound);

        if(productRepository.existsByTags_Id(id)){
            throw TagExceptions.tagHasProducts();
        }
        tagRepository.delete(tag);
    }
    private String normalize(String tag) {
        return tag.trim().toLowerCase();
    }
    private Tag findOrCreate(String name) {
        return tagRepository.findByName(name)
                .orElseGet(() -> {
                    Tag tag = new Tag();
                    tag.setName(name);
                    return tagRepository.save(tag);
                });
    }
}
