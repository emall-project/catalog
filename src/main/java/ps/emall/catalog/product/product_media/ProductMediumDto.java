package ps.emall.catalog.product.product_media;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ps.emall.catalog.client.media_manager.FileDto;
import ps.emall.catalog.common.base.EMallsBaseDto;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ProductMediumDto extends EMallsBaseDto {
    @Null(message = "product.image.id.null")
    private Long id;

    @NotNull(message = "product.image.mediaId.notnull")
    private UUID mediumId;

    @NotNull(message = "product.image.sortOrder.notnull")
    private int sortOrder;

    private FileDto mediumFile;
}
