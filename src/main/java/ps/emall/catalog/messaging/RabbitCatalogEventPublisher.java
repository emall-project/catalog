package ps.emall.catalog.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import ps.emall.catalog.event.OutgoingEventRoutingKey;
import ps.emall.catalog.event.CatalogEvent;

@Component
@RequiredArgsConstructor
public class RabbitCatalogEventPublisher implements CatalogEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishProductViewed(CatalogEvent event) {
        rabbitTemplate.convertAndSend(
                OutgoingEventRoutingKey.EXCHANGE_NAME,
                OutgoingEventRoutingKey.ROUTING_KEY_CATALOG_PRODUCT_VIEWED,
                event
        );
    }
}