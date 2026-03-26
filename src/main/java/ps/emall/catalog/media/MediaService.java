package ps.emall.catalog.media;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ps.emall.catalog.brand.Brand;
import ps.emall.catalog.brand.BrandRepository;
import ps.emall.catalog.category.Category;
import ps.emall.catalog.category.CategoryRepository;
import ps.emall.catalog.common.Entity;
import ps.emall.catalog.product.product_variant.ProductVariant;
import ps.emall.catalog.product.product_variant.ProductVariantRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MediaService {
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductVariantRepository productVariantRepository;

    public MediaUsageDto getMediumUsage(UUID mediumId) {
        List<Category> categories = categoryRepository.findByImageId(mediumId);
        List<Brand> brands = brandRepository.findByImageId(mediumId);
        List<ProductVariant> variants = productVariantRepository.findByMediumId(mediumId);

        List<Reference> references = new ArrayList<>();
        boolean inUse = false;

        if (categories.size() > 0) {
            inUse = true;
            for (Category category : categories) {
                Reference reference = Reference.builder()
                        .entity(Entity.CATEGORY)
                        .entityId(category.getId())
                        .entityName(category.getName())
                        .build();
                references.add(reference);
            }
        }

        if (brands.size() > 0) {
            inUse = true;
            for (Brand brand : brands) {
                Reference reference = Reference.builder()
                        .entity(Entity.BRAND)
                        .entityId(brand.getId())
                        .entityName(brand.getName())
                        .build();
                references.add(reference);
            }
        }

        if (variants.size() > 0) {
            inUse = true;
            for (ProductVariant variant : variants) {
                Reference reference = Reference.builder()
                        .entity(Entity.PRODUCT_VARIANT)
                        .entityId(variant.getId())
                        .entityName(variant.getName())
                        .build();
                references.add(reference);
            }
        }

        return MediaUsageDto.builder()
                .inUse(inUse)
                .references(references)
                .build();
    }

}
