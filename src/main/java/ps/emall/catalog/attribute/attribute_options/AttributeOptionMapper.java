package ps.emall.catalog.attribute.attribute_options;

public class AttributeOptionMapper {
    public static AttributeOptionDto toDto(AttributeOption entity) {
        return AttributeOptionDto.builder()
                .id(entity.getId())
                .value(entity.getValue())
                .sortOrder(entity.getSortOrder())
                .createdAt(entity.getCreatedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    public static AttributeOption toEntity(AttributeOptionDto dto) {
        return AttributeOption.builder()
                .id(dto.getId())
                .value(dto.getValue())
                .sortOrder(dto.getSortOrder())
                .build();
    }
}
