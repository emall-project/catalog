package ps.emall.catalog.product.summary;

import org.springframework.data.jpa.domain.Specification;
import ps.emall.catalog.product.Product;
import ps.emall.catalog.product.light.ProductLightRow;

import java.util.List;

public interface ProductSummaryRepository {


    AgeDistribution getAgeDistribution(Specification<Product> spec);

    AudienceDistribution getAudienceDistribution(Specification<Product> spec);

    List<CategoryDistribution> getCategoryDistribution(Specification<Product> spec);

    List<BrandDistribution> getBrandDistribution(Specification<Product> spec);

    PriceRange getPriceRange(Specification<Product> spec);

    List<AttributeSummary> getAttributeSummary(Specification<Product> spec);

}