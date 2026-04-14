package ps.emall.catalog.product;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ps.emall.catalog.common.audience.AgeGroup;
import ps.emall.catalog.common.audience.TargetedAudience;
import ps.emall.catalog.product.product_variant.ProductVariant;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public final class ProductSpecificationBuilder {

    private ProductSpecificationBuilder() {
    }

    public Specification<Product> build(ProductFilter filter) {
        if (filter == null) {
            return Specification.where((Specification<Product>) null);
        }

        return Specification.allOf(
                qSpec(filter.getQ()),
                slugSpec(filter.getSlug()),
                categorySpec(filter.getCategoryId()),
                brandSpec(filter.getBrandId()),
                mallSpec(filter.getMallId()),
                storeSpec(filter.getStoreId()),
                isActiveSpec(filter.getIsActive()),
                targetedAudienceSpec(filter.getTargetedAudience()),
                ageGroupSpec(filter.getAgeGroup()),
                selectedOptionsByAttributeSpec(filter.getSelectedOptionsByAttribute()),
                priceRangeSpec(filter.getMinPrice(), filter.getMaxPrice())
        );
    }

    public static Specification<Product> qSpec(String q) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(q)) {
                return null;
            }

            String pattern = "%" + q.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("shortDescription")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    public static Specification<Product> slugSpec(String slug) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(slug)) {
                return null;
            }

            return cb.like(cb.lower(root.get("slug")), "%" + slug.toLowerCase() + "%");
        };
    }


    public static Specification<Product> categorySpec(Long categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) {
                return null;
            }
            return cb.equal(root.get("category").get("id"), categoryId);
        };
    }


    public static Specification<Product> brandSpec(Long brandId) {
        return (root, query, cb) -> {
            if (brandId == null) {
                return null;
            }
            return cb.equal(root.get("brand").get("id"), brandId);
        };
    }

    public static Specification<Product> mallSpec(Long mallId) {
        return (root, query, cb) -> {
            if (mallId == null) {
                return null;
            }
            return cb.equal(root.get("mallId"), mallId);
        };
    }

    public static Specification<Product> storeSpec(Long storeId) {
        return (root, query, cb) -> {
            if (storeId == null) {
                return null;
            }
            return cb.equal(root.get("storeId"), storeId);
        };
    }

    public static Specification<Product> isActiveSpec(Boolean isActive) {
        return (root, query, cb) -> {
            if (isActive == null) {
                return null;
            }
            return cb.equal(root.get("isActive"), isActive);
        };
    }

    public static Specification<Product> targetedAudienceSpec(TargetedAudience targetedAudience) {
        return (root, query, cb) -> {
            if (targetedAudience == null) {
                return null;
            }
            return cb.equal(root.get("targetedAudience"), targetedAudience);
        };
    }

    public static Specification<Product> ageGroupSpec(AgeGroup ageGroup) {
        return (root, query, cb) -> {
            if (ageGroup == null) {
                return null;
            }
            return cb.equal(root.get("ageGroup"), ageGroup);
        };
    }

    public static Specification<Product> selectedOptionsByAttributeSpec(
            Map<Long, List<Long>> selectedOptionsByAttribute
    ) {
        return (root, query, cb) -> {
            if (selectedOptionsByAttribute == null || selectedOptionsByAttribute.isEmpty()) {
                return null;
            }

            // remove empty groups
            Map<Long, List<Long>> validGroups = selectedOptionsByAttribute.entrySet().stream()
                    .filter(e -> e.getKey() != null && e.getValue() != null && !e.getValue().isEmpty())
                    .collect(java.util.stream.Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue
                    ));

            if (validGroups.isEmpty()) {
                return null;
            }

            query.distinct(true);

            var subquery = query.subquery(Long.class);
            var variantRoot = subquery.from(ProductVariant.class);
            var variantAttributeJoin = variantRoot.join("variantAttributes");

            List<jakarta.persistence.criteria.Predicate> orGroups = new ArrayList<>();

            for (Map.Entry<Long, List<Long>> entry : validGroups.entrySet()) {
                Long attributeId = entry.getKey();
                List<Long> optionIds = entry.getValue();

                orGroups.add(
                        cb.and(
                                cb.equal(variantAttributeJoin.get("attribute").get("id"), attributeId),
                                variantAttributeJoin.get("option").get("id").in(optionIds)
                        )
                );
            }

            subquery.select(variantRoot.get("product").get("id"))
                    .where(
                            cb.equal(variantRoot.get("product").get("id"), root.get("id")),
                            cb.or(orGroups.toArray(new jakarta.persistence.criteria.Predicate[0]))
                    )
                    .groupBy(variantRoot.get("id"), variantRoot.get("product").get("id"))
                    .having(
                            cb.equal(
                                    cb.countDistinct(variantAttributeJoin.get("attribute").get("id")),
                                    validGroups.size()
                            )
                    );

            return cb.exists(subquery);
        };
    }

    public static Specification<Product> priceRangeSpec(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (minPrice != null) {
                predicate = cb.and(
                        predicate,
                        cb.greaterThanOrEqualTo(root.get("defaultVariant").get("basePrice"), minPrice)
                );
            }

            if (maxPrice != null) {
                predicate = cb.and(
                        predicate,
                        cb.lessThanOrEqualTo(root.get("defaultVariant").get("basePrice"), maxPrice)
                );
            }

            return predicate;
        };
    }
}