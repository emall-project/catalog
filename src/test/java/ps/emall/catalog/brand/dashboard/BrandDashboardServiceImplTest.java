package ps.emall.catalog.brand.dashboard;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ps.emall.catalog.brand.BrandRepository;
import ps.emall.catalog.common.audience.TargetedAudience;
import ps.emall.catalog.product.ProductRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrandDashboardServiceImplTest {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductBrandDistributionRepository productBrandDistributionRepository;

    @InjectMocks
    private BrandDashboardServiceImpl brandDashboardService;

    @Test
    void getSummaryBuildsBrandAnalyticsSummary() {
        List<ProductBrandDistributionDto> productDistribution = List.of(
                new ProductBrandDistributionDto(1L, "Apple", 12L),
                new ProductBrandDistributionDto(2L, "Empty brand", 0L)
        );

        when(brandRepository.count()).thenReturn(5L);
        when(brandRepository.countByIsActive(true)).thenReturn(3L);
        when(brandRepository.countByIsActive(false)).thenReturn(2L);
        when(brandRepository.countByTargetedAudience(TargetedAudience.MALE)).thenReturn(2L);
        when(brandRepository.countByTargetedAudience(TargetedAudience.FEMALE)).thenReturn(1L);
        when(brandRepository.countByTargetedAudience(TargetedAudience.ALL)).thenReturn(2L);
        when(productRepository.countByTargetedAudience(TargetedAudience.MALE)).thenReturn(10L);
        when(productRepository.countByTargetedAudience(TargetedAudience.FEMALE)).thenReturn(7L);
        when(productRepository.countByTargetedAudience(TargetedAudience.ALL)).thenReturn(4L);
        when(productBrandDistributionRepository.getProductDistributionByBrand())
                .thenReturn(productDistribution);

        BrandDashboardSummaryDto summary = brandDashboardService.getSummary();

        assertThat(summary.getTotalBrands()).isEqualTo(5L);
        assertThat(summary.getActiveBrands()).isEqualTo(3L);
        assertThat(summary.getInactiveBrands()).isEqualTo(2L);
        assertThat(summary.getBrandAudienceDistribution())
                .usingRecursiveComparison()
                .isEqualTo(new DashboardAudienceDistributionDto(2L, 1L, 2L));
        assertThat(summary.getProductAudienceDistribution())
                .usingRecursiveComparison()
                .isEqualTo(new DashboardAudienceDistributionDto(10L, 7L, 4L));
        assertThat(summary.getProductDistributionByBrand()).isEqualTo(productDistribution);

        verify(productBrandDistributionRepository).getProductDistributionByBrand();
    }
}
