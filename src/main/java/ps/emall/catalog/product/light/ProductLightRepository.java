package ps.emall.catalog.product.light;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ps.emall.catalog.product.Product;

import java.util.List;

public interface ProductLightRepository {
    List<ProductLightRow> findLightRowsByProductIds(List<Long> productIds);
    List<Long> findIdsBySpecification(Specification<Product> spec);
    Page<Long> findIdsBySpecification(Specification<Product> spec, Pageable pageable);

}