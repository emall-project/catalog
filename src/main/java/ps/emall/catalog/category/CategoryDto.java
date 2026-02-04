package ps.emall.catalog.category;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;
import ps.emall.catalog.common.validation.OnCreate;
import ps.emall.catalog.common.validation.OnUpdate;

import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto {
    @Null(groups = OnCreate.class, message = "category.id.null")
    @NotNull(groups = OnUpdate.class, message = "category.id.notnull")
    private Long id;

    @NotBlank(message = "category.name.notblank")
    private String name;

    @NotBlank(message = "category.slug.notblank")
    private String slug;

    private boolean isActive;

    @NotNull(message = "category.imageFileKey.notnull")
    private UUID imageFileKey;

    private Long parentId;
}
