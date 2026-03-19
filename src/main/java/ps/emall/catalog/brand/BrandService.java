package ps.emall.catalog.brand;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ps.emall.catalog.common.page.PaginatedResponse;

import java.util.List;

public interface BrandService {

    PaginatedResponse<BrandDto> getAll(Specification<Brand> spec, Pageable pageable);

    List<BrandDto> getAllBrandsList(Specification<Brand> spec);

    BrandDto create(BrandDto dto);

    BrandDto update(BrandDto dto);

    BrandDto findById(Long id);

    BrandDto findBySlug(String slug);

    void delete(Long id);

}
