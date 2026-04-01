package ps.emall.catalog.product.light;
import java.math.BigDecimal;
import java.util.UUID;

public class ProductLightRowImpl implements ProductLightRow {

    private final Long productId;
    private final String productName;
    private final String productSlug;
    private final Long defaultVariantId;
    private final BigDecimal basePrice;
    private final UUID mediumId;

    public ProductLightRowImpl(
            Long productId,
            String productName,
            String productSlug,
            Long defaultVariantId,
            BigDecimal basePrice,
            UUID mediumId
    ) {
        this.productId = productId;
        this.productName = productName;
        this.productSlug = productSlug;
        this.defaultVariantId = defaultVariantId;
        this.basePrice = basePrice;
        this.mediumId = mediumId;
    }

    @Override
    public Long getProductId() {
        return productId;
    }

    @Override
    public String getProductName() {
        return productName;
    }

    @Override
    public String getProductSlug() {
        return productSlug;
    }

    @Override
    public Long getDefaultVariantId() {
        return defaultVariantId;
    }

    @Override
    public BigDecimal getBasePrice() {
        return basePrice;
    }

    @Override
    public UUID getMediumId() {
        return mediumId;
    }
}
