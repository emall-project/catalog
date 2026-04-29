package ps.emall.catalog.category.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ps.emall.catalog.category.CategoryRepository;
import ps.emall.catalog.common.audience.TargetedAudience;
import ps.emall.catalog.product.ProductRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryDashboardServiceImpl implements CategoryDashboardService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductCategoryDistributionRepository productCategoryDistributionRepository;

    @Override
    public CategoryDashboardSummaryDto getSummary() {
        return CategoryDashboardSummaryDto.builder()
                .totalCategories(categoryRepository.count())
                .activeCategories(categoryRepository.countByIsActive(true))
                .inactiveCategories(categoryRepository.countByIsActive(false))
                .categoryAudienceDistribution(buildCategoryAudienceDistribution())
                .productAudienceDistribution(buildProductAudienceDistribution())
                .productDistributionByCategory(productCategoryDistributionRepository.getProductDistributionByCategory())
                .build();
    }

    private DashboardAudienceDistributionDto buildCategoryAudienceDistribution() {
        return new DashboardAudienceDistributionDto(
                categoryRepository.countByTargetedAudience(TargetedAudience.MALE),
                categoryRepository.countByTargetedAudience(TargetedAudience.FEMALE),
                categoryRepository.countByTargetedAudience(TargetedAudience.ALL)
        );
    }

    private DashboardAudienceDistributionDto buildProductAudienceDistribution() {
        return new DashboardAudienceDistributionDto(
                productRepository.countByTargetedAudience(TargetedAudience.MALE),
                productRepository.countByTargetedAudience(TargetedAudience.FEMALE),
                productRepository.countByTargetedAudience(TargetedAudience.ALL)
        );
    }
}
