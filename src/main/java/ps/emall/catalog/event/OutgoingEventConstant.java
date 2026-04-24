package ps.emall.catalog.event;

public final class OutgoingEventConstant {

    private OutgoingEventConstant() {
    }


    // ========================== EXCHANGE =============================
    public static final String EXCHANGE_NAME = "e-mall.events";


    // ========================== ROUTING KEYS ==========================
    public static final String CATALOG_PRODUCT_VIEWED_ROUTING_KEY = "catalog.product.viewed";

}