package ps.emall.catalog.category;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;


@And({
        @Spec(params = "name", path = "name", spec = LikeIgnoreCase.class),
        @Spec(params = "slug", path = "slug", spec = LikeIgnoreCase.class),
        @Spec(params = "parent-id", path = "parent.id", spec = Equal.class),
        @Spec(params = "is-active", path = "isActive", spec = Equal.class)
})
public interface CategorySpec extends Specification<Category> {
}
