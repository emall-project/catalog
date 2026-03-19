package ps.emall.catalog.attribute;
import ps.emall.catalog.attribute.attribute_options.AttributeOptionMapper;


public class AttributeMapper {

    public static AttributeDto toDto(Attribute entity) {
        if (entity == null) return null;
        return AttributeDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .attributeType(entity.getAttributeType())
                .options(entity.getOptions() != null
                        ? entity.getOptions().stream().map(AttributeOptionMapper::toDto).toList()
                        : null)
                .isActive(entity.getIsActive())
                .build();
    }

    public static Attribute toEntity(AttributeDto dto) {
        if (dto == null) return null;

        Attribute attribute = Attribute.builder()
                .id(dto.getId())
                .name(dto.getName())
                .slug(dto.getSlug())
                .attributeType(dto.getAttributeType())
                .isActive(dto.getIsActive())
                .build();

        if (dto.getOptions() != null) {
            dto.getOptions().forEach(optionDto ->
                    attribute.addOption(AttributeOptionMapper.toEntity(optionDto))
            );
        }

        return attribute;
    }
}