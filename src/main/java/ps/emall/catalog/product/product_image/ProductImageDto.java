package ps.emall.catalog.product.product_image;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImageDto {
    private Long id;

    @NotNull(message = "product.image.imageFileKey.notnull")
    private UUID imageFileKey;

    @NotNull(message = "product.image.sortOrder.notnull")
    private int sortOrder;
}
