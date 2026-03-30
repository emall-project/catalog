package ps.emall.catalog.category.audience_config;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;
import ps.emall.catalog.category.Category;
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
public class CategoryAudienceConfigDto {
    @Null(groups = OnCreate.class, message = "category.audience.config.id.null")
    @NotNull(groups = OnUpdate.class, message = "category.audience.config.id.notnull")
    private Long id;

    @NotNull(message = "category.audience.config.ageGroup.notnull")
    private AgeGroup ageGroup;

    @NotNull(message = "category.audience.config.targetedAudience.notnull")
    private TargetedAudience targetedAudience;

    @NotNull(message = "category.audience.config.imageId.notnull")
    private UUID imageId;

    private FileDto image;

}
