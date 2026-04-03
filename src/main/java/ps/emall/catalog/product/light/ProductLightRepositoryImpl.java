package ps.emall.catalog.product.light;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import ps.emall.catalog.product.Product;

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

    public List<Long> findIdsBySpecification(Specification<Product> spec) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Product> root = query.from(Product.class);

        query.select(root.get("id"))
                .where(spec.toPredicate(root, query, cb));

        return entityManager.createQuery(query).getResultList();
    }
    @Override
    public Page<Long> findIdsBySpecification(Specification<Product> spec, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // main query
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Product> root = query.from(Product.class);

        Predicate predicate = cb.conjunction();
        if (spec != null) {
            Predicate specPredicate = spec.toPredicate(root, query, cb);
            if (specPredicate != null) {
                predicate = specPredicate;
            }
        }

        query.select(root.get("id")).where(predicate).distinct(true);

        // apply sorting from pageable if present
        if (pageable.getSort().isSorted()) {
            List<Order> orders = pageable.getSort().stream()
                    .map(order -> order.isAscending()
                            ? cb.asc(root.get(order.getProperty()))
                            : cb.desc(root.get(order.getProperty())))
                    .toList();
            query.orderBy(orders);
        }

        List<Long> ids = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        // count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Product> countRoot = countQuery.from(Product.class);

        Predicate countPredicate = cb.conjunction();
        if (spec != null) {
            Predicate specPredicate = spec.toPredicate(countRoot, countQuery, cb);
            if (specPredicate != null) {
                countPredicate = specPredicate;
            }
        }

        countQuery.select(cb.countDistinct(countRoot)).where(countPredicate);

        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(ids, pageable, total);
    }
}