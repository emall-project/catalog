package ps.emall.catalog.attribute.dashboard;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductAttributeDistributionRepository {

    private final EntityManager entityManager;

    public List<AttributeTypeDistributionDto> getAttributeTypeDistribution() {
        return entityManager.createQuery("""
                        SELECT new ps.emall.catalog.attribute.dashboard.AttributeTypeDistributionDto(
                            a.attributeType,
                            COUNT(a.id)
                        )
                        FROM Attribute a
                        GROUP BY a.attributeType
                        ORDER BY a.attributeType ASC
                        """, AttributeTypeDistributionDto.class)
                .getResultList();
    }

    public List<ProductAttributeDistributionDto> getProductDistributionByAttribute() {
        Map<Long, List<ProductAttributeOptionDistributionDto>> optionsByAttribute =
                getProductDistributionByOption();

        List<ProductAttributeDistributionDto> attributes = entityManager.createQuery("""
                        SELECT new ps.emall.catalog.attribute.dashboard.ProductAttributeDistributionDto(
                            a.id,
                            a.name,
                            a.slug,
                            COUNT(DISTINCT p.id)
                        )
                        FROM Attribute a
                        LEFT JOIN VariantAttribute va ON va.attribute = a
                        LEFT JOIN va.variant v
                        LEFT JOIN v.product p
                        GROUP BY a.id, a.name, a.slug
                        ORDER BY COUNT(DISTINCT p.id) DESC, a.name ASC
                        """, ProductAttributeDistributionDto.class)
                .getResultList();

        attributes.forEach(attribute ->
                attribute.setOptions(optionsByAttribute.getOrDefault(attribute.getAttributeId(), List.of()))
        );

        return attributes;
    }

    private Map<Long, List<ProductAttributeOptionDistributionDto>> getProductDistributionByOption() {
        return entityManager.createQuery("""
                        SELECT
                            a.id AS attributeId,
                            o.id AS optionId,
                            o.value AS optionValue,
                            COUNT(DISTINCT p.id) AS totalProducts
                        FROM Attribute a
                        JOIN a.options o
                        LEFT JOIN VariantAttribute va ON va.option = o
                        LEFT JOIN va.variant v
                        LEFT JOIN v.product p
                        GROUP BY a.id, o.id, o.value, o.sortOrder
                        ORDER BY a.id ASC, o.sortOrder ASC, o.value ASC
                        """, Tuple.class)
                .getResultList()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get("attributeId", Long.class),
                        LinkedHashMap::new,
                        Collectors.mapping(
                                tuple -> new ProductAttributeOptionDistributionDto(
                                        tuple.get("optionId", Long.class),
                                        tuple.get("optionValue", String.class),
                                        tuple.get("totalProducts", Long.class)
                                ),
                                Collectors.toList()
                        )
                ));
    }
}
