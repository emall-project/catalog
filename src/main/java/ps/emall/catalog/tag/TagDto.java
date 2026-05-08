package ps.emall.catalog.tag;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ps.emall.catalog.common.base.EMallsBaseDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TagDto extends EMallsBaseDto {

    @Null(message = "tag.id.null")
    private Long id;

    @NotBlank(message = "tag.name.notblank")
    @Size(min = 2, max = 50, message = "tag.name.size")
    private String name;

    private Long productsCount;
}
