package ps.emall.catalog.product;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Conjunction;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Or;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;


@Conjunction(
        value = {
                @Or({
                        @Spec(params = "q", path = "name", spec = LikeIgnoreCase.class),
                        @Spec(params = "q", path = "shortDescription", spec = LikeIgnoreCase.class),
                        @Spec(params = "q", path = "description", spec = LikeIgnoreCase.class)
                })
        },
        and = {
                @Spec(params = "slug", path = "slug", spec = LikeIgnoreCase.class),
                @Spec(params = "category-id", path = "category.id", spec = Equal.class),
                @Spec(params = "brand-id", path = "brand.id", spec = Equal.class),
                @Spec(params = "is-active", path = "isActive", spec = Equal.class),
                @Spec(params = "targeted-audience", path = "targetedAudience", spec = Equal.class),
                @Spec(params = "age-group", path = "ageGroup", spec = Equal.class)
        }
)
public interface ProductSpec extends Specification<Product> {
}
