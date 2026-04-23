package ps.emall.catalog.brand;

import org.springframework.data.domain.Pageable;
import ps.emall.catalog.common.page.PaginatedResponse;

import java.util.List;

public interface BrandService {

    PaginatedResponse<BrandDto> getAll(BrandFilter filter, Pageable pageable);

    List<BrandDto> getAllBrandsList(BrandFilter filter);

    BrandDto create(BrandDto dto);

    BrandDto update(BrandDto dto);

    BrandDto getById(Long id);

    BrandDto getActiveById(Long id);

    BrandDto getBySlug(String slug);

    BrandDto getActiveBySlug(String slug);

    void delete(Long id);

}
