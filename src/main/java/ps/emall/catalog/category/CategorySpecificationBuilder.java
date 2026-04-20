package ps.emall.catalog.category;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ps.emall.catalog.common.audience.AgeGroup;
import ps.emall.catalog.common.audience.TargetedAudience;

@Component
public final class CategorySpecificationBuilder {
    public Specification<Category> build(CategoryFilter categoryFilter) {
        if (categoryFilter == null) {
            return Specification.where((Specification<Category>) null);
        }
        return Specification.allOf(
                nameSpec(categoryFilter.getName()),
                slugSpec(categoryFilter.getSlug()),
                parentIdSpec(categoryFilter.getParentId()),
                isActiveSpec(categoryFilter.getIsActive()),
                targetedAudienceSpec(categoryFilter.getTargetedAudience()),
                ageGroupSpec(categoryFilter.getAgeGroup()),
                depthLevelSpec(categoryFilter.getDepthLevel()),
                isRootSpec(categoryFilter.getIsRoot())
        );
    }

    public static Specification<Category> nameSpec(String name) {
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

    public static Specification<Category> slugSpec(String slug) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(slug)) {
                return null;
            }

            return cb.like(cb.lower(root.get("slug")), "%" + slug.toLowerCase() + "%");
        };
    }


    public static Specification<Category> parentIdSpec(Long parentId) {
        return (root, query, cb) -> {
            if (parentId == null) {
                return null;
            }
            return cb.equal(root.get("parent").get("id"), parentId);
        };
    }

    public static Specification<Category> isActiveSpec(Boolean isActive) {
        return (root, query, cb) -> {
            if (isActive == null) {
                return null;
            }
            return cb.equal(root.get("isActive"), isActive);
        };
    }


    public static Specification<Category> targetedAudienceSpec(TargetedAudience targetedAudience) {
        return (root, query, cb) -> {
            if (targetedAudience == null) {
                return null;
            }
            return cb.equal(root.get("targetedAudience"), targetedAudience);
        };
    }

    public static Specification<Category> ageGroupSpec(AgeGroup ageGroup) {
        return (root, query, cb) -> {
            if (ageGroup == null) {
                return null;
            }
            return cb.equal(root.get("ageGroup"), ageGroup);
        };
    }


    public static Specification<Category> depthLevelSpec(Integer depthLevel) {
        return (root, query, cb) -> {
            if (depthLevel == null) {
                return null;
            }
            return cb.equal(root.get("depthLevel"), depthLevel);
        };
    }

    public static Specification<Category> isRootSpec(Boolean isRoot) {
        return ((root, query, criteriaBuilder) -> {
            if (isRoot == null) {
                return null;
            }
            if (isRoot) {
                return criteriaBuilder.isNull(root.get("parent"));
            }
            return criteriaBuilder.isNotNull(root.get("parent"));
        });
    }

}
