package ps.emall.catalog.product.dashboard;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ProductDashboardRepositoryImpl implements ProductDashboardRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public ProductDashboardKpisDto getKpis(Long storeId) {
        Object[] row = singleRow("""
                SELECT COUNT(p.id),
                       SUM(CASE WHEN p.isActive = true THEN 1 ELSE 0 END),
                       SUM(CASE WHEN p.isActive = false THEN 1 ELSE 0 END)
                FROM Product p
                WHERE p.storeId = :storeId
                """, storeId);

        return new ProductDashboardKpisDto(
                longValue(row[0]),
                longValue(row[1]),
                longValue(row[2])
        );
    }

    @Override
    public ProductDashboardVariantKpisDto getVariantKpis(Long storeId) {
        long totalProducts = longValue(em.createQuery("""
                        SELECT COUNT(p.id)
                        FROM Product p
                        WHERE p.storeId = :storeId
                        """)
                .setParameter("storeId", storeId)
                .getSingleResult());

        long totalVariants = longValue(em.createQuery("""
                        SELECT COUNT(v.id)
                        FROM ProductVariant v
                        WHERE v.product.storeId = :storeId
                        """)
                .setParameter("storeId", storeId)
                .getSingleResult());

        Object[] row = singleRow("""
                SELECT SUM(CASE WHEN SIZE(p.variants) = 1 THEN 1 ELSE 0 END),
                       SUM(CASE WHEN SIZE(p.variants) > 1 THEN 1 ELSE 0 END)
                FROM Product p
                WHERE p.storeId = :storeId
                """, storeId);

        BigDecimal averageVariantsPerProduct = totalProducts == 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(totalVariants)
                .divide(BigDecimal.valueOf(totalProducts), 2, RoundingMode.HALF_UP);

        return new ProductDashboardVariantKpisDto(
                totalVariants,
                averageVariantsPerProduct,
                longValue(row[0]),
                longValue(row[1])
        );
    }

    @Override
    public ProductDashboardTagCoverageDto getTagCoverage(Long storeId) {
        Object[] row = singleRow("""
                SELECT SUM(CASE WHEN SIZE(p.tags) > 0 THEN 1 ELSE 0 END),
                       SUM(CASE WHEN SIZE(p.tags) = 0 THEN 1 ELSE 0 END)
                FROM Product p
                WHERE p.storeId = :storeId
                """, storeId);

        return new ProductDashboardTagCoverageDto(
                longValue(row[0]),
                longValue(row[1])
        );
    }

    @Override
    public ProductDashboardPriceStatsDto getPriceStats(Long storeId) {
        Object[] row = singleRow("""
                SELECT MIN(p.defaultVariant.basePrice),
                       MAX(p.defaultVariant.basePrice),
                       AVG(p.defaultVariant.basePrice)
                FROM Product p
                WHERE p.storeId = :storeId
                  AND p.defaultVariant IS NOT NULL
                """, storeId);

        return new ProductDashboardPriceStatsDto(
                bigDecimalValue(row[0]),
                bigDecimalValue(row[1]),
                bigDecimalValue(row[2])
        );
    }

    @Override
    public List<NamedDistributionRowDto> getCategoryDistribution(Long storeId) {
        return em.createQuery("""
                        SELECT p.category.id,
                               p.category.name,
                               COUNT(p.id),
                               SUM(CASE WHEN p.isActive = true THEN 1 ELSE 0 END)
                        FROM Product p
                        WHERE p.storeId = :storeId
                        GROUP BY p.category.id, p.category.name
                        ORDER BY COUNT(p.id) DESC, p.category.name ASC
                        """, Object[].class)
                .setParameter("storeId", storeId)
                .getResultList()
                .stream()
                .map(row -> new NamedDistributionRowDto(
                        (Long) row[0],
                        (String) row[1],
                        longValue(row[2]),
                        longValue(row[3])
                ))
                .toList();
    }

    @Override
    public List<NamedDistributionRowDto> getBrandDistribution(Long storeId) {
        return em.createQuery("""
                        SELECT p.brand.id,
                               p.brand.name,
                               COUNT(p.id),
                               SUM(CASE WHEN p.isActive = true THEN 1 ELSE 0 END)
                        FROM Product p
                        WHERE p.storeId = :storeId
                        GROUP BY p.brand.id, p.brand.name
                        ORDER BY COUNT(p.id) DESC, p.brand.name ASC
                        """, Object[].class)
                .setParameter("storeId", storeId)
                .getResultList()
                .stream()
                .map(row -> new NamedDistributionRowDto(
                        (Long) row[0],
                        (String) row[1],
                        longValue(row[2]),
                        longValue(row[3])
                ))
                .toList();
    }

    @Override
    public List<EnumDistributionRowDto> getAudienceDistribution(Long storeId) {
        return getEnumDistribution(storeId, "targetedAudience");
    }

    @Override
    public List<EnumDistributionRowDto> getAgeDistribution(Long storeId) {
        return getEnumDistribution(storeId, "ageGroup");
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ProductCreatedByMonthDto> getProductsCreatedByMonth(Long storeId, LocalDateTime fromInclusive) {
        return em.createNativeQuery("""
                        SELECT TO_CHAR(DATE_TRUNC('month', p.created_at), 'YYYY-MM') AS month_key,
                               COUNT(p.id) AS total_products,
                               SUM(CASE WHEN p.is_active = true THEN 1 ELSE 0 END) AS active_products
                        FROM catalog.products p
                        WHERE p.store_id = :storeId
                          AND p.created_at >= :fromInclusive
                        GROUP BY month_key
                        ORDER BY month_key ASC
                        """)
                .setParameter("storeId", storeId)
                .setParameter("fromInclusive", Timestamp.valueOf(fromInclusive))
                .getResultList()
                .stream()
                .map(rowObj -> {
                    Object[] row = (Object[]) rowObj;
                    return new ProductCreatedByMonthDto(
                            (String) row[0],
                            longValue(row[1]),
                            longValue(row[2])
                    );
                })
                .toList();
    }

    private List<EnumDistributionRowDto> getEnumDistribution(Long storeId, String fieldName) {
        String jpql = """
                SELECT p.%s,
                       COUNT(p.id),
                       SUM(CASE WHEN p.isActive = true THEN 1 ELSE 0 END)
                FROM Product p
                WHERE p.storeId = :storeId
                GROUP BY p.%s
                ORDER BY COUNT(p.id) DESC, p.%s ASC
                """.formatted(fieldName, fieldName, fieldName);

        return em.createQuery(jpql, Object[].class)
                .setParameter("storeId", storeId)
                .getResultList()
                .stream()
                .map(row -> new EnumDistributionRowDto(
                        row[0] == null ? null : row[0].toString(),
                        longValue(row[1]),
                        longValue(row[2])
                ))
                .toList();
    }

    private Object[] singleRow(String jpql, Long storeId) {
        return (Object[]) em.createQuery(jpql)
                .setParameter("storeId", storeId)
                .getSingleResult();
    }

    private long longValue(Object value) {
        return value == null ? 0L : ((Number) value).longValue();
    }

    private BigDecimal bigDecimalValue(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(((Number) value).doubleValue()).setScale(2, RoundingMode.HALF_UP);
    }
}
