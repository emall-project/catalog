package ps.emall.catalog.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;
import ps.emall.catalog.client.media_manager.FileDto;
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
    @Pattern(
            regexp = "^[a-z0-9-]+$",
            message = "category.slug.lowercase"
    )
    @Pattern(
            regexp = "^[a-z].*[a-z]$",
            message = "category.slug.start.end.letter"
    )
    @Size(min = 3, max = 50, message = "category.slug.size")
    private String slug;

    @NotNull(message = "category.targetedAudience.notnull")
    private TargetedAudience targetedAudience;

    @NotNull(message = "category.ageGroup.notnull")
    private AgeGroup ageGroup;

    @NotNull(message = "category.isActive.notnull")
    private Boolean isActive;

    @NotNull(message = "category.imageId.notnull")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UUID imageId;

    private Long parentId;

    private FileDto image;
}
