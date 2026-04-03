package ps.emall.catalog.product.summary;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import ps.emall.catalog.attribute.Attribute;
import ps.emall.catalog.attribute.attribute_options.AttributeOption;
import ps.emall.catalog.brand.Brand;
import ps.emall.catalog.category.Category;
import ps.emall.catalog.common.audience.AgeGroup;
import ps.emall.catalog.common.audience.TargetedAudience;
import ps.emall.catalog.product.Product;
import ps.emall.catalog.product.light.ProductLightRow;
import ps.emall.catalog.product.light.ProductLightRowImpl;
import ps.emall.catalog.product.product_variant.ProductVariant;
import ps.emall.catalog.product.product_variant.variant_attribute.VariantAttribute;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ProductSummaryRepositoryImpl implements ProductSummaryRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public AgeDistribution getAgeDistribution(Specification<Product> spec) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Product> root = query.from(Product.class);

        Predicate predicate = cb.conjunction();
        if (spec != null) {
            Predicate specPredicate = spec.toPredicate(root, query, cb);
            if (specPredicate != null) {
                predicate = specPredicate;
            }
        }

        query.multiselect(
                sumCase(cb, root, AgeGroup.NEWBORN).alias("newborn"),
                sumCase(cb, root, AgeGroup.INFANT).alias("infant"),
                sumCase(cb, root, AgeGroup.TODDLER).alias("toddler"),
                sumCase(cb, root, AgeGroup.CHILD).alias("child"),
                sumCase(cb, root, AgeGroup.TEENAGER).alias("teenager"),
                sumCase(cb, root, AgeGroup.YOUTH).alias("youth"),
                sumCase(cb, root, AgeGroup.ADULT).alias("adult"),
                sumCase(cb, root, AgeGroup.ALL).alias("allAge")
        );

        query.where(predicate);

        Tuple tuple = entityManager.createQuery(query).getSingleResult();

        return new AgeDistribution(
                getLong(tuple, "newborn"),
                getLong(tuple, "infant"),
                getLong(tuple, "toddler"),
                getLong(tuple, "child"),
                getLong(tuple, "teenager"),
                getLong(tuple, "youth"),
                getLong(tuple, "adult"),
                getLong(tuple, "allAge")
        );
    }

    @Override
    public AudienceDistribution getAudienceDistribution(Specification<Product> spec) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Product> root = query.from(Product.class);

        Predicate predicate = cb.conjunction();
        if (spec != null) {
            Predicate specPredicate = spec.toPredicate(root, query, cb);
            if (specPredicate != null) {
                predicate = specPredicate;
            }
        }

        query.multiselect(
                sumCase(cb, root, TargetedAudience.MALE).alias("male"),
                sumCase(cb, root, TargetedAudience.FEMALE).alias("female"),
                sumCase(cb, root, TargetedAudience.ALL).alias("allAudience")
        );

        query.where(predicate);

        Tuple tuple = entityManager.createQuery(query).getSingleResult();

        return new AudienceDistribution(
                getLong(tuple, "male"),
                getLong(tuple, "female"),
                getLong(tuple, "allAudience")
        );
    }

    private Expression<Long> sumCase(CriteriaBuilder cb, Root<Product> root, AgeGroup ageGroup) {
        return cb.sum(
                cb.<Long>selectCase()
                        .when(cb.equal(root.get("ageGroup"), ageGroup), 1L)
                        .otherwise(0L)
        );
    }

    private Expression<Long> sumCase(CriteriaBuilder cb, Root<Product> root, TargetedAudience targetedAudience) {
        return cb.sum(
                cb.<Long>selectCase()
                        .when(cb.equal(root.get("targetedAudience"), targetedAudience), 1L)
                        .otherwise(0L)
        );
    }

    private Long getLong(Tuple tuple, String alias) {
        Long value = tuple.get(alias, Long.class);
        return value != null ? value : 0L;
    }

    @Override
    public List<CategoryDistribution> getCategoryDistribution(Specification<Product> spec) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Product> root = query.from(Product.class);

        Join<Product, Category> categoryJoin = root.join("category");

        Predicate predicate = cb.conjunction();
        if (spec != null) {
            Predicate specPredicate = spec.toPredicate(root, query, cb);
            if (specPredicate != null) {
                predicate = specPredicate;
            }
        }

        query.multiselect(
                categoryJoin.get("id").alias("id"),
                categoryJoin.get("name").alias("name"),
                cb.count(root.get("id")).alias("totalProduct")
        );

        query.where(predicate);
        query.groupBy(
                categoryJoin.get("id"),
                categoryJoin.get("name")
        );
        query.orderBy(cb.desc(cb.count(root.get("id"))));

        return entityManager.createQuery(query)
                .getResultList()
                .stream()
                .map(tuple -> new CategoryDistribution(
                        tuple.get("id", Long.class),
                        tuple.get("name", String.class),
                        tuple.get("totalProduct", Long.class)
                ))
                .toList();
    }

    @Override
    public List<BrandDistribution> getBrandDistribution(Specification<Product> spec) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Product> root = query.from(Product.class);

        Join<Product, Brand> brandJoin = root.join("brand");

        Predicate predicate = cb.conjunction();
        if (spec != null) {
            Predicate specPredicate = spec.toPredicate(root, query, cb);
            if (specPredicate != null) {
                predicate = specPredicate;
            }
        }

        query.multiselect(
                brandJoin.get("id").alias("id"),
                brandJoin.get("name").alias("name"),
                cb.count(root.get("id")).alias("totalProduct")
        );

        query.where(predicate);
        query.groupBy(
                brandJoin.get("id"),
                brandJoin.get("name")
        );
        query.orderBy(cb.desc(cb.count(root.get("id"))));

        return entityManager.createQuery(query)
                .getResultList()
                .stream()
                .map(tuple -> new BrandDistribution(
                        tuple.get("id", Long.class),
                        tuple.get("name", String.class),
                        tuple.get("totalProduct", Long.class)
                ))
                .toList();
    }

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

    @Override
    public PriceRange getPriceRange(Specification<Product> spec) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Product> root = query.from(Product.class);

        Join<Product, ProductVariant> defaultVariantJoin = root.join("defaultVariant", JoinType.LEFT);

        Predicate predicate = cb.conjunction();
        if (spec != null) {
            Predicate specPredicate = spec.toPredicate(root, query, cb);
            if (specPredicate != null) {
                predicate = specPredicate;
            }
        }

        Path<BigDecimal> pricePath = defaultVariantJoin.get("basePrice");

        query.multiselect(
                cb.min(pricePath).alias("minPrice"),
                cb.max(pricePath).alias("maxPrice"),
                cb.avg(pricePath).alias("avgPrice")
        );

        query.where(predicate);

        Tuple tuple = entityManager.createQuery(query).getSingleResult();

        Double avgValue = tuple.get("avgPrice", Double.class);

        return new PriceRange(
                tuple.get("minPrice", BigDecimal.class),
                tuple.get("maxPrice", BigDecimal.class),
                avgValue != null ? BigDecimal.valueOf(avgValue) : null
        );
    }

    @Override
    public List<AttributeSummary> getAttributeSummary(Specification<Product> spec) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Product> root = query.from(Product.class);

        Join<Product, ProductVariant> variantJoin = root.join("variants");
        Join<ProductVariant, VariantAttribute> variantAttributeJoin = variantJoin.join("variantAttributes");
        Join<VariantAttribute, Attribute> attributeJoin = variantAttributeJoin.join("attribute");
        Join<VariantAttribute, AttributeOption> optionJoin = variantAttributeJoin.join("option", JoinType.LEFT);

        Predicate predicate = cb.conjunction();
        if (spec != null) {
            Predicate specPredicate = spec.toPredicate(root, query, cb);
            if (specPredicate != null) {
                predicate = specPredicate;
            }
        }

        Expression<Long> totalProducts = cb.countDistinct(root.get("id"));

        query.multiselect(
                attributeJoin.get("id").alias("attributeId"),
                attributeJoin.get("name").alias("attributeName"),
                attributeJoin.get("slug").alias("attributeSlug"),
                optionJoin.get("id").alias("optionId"),
                optionJoin.get("value").alias("optionValue"),
                totalProducts.alias("totalProducts")
        );

        query.where(predicate);

        query.groupBy(
                attributeJoin.get("id"),
                attributeJoin.get("name"),
                attributeJoin.get("slug"),
                optionJoin.get("id"),
                optionJoin.get("value")
        );

        query.orderBy(
                cb.asc(attributeJoin.get("name"))
        );

        List<Tuple> rows = entityManager.createQuery(query).getResultList();

        Map<Long, AttributeSummary> grouped = new LinkedHashMap<>();

        for (Tuple tuple : rows) {
            Long attributeId = tuple.get("attributeId", Long.class);

            AttributeSummary attributeSummary = grouped.computeIfAbsent(attributeId, id ->
                    new AttributeSummary(
                            id,
                            tuple.get("attributeName", String.class),
                            tuple.get("attributeSlug", String.class),
                            new ArrayList<>()
                    )
            );

            Long optionId = tuple.get("optionId", Long.class);
            if (optionId != null) {
                attributeSummary.getOptions().add(
                        new AttributeSummary.AttributeOptionSummary(
                                optionId,
                                tuple.get("optionValue", String.class),
                                tuple.get("totalProducts", Long.class)
                        )
                );
            }
        }

        return new ArrayList<>(grouped.values());
    }
}