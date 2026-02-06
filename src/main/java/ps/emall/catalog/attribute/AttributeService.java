package ps.emall.catalog.attribute;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface AttributeService {

    Page<AttributeDto> getAll(Specification<Attribute> spec, Pageable pageable);

    List<AttributeDto> getAllList(Specification<Attribute> spec);

    AttributeDto create(AttributeDto dto);

    AttributeDto update(AttributeDto dto);

    AttributeDto findById(Long id);

    AttributeDto findBySlug(String slug);

    void delete(Long id);
}
