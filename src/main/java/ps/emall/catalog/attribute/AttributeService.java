package ps.emall.catalog.attribute;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

public interface AttributeService {

    Page<AttributeDto> getAll(AttributeFilter filter, Pageable pageable);

    List<AttributeDto> getAllList(AttributeFilter filter);

    AttributeDto create(AttributeDto dto);

    AttributeDto update(AttributeDto dto);

    AttributeDto findById(Long id);

    AttributeDto findActiveById(Long id);

    AttributeDto findBySlug(String slug);

    AttributeDto findActiveBySlug(String slug);

    void delete(Long id);
}
