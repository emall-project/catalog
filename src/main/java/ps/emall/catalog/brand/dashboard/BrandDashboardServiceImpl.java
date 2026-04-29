package ps.emall.catalog.brand.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ps.emall.catalog.brand.BrandRepository;
import ps.emall.catalog.common.audience.TargetedAudience;
import ps.emall.catalog.product.ProductRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandDashboardServiceImpl implements BrandDashboardService {

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final ProductBrandDistributionRepository productBrandDistributionRepository;

    @Override
    public BrandDashboardSummaryDto getSummary() {
        return BrandDashboardSummaryDto.builder()
                .totalBrands(brandRepository.count())
                .activeBrands(brandRepository.countByIsActive(true))
                .inactiveBrands(brandRepository.countByIsActive(false))
                .brandAudienceDistribution(buildBrandAudienceDistribution())
                .productAudienceDistribution(buildProductAudienceDistribution())
                .productDistributionByBrand(productBrandDistributionRepository.getProductDistributionByBrand())
                .build();
    }

    private DashboardAudienceDistributionDto buildBrandAudienceDistribution() {
        return new DashboardAudienceDistributionDto(
                brandRepository.countByTargetedAudience(TargetedAudience.MALE),
                brandRepository.countByTargetedAudience(TargetedAudience.FEMALE),
                brandRepository.countByTargetedAudience(TargetedAudience.ALL)
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
