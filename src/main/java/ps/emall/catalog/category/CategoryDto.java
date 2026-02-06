package ps.emall.catalog.category;

import jakarta.validation.constraints.*;
import lombok.*;
import ps.emall.catalog.common.audience.AgeGroup;
import ps.emall.catalog.common.audience.TargetedAudience;
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
    @Size(min = 3, max = 50, message = "category.name.size")
    private String name;

    @NotBlank(message = "category.slug.notblank")
    @Pattern(
        regexp = "^[^\\s]+$",
        message = "category.slug.white.spaces"
    )
    @Size(min = 3, max = 50, message = "category.slug.size")
    private String slug;

    @NotNull(message = "category.targetedAudience.notnull")
    private TargetedAudience targetedAudience;

    @NotNull(message = "category.ageGroup.notnull")
    private AgeGroup ageGroup;

    @NotNull(message = "category.isActive.notnull")
    private Boolean isActive;

    @NotNull(message = "category.imageFileKey.notnull")
    private UUID imageFileKey;

    private Long parentId;
}
