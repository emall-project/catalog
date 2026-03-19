package ps.emall.catalog.tag;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagDto {

    @Null(message = "tag.id.null")
    private Long id;

    @NotBlank(message = "tag.name.notblank")
    @Size(min = 2, max = 50, message = "tag.name.size")
    private String name;
}
