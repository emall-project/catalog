package ps.emall.catalog.product.product_variant.variant_attribute;

public class VariantAttributeMapper {
    public static VariantAttributeDto toDto(VariantAttribute entity) {
        return VariantAttributeDto.builder()
                .attributeId(entity.getAttribute().getId())
                .optionId(entity.getOption().getId())
                .build();
    }
}
