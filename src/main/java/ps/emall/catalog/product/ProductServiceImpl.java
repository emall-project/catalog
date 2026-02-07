package ps.emall.catalog.product;

import lombok.RequiredArgsConstructor;
import ps.emall.catalog.brand.Brand;
import ps.emall.catalog.brand.BrandRepository;
import ps.emall.catalog.category.Category;
import ps.emall.catalog.category.CategoryRepository;
import ps.emall.catalog.tag.*;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class ProductServiceImpl {
    CategoryRepository categoryRepository;
    BrandRepository brandRepository;
    TagRepository tagRepository;
    TagService tagService;
    public Product create(ProductDto productDto) {
        Category category = categoryRepository.findById((productDto.getCategoryId())).orElseThrow();
        Brand brand = brandRepository.findById(productDto.getBrandId()).orElseThrow();
         Set<TagDto> tagsDto = tagService.resolveTags(productDto.getTags());
        Set<Tag> tags = new HashSet<>();
        for (TagDto dto : tagsDto) {
            Tag tag = TagMapper.toEntity(dto);
            tags.add(tag);
        }


        Product product = Product.builder()
                .name(productDto.getName())
                .slug(productDto.getSlug())
                .targetedAudience(productDto.getTargetedAudience())
                .ageGroup(productDto.getAgeGroup())
                .isActive(productDto.getIsActive())
                .shortDescription(productDto.getShortDescription())
                .description(productDto.getDescription())
                .category(category)
                .brand(brand)
                .mallId(productDto.getMallId())
                .storeId(productDto.getStoreId())
                .tags(tags)
                .build();
    }
}
