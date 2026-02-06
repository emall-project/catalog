package ps.emall.catalog.tag;

import jakarta.validation.constraints.*;
import lombok.*;
import ps.emall.catalog.common.validation.OnCreate;
import ps.emall.catalog.common.validation.OnUpdate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagDto {

    @Null(groups = OnCreate.class, message = "tag.id.null")
    @NotNull(groups = OnUpdate.class, message = "tag.id.notnull")
    private Long id;

    @NotBlank(message = "tag.name.notblank")
    @Size(min = 2, max = 50, message = "tag.name.size")
    private String name;
}
