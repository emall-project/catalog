package ps.emall.catalog.brand.dashboard;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductBrandDistributionRepository {

    private final EntityManager entityManager;

    public List<ProductBrandDistributionDto> getProductDistributionByBrand() {
        return entityManager.createQuery("""
                        SELECT new ps.emall.catalog.brand.dashboard.ProductBrandDistributionDto(
                            b.id,
                            b.name,
                            COUNT(p.id)
                        )
                        FROM Brand b
                        LEFT JOIN Product p ON p.brand = b
                        GROUP BY b.id, b.name
                        ORDER BY COUNT(p.id) DESC, b.name ASC
                        """, ProductBrandDistributionDto.class)
                .getResultList();
    }
}
