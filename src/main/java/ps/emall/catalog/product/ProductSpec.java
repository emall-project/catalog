package ps.emall.catalog.product;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@And({
        @Spec(params = "name", path = "name", spec = LikeIgnoreCase.class),
        @Spec(params = "slug", path = "slug", spec = LikeIgnoreCase.class),
        @Spec(params = "category-id", path = "category.id", spec = Equal.class),
        @Spec(params = "brand-id", path = "brand.id", spec = Equal.class),
        @Spec(params = "is-active", path = "isActive", spec = Equal.class)
})
public interface ProductSpec extends Specification<Product> {
}
