package ps.emall.catalog.messaging;

import ps.emall.catalog.event.CatalogEvent;

public interface CatalogEventPublisher {
    void publishProductViewed(CatalogEvent event);
}