package ps.emall.catalog.event;

public final class OutgoingEventRoutingKey {

    private OutgoingEventRoutingKey() {
    }

    public static final String EXCHANGE_NAME = "emall.events";

    public static final String ROUTING_KEY_CATALOG_PRODUCT_VIEWED = "catalog.product.viewed";
    public static final String ROUTING_KEY_ORDER_CREATED = "order-hub.order.created";
    public static final String ROUTING_KEY_CAMPAIGN_CLICKED = "campaigns.campaign.clicked";
}