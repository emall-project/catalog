package ps.emall.catalog.brand;

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
public class BrandDto {
    @Null(groups = OnCreate.class, message = "brand.id.null")
    @NotNull(groups = OnUpdate.class, message = "brand.id.notnull")
    private Long id;

    @NotBlank(message = "brand.name.notblank")
    @Size(min = 3, max = 50, message = "brand.name.size")
    private String name;

    @NotBlank(message = "brand.slug.notblank")
    @Pattern(
            regexp = "^[^\\s]+$",
            message = "brand.slug.white.spaces"
    )
    @Size(min = 3, max = 50, message = "brand.slug.size")
    private String slug;

    @NotNull(message = "brand.targetedAudience.notnull")
    private TargetedAudience targetedAudience;

    @NotNull(message = "brand.ageGroup.notnull")
    private AgeGroup ageGroup;

    @NotNull(message = "brand.isActive.notnull")
    private Boolean isActive;

    @NotNull(message = "brand.imageFileKey.notnull")
    private UUID imageFileKey;

}
