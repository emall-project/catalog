package ps.emall.catalog.product;

import ps.emall.catalog.brand.Brand;
import ps.emall.catalog.category.Category;
import ps.emall.catalog.job.ProductJob;
import ps.emall.catalog.product.product_variant.ProductVariant;
import ps.emall.catalog.product.product_variant.ProductVariantMapper;
import ps.emall.catalog.product.product_variant.variant_attribute.VariantAttribute;
import ps.emall.catalog.tag.Tag;
import ps.emall.catalog.tag.TagMapper;

import java.util.*;
import java.util.stream.Collectors;

public class ProductMapper {
    public static ProductDto toDto(Product entity) {
        return ProductDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .targetedAudience(entity.getTargetedAudience())
                .ageGroup(entity.getAgeGroup())
                .isActive(entity.getIsActive())
                .shortDescription(entity.getShortDescription())
                .description(entity.getDescription())
                .categoryId(entity.getCategory().getId())
                .brandId(entity.getBrand().getId())
                .mallId(entity.getMallId())
                .storeId(entity.getStoreId())

                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())

                .tags(entity.getTags() != null ?
                        entity.getTags().stream().map(TagMapper::toDto).toList()
                        : null
                )
                .variants(entity.getVariants() != null ?
                        entity.getVariants().stream().map(ProductVariantMapper::toDto).toList()
                        : null
                )
                .build();
    }

    public static Product toEntity(ProductDto dto, Category category, Brand brand, List<Tag> tags) {
        return Product.builder()
                .id(dto.getId())
                .name(dto.getName())
                .slug(dto.getSlug())
                .targetedAudience(dto.getTargetedAudience())
                .ageGroup(dto.getAgeGroup())
                .isActive(dto.getIsActive())
                .shortDescription(dto.getShortDescription())
                .description(dto.getDescription())
                .category(category)
                .brand(brand)
                .tags(tags)
                .build();
    }

    public static ProductJob toProductJob(Product entity) {
        ProductJob productJob = ProductJob.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .targetedAudience(entity.getTargetedAudience().name())
                .ageGroup(entity.getAgeGroup().name())
                .isActive(entity.getIsActive())
                .shortDescription(entity.getShortDescription())
                .description(entity.getDescription())
                .category(entity.getCategory().getName())
                .brand(entity.getBrand().getName())
                .mallId(entity.getMallId())
                .storeId(entity.getStoreId())
                .build();
        if (entity.getTags() != null) {
            productJob.setTags(entity.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
        }
        if (entity.getVariants() != null) {
            Map<String, Set<String>> attributes =  new HashMap<>();
            for (ProductVariant productVariant : entity.getVariants()) {
                if (productVariant.getVariantAttributes() != null) {
                    List<VariantAttribute> variantAttributes = productVariant.getVariantAttributes();
                    for (VariantAttribute variantAttribute : variantAttributes) {
                        // TODO: check if option is null when u add other type of attributes like text..etc
                        appendOptionSafely(attributes,
                                variantAttribute.getAttribute().getName(),
                                variantAttribute.getOption().getValue());
                    }
                }
            }
            productJob.setAttributes(attributes);
        }
        return productJob;
    }

    private static void appendOptionSafely(Map<String, Set<String>> attributes, String attributeName, String optionValue) {
        if (!attributes.containsKey(attributeName)) {
            attributes.put(attributeName, new HashSet<>());
        }
        attributes.get(attributeName).add(optionValue);
    }
}
