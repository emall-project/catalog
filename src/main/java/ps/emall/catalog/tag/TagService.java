package ps.emall.catalog.tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Set;

public interface TagService {

    Page<TagDto> getAll(Specification<Tag> spec, Pageable pageable);

    List<TagDto> getAllTagsList(Specification<Tag> spec);

    TagDto findById(Long id);

    TagDto findByName(String name);

    TagDto create(TagDto dto);

    TagDto update(TagDto dto);

    void delete(Long id);

    Set<TagDto> resolveTags(Set<String> tagNames);
}
