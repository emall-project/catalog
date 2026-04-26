package ps.emall.catalog.publisher;

import ps.emall.catalog.event.CatalogEvent;

public interface EventPublisher {
    void publishProductViewed(CatalogEvent event);
}