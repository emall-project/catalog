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
    private final String categoryName;
    private final String brandName;
    private final Boolean isActive;
    private final Long variantsCount;

    public ProductLightRowImpl(
            Long productId,
            String productName,
            String productSlug,
            Long defaultVariantId,
            BigDecimal basePrice,
            UUID mediumId,
            String categoryName,
            String brandName,
            Boolean isActive,
            Long variantsCount
    ) {
        this.productId = productId;
        this.productName = productName;
        this.productSlug = productSlug;
        this.defaultVariantId = defaultVariantId;
        this.basePrice = basePrice;
        this.mediumId = mediumId;
        this.categoryName = categoryName;
        this.brandName = brandName;
        this.isActive = isActive;
        this.variantsCount = variantsCount;
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

    @Override
    public String getCategoryName() {
        return categoryName;
    }

    @Override
    public String getBrandName() { return brandName; }

    @Override
    public Boolean getIsActive() {
        return isActive;
    }

    @Override
    public Long getVariantsCount() {
        return variantsCount;
    }
}
