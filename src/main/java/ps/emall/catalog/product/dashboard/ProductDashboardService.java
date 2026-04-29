package ps.emall.catalog.product.dashboard;

import org.springframework.stereotype.Service;

@Service
public interface ProductDashboardService {
    ProductDashboardSummaryDto getSummary(Long storeId);
}
