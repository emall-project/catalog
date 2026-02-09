package ps.emall.catalog.tag;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<TagDto> getAll(Specification<Tag> spec, Pageable pageable) {
        return tagRepository.findAll(spec, pageable)
                .map(TagMapper::toDto);
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

        // TODO: prevent delete if products reference this tag
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
