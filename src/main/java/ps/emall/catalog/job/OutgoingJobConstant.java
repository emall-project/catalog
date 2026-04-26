package ps.emall.catalog.job;

public final class OutgoingJobConstant {
    private OutgoingJobConstant() {
    }

    // ========================== EXCHANGE =============================
    public static final String EXCHANGE_NAME = "e-mall.jobs";


    // ========================== ROUTING KEYS ==========================
    public static final String CATALOG_PRODUCT_CREATED_ROUTING_KEY = "catalog.product.created";
    public static final String CATALOG_PRODUCT_UPDATED_ROUTING_KEY = "catalog.product.updated";
    public static final String CATALOG_PRODUCT_DELETED_ROUTING_KEY = "catalog.product.deleted";


}
