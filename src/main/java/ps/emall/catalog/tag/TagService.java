package ps.emall.catalog.tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import ps.emall.catalog.common.page.PaginatedResponse;

import java.util.List;
import java.util.Set;

@Repository
public interface TagService {

    PaginatedResponse<TagDto> getAll(Specification<Tag> spec, Pageable pageable);

    List<TagDto> getAllTagsList(Specification<Tag> spec);

    TagDto findById(Long id);

    TagDto findByName(String name);

    TagDto create(TagDto dto);

    TagDto update(TagDto dto);

    void delete(Long id);

    List<TagDto> resolveTags(List<TagDto> tagNames);
}
