package ps.emall.catalog.favorite;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ps.emall.catalog.product.light.ProductLightDto;
//import ps.emall.catalog.product.ProductLightDto;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class FavoriteDto {

    private Long id;

    private Long productId;

    private String user;

    private LocalDateTime addedAt;

    private ProductLightDto product;
}