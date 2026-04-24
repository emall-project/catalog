package ps.emall.catalog.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import ps.emall.catalog.event.OutgoingEventConstant;
import ps.emall.catalog.event.CatalogEvent;

@Component
@RequiredArgsConstructor
public class RabbitEventPublisher implements EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishProductViewed(CatalogEvent event) {
        rabbitTemplate.convertAndSend(
                OutgoingEventConstant.EXCHANGE_NAME,
                OutgoingEventConstant.CATALOG_PRODUCT_VIEWED_ROUTING_KEY,
                event
        );
    }
}