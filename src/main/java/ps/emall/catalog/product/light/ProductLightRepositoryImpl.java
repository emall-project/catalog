package ps.emall.catalog.product.light;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class ProductLightRepositoryImpl implements ProductLightRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ProductLightRow> findLightRowsByProductIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return List.of();
        }

        return entityManager.createQuery("""
                        select
                            p.id as productId,
                            p.name as productName,
                            p.slug as productSlug,
                            dv.id as defaultVariantId,
                            dv.basePrice as basePrice,
                            pm.mediumId as mediumId
                        from Product p
                        left join p.defaultVariant dv
                        left join ProductMedium pm
                            on pm.variant.id = dv.id
                           and pm.sortOrder = (
                                select min(pm2.sortOrder)
                                from ProductMedium pm2
                                where pm2.variant.id = dv.id
                           )
                        where p.id in :productIds
                        """, Tuple.class)
                .setParameter("productIds", productIds)
                .getResultList()
                .stream()
                .map(tuple -> new ProductLightRowImpl(
                        tuple.get("productId", Long.class),
                        tuple.get("productName", String.class),
                        tuple.get("productSlug", String.class),
                        tuple.get("defaultVariantId", Long.class),
                        tuple.get("basePrice", BigDecimal.class),
                        tuple.get("mediumId", UUID.class)
                ))
                .collect(Collectors.toList());
    }

}