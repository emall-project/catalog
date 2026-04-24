package ps.emall.catalog.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import ps.emall.catalog.job.OutgoingJobConstant;
import ps.emall.catalog.job.ProductCreatedJob;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitJobPublisher implements JobPublisher {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishProductCreatedJob(ProductCreatedJob job) {
        log.info("Publishing product created job {}", job.getId());
        rabbitTemplate.convertAndSend(
                OutgoingJobConstant.EXCHANGE_NAME,
                OutgoingJobConstant.CATALOG_PRODUCT_CREATED_ROUTING_KEY,
                job
        );
    }
}
