package ps.emall.catalog.attribute;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AttributeSpecificationBuilder {

    public Specification<Attribute> build(AttributeFilter attributeFilter) {
        if (attributeFilter == null) {
            return Specification.where((Specification<Attribute>) null);
        }
        return Specification.allOf(
                nameSpec(attributeFilter.getName()),
                slugSpec(attributeFilter.getSlug()),
                isActiveSpec(attributeFilter.getIsActive()),
                attributeTypeSpec(attributeFilter.getType())
        );
    }

    public static Specification<Attribute> nameSpec(String name) {
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

    public static Specification<Attribute> slugSpec(String slug) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(slug)) {
                return null;
            }

            return cb.like(cb.lower(root.get("slug")), "%" + slug.toLowerCase() + "%");
        };
    }


    public static Specification<Attribute> isActiveSpec(Boolean isActive) {
        return (root, query, cb) -> {
            if (isActive == null) {
                return null;
            }
            return cb.equal(root.get("isActive"), isActive);
        };
    }


    public static Specification<Attribute> attributeTypeSpec(AttributeType type) {
        return (root, query, cb) -> {
            if (type == null) {
                return null;
            }
            return cb.equal(root.get("attributeType"), type);
        };
    }


}
