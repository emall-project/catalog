package ps.emall.catalog.product.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductDashboardServiceImpl implements ProductDashboardService {

    private final ProductDashboardRepository productDashboardRepository;

    @Override
    public ProductDashboardSummaryDto getSummary(Long storeId) {
        LocalDate from = LocalDate.now()
                .withDayOfMonth(1)
                .minusMonths(5);

        return new ProductDashboardSummaryDto(
                productDashboardRepository.getKpis(storeId),
                productDashboardRepository.getVariantKpis(storeId),
                productDashboardRepository.getTagCoverage(storeId),
                productDashboardRepository.getPriceStats(storeId),
                productDashboardRepository.getCategoryDistribution(storeId),
                productDashboardRepository.getBrandDistribution(storeId),
                productDashboardRepository.getAudienceDistribution(storeId),
                productDashboardRepository.getAgeDistribution(storeId),
                productDashboardRepository.getProductsCreatedByMonth(storeId, from.atStartOfDay())
        );
    }
}
