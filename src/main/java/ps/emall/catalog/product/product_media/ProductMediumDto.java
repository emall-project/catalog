package ps.emall.catalog.product.product_media;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import ps.emall.catalog.client.media_manager.FileDto;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductMediumDto {
    private Long id;

    @NotNull(message = "product.image.mediaId.notnull")
    private UUID mediumId;

    @NotNull(message = "product.image.sortOrder.notnull")
    private int sortOrder;

    private FileDto mediumFile;
}
