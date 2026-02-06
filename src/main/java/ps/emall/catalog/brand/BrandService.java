package ps.emall.catalog.brand;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface BrandService {

    Page<BrandDto> getAll(Specification<Brand> spec, Pageable pageable);

    List<BrandDto> getAllBrandsList(Specification<Brand> spec);

    BrandDto create(BrandDto dto);

    BrandDto update(BrandDto dto);

    BrandDto findById(Long id);

    BrandDto findBySlug(String slug);

    void delete(Long id);

}
