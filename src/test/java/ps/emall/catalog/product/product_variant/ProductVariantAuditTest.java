package ps.emall.catalog.product.product_variant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ps.emall.catalog.attribute.Attribute;
import ps.emall.catalog.attribute.AttributeRepository;
import ps.emall.catalog.attribute.attribute_options.AttributeOption;
import ps.emall.catalog.attribute.attribute_options.AttributeOptionRepository;
import ps.emall.catalog.brand.Brand;
import ps.emall.catalog.brand.BrandRepository;
import ps.emall.catalog.category.Category;
import ps.emall.catalog.category.CategoryRepository;
import ps.emall.catalog.common.audience.AgeGroup;
import ps.emall.catalog.common.audience.TargetedAudience;
import ps.emall.catalog.product.Product;
import ps.emall.catalog.product.ProductDto;
import ps.emall.catalog.product.ProductRepository;
import ps.emall.catalog.product.ProductService;
import ps.emall.catalog.product.product_variant.variant_attribute.VariantAttributeDto;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@SpringBootTest
public class ProductVariantAuditTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private AttributeRepository attributeRepository;

    @Autowired
    private AttributeOptionRepository attributeOptionRepository;

    @Test
    public void testUpdateProductVariantAudit() {
        // 1. Setup Data
        Category category = categoryRepository.save(Category.builder().name("Test Cat").slug("test-cat").build());
        Brand brand = brandRepository.save(Brand.builder().name("Test Brand").slug("test-brand").build());

        Attribute attribute = attributeRepository.save(Attribute.builder().name("Color").slug("color")
                .type(ps.emall.catalog.attribute.AttributeType.COLOR).build());
        AttributeOption option1 = attributeOptionRepository
                .save(AttributeOption.builder().attribute(attribute).value("Red").build());
        AttributeOption option2 = attributeOptionRepository
                .save(AttributeOption.builder().attribute(attribute).value("Blue").build());

        // 2. Create Product with Variant
        ProductDto productDto = ProductDto.builder()
                .name("Test Product")
                .slug("test-product")
                .targetedAudience(TargetedAudience.MEN)
                .ageGroup(AgeGroup.ADULT)
                .isActive(true)
                .shortDescription("Short")
                .description("Desc")
                .categoryId(category.getId())
                .brandId(brand.getId())
                .mallId(1L)
                .storeId(1L)
                .variants(Collections.singletonList(
                        ProductVariantDto.builder()
                                .name("Variant 1")
                                .basePrice(BigDecimal.TEN)
                                .isDefault(true)
                                .attributes(Collections.singletonList(
                                        VariantAttributeDto.builder()
                                                .attributeId(attribute.getId())
                                                .optionId(option1.getId())
                                                .build()))
                                .build()))
                .build();

        ProductDto created = productService.create(productDto);
        Long productId = created.getId();
        Long variantId = created.getVariants().get(0).getId();

        // 3. Update Product Variant (Change attribute option)
        // This simulates the scenario where attributes are cleared and re-added
        created.getVariants().get(0).setAttributes(Collections.singletonList(
                VariantAttributeDto.builder()
                        .attributeId(attribute.getId())
                        .optionId(option2.getId()) // Changed to option 2
                        .build()));

        // This triggers the update logic in
        // ProductServiceImpl/ProductVariantServiceImpl
        productService.update(created);
    }
}
