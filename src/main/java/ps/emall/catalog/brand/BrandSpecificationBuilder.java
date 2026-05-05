package ps.emall.catalog.brand;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ps.emall.catalog.category.Category;
import ps.emall.catalog.common.audience.AgeGroup;
import ps.emall.catalog.common.audience.TargetedAudience;

@Component
public final class BrandSpecificationBuilder {

    public Specification<Brand> build(BrandFilter brandFilter) {
        if (brandFilter == null) {
            return Specification.where((Specification<Brand>) null);
        }
        return Specification.allOf(
                nameSpec(brandFilter.getName()),
                slugSpec(brandFilter.getSlug()),
                isActiveSpec(brandFilter.getIsActive()),
                targetedAudienceSpec(brandFilter.getTargetedAudience()),
                ageGroupSpec(brandFilter.getAgeGroup()),
                excludedAudienceSpec(brandFilter.getExcludedAudience())
        );
    }

    public static Specification<Brand> nameSpec(String name) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(name)) {
                return null;
            }

            return cb.like(
                    cb.lower(root.get("name")),
                    "%" + name.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Brand> slugSpec(String slug) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(slug)) {
                return null;
            }

            return cb.like(cb.lower(root.get("slug")), "%" + slug.toLowerCase() + "%");
        };
    }

    public static Specification<Brand> isActiveSpec(Boolean isActive) {
        return (root, query, cb) -> {
            if (isActive == null) {
                return null;
            }
            return cb.equal(root.get("isActive"), isActive);
        };
    }


    public static Specification<Brand> targetedAudienceSpec(TargetedAudience targetedAudience) {
        return (root, query, cb) -> {
            if (targetedAudience == null) {
                return null;
            }
            return cb.equal(root.get("targetedAudience"), targetedAudience);
        };
    }

    public static Specification<Brand> ageGroupSpec(AgeGroup ageGroup) {
        return (root, query, cb) -> {
            if (ageGroup == null) {
                return null;
            }
            return cb.equal(root.get("ageGroup"), ageGroup);
        };
    }

    public static Specification<Brand> excludedAudienceSpec(TargetedAudience excludedAudience) {
        return (root, query, cb) -> {
            if (excludedAudience == null) {
                return null;
            }
            return cb.notEqual(root.get("targetedAudience"), excludedAudience);
        };
    }
}
