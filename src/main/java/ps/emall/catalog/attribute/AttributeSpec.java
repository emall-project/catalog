package ps.emall.catalog.attribute;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
        @Spec(params = "name", path = "name", spec = LikeIgnoreCase.class),
        @Spec(params = "slug", path = "slug", spec = LikeIgnoreCase.class),
        @Spec(params = "is-active", path = "isActive", spec = Equal.class),
        @Spec(params = "type", path = "attributeType", spec = Equal.class)
})
public interface AttributeSpec extends Specification<Attribute> {
}
