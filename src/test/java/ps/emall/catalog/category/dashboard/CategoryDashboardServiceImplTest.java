package ps.emall.catalog.category.dashboard;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ps.emall.catalog.category.CategoryRepository;
import ps.emall.catalog.common.audience.TargetedAudience;
import ps.emall.catalog.product.ProductRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryDashboardServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCategoryDistributionRepository productCategoryDistributionRepository;

    @InjectMocks
    private CategoryDashboardServiceImpl categoryDashboardService;

    @Test
    void getSummaryBuildsCategoryAnalyticsSummary() {
        List<ProductCategoryDistributionDto> productDistribution = List.of(
                new ProductCategoryDistributionDto(1L, "Phones", 12L),
                new ProductCategoryDistributionDto(2L, "Empty category", 0L)
        );

        when(categoryRepository.count()).thenReturn(5L);
        when(categoryRepository.countByIsActive(true)).thenReturn(3L);
        when(categoryRepository.countByIsActive(false)).thenReturn(2L);
        when(categoryRepository.countByTargetedAudience(TargetedAudience.MALE)).thenReturn(2L);
        when(categoryRepository.countByTargetedAudience(TargetedAudience.FEMALE)).thenReturn(1L);
        when(categoryRepository.countByTargetedAudience(TargetedAudience.ALL)).thenReturn(2L);
        when(productRepository.countByTargetedAudience(TargetedAudience.MALE)).thenReturn(10L);
        when(productRepository.countByTargetedAudience(TargetedAudience.FEMALE)).thenReturn(7L);
        when(productRepository.countByTargetedAudience(TargetedAudience.ALL)).thenReturn(4L);
        when(productCategoryDistributionRepository.getProductDistributionByCategory())
                .thenReturn(productDistribution);

        CategoryDashboardSummaryDto summary = categoryDashboardService.getSummary();

        assertThat(summary.getTotalCategories()).isEqualTo(5L);
        assertThat(summary.getActiveCategories()).isEqualTo(3L);
        assertThat(summary.getInactiveCategories()).isEqualTo(2L);
        assertThat(summary.getCategoryAudienceDistribution())
                .usingRecursiveComparison()
                .isEqualTo(new DashboardAudienceDistributionDto(2L, 1L, 2L));
        assertThat(summary.getProductAudienceDistribution())
                .usingRecursiveComparison()
                .isEqualTo(new DashboardAudienceDistributionDto(10L, 7L, 4L));
        assertThat(summary.getProductDistributionByCategory()).isEqualTo(productDistribution);

        verify(productCategoryDistributionRepository).getProductDistributionByCategory();
    }
}
