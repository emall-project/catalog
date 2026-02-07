package ps.emall.catalog.attribute;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.*;
import ps.emall.catalog.attribute.attribute_options.AttributeOptionDto;
import ps.emall.catalog.common.validation.OnCreate;
import ps.emall.catalog.common.validation.OnUpdate;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttributeDto {
    @Null(groups = OnCreate.class, message = "attribute.id.null")
    @NotNull(groups = OnUpdate.class, message = "attribute.id.notnull")
    private Long id;

    @NotBlank(message = "attribute.name.notblank")
    @Size(min = 3, max = 50, message = "attribute.name.size")
    private String name;

    @NotBlank(message = "attribute.slug.notblank")
    @Size(min = 3, max = 50, message = "attribute.slug.size")
    private String slug;

    @NotNull(message = "attribute.attributeType.notnull")
    private AttributeType attributeType;

    @NotNull(message = "attribute.isActive.notnull")
    private Boolean isActive;

    @Valid
    @NotNull(message = "attribute.options.notnull")
    private List<AttributeOptionDto> options;

}
