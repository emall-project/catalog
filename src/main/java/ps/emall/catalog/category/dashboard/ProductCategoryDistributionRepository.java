package ps.emall.catalog.category.dashboard;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductCategoryDistributionRepository {

    private final EntityManager entityManager;

    public List<ProductCategoryDistributionDto> getProductDistributionByCategory() {
        return entityManager.createQuery("""
                        SELECT new ps.emall.catalog.category.dashboard.ProductCategoryDistributionDto(
                            c.id,
                            c.name,
                            COUNT(p.id)
                        )
                        FROM Category c
                        LEFT JOIN Product p ON p.category = c
                        GROUP BY c.id, c.name
                        ORDER BY COUNT(p.id) DESC, c.name ASC
                        """, ProductCategoryDistributionDto.class)
                .getResultList();
    }
}
