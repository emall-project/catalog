package ps.emall.catalog.attribute.dashboard;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ps.emall.catalog.attribute.AttributeRepository;
import ps.emall.catalog.attribute.AttributeType;
import ps.emall.catalog.attribute.attribute_options.AttributeOptionRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttributeDashboardServiceImplTest {

    @Mock
    private AttributeRepository attributeRepository;

    @Mock
    private AttributeOptionRepository attributeOptionRepository;

    @Mock
    private ProductAttributeDistributionRepository productAttributeDistributionRepository;

    @InjectMocks
    private AttributeDashboardServiceImpl attributeDashboardService;

    @Test
    void getSummaryBuildsAttributeAnalyticsSummary() {
        List<AttributeTypeDistributionDto> typeDistribution = List.of(
                new AttributeTypeDistributionDto(AttributeType.SELECT, 5L)
        );
        List<ProductAttributeDistributionDto> productDistribution = List.of(
                new ProductAttributeDistributionDto(
                        1L,
                        "Color",
                        "color",
                        12L,
                        List.of(new ProductAttributeOptionDistributionDto(10L, "Black", 8L))
                ),
                new ProductAttributeDistributionDto(
                        2L,
                        "Unused attribute",
                        "unused-attribute",
                        0L,
                        List.of()
                )
        );

        when(attributeRepository.count()).thenReturn(5L);
        when(attributeRepository.countByIsActive(true)).thenReturn(3L);
        when(attributeRepository.countByIsActive(false)).thenReturn(2L);
        when(attributeOptionRepository.count()).thenReturn(10L);
        when(productAttributeDistributionRepository.getAttributeTypeDistribution())
                .thenReturn(typeDistribution);
        when(productAttributeDistributionRepository.getProductDistributionByAttribute())
                .thenReturn(productDistribution);

        AttributeDashboardSummaryDto summary = attributeDashboardService.getSummary();

        assertThat(summary.getTotalAttributes()).isEqualTo(5L);
        assertThat(summary.getActiveAttributes()).isEqualTo(3L);
        assertThat(summary.getInactiveAttributes()).isEqualTo(2L);
        assertThat(summary.getTotalOptions()).isEqualTo(10L);
        assertThat(summary.getAverageOptionsPerAttribute()).isEqualTo(2.0);
        assertThat(summary.getAttributeTypeDistribution()).isEqualTo(typeDistribution);
        assertThat(summary.getProductDistributionByAttribute()).isEqualTo(productDistribution);

        verify(productAttributeDistributionRepository).getAttributeTypeDistribution();
        verify(productAttributeDistributionRepository).getProductDistributionByAttribute();
    }
}
