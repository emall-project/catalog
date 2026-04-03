package ps.emall.catalog.product.summary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeSummary {
    private Long id;
    private String name;
    private String slug;
    private List<AttributeOptionSummary> options;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttributeOptionSummary {
        private Long id;
        private String value;
        private Long totalProducts;
    }

}
