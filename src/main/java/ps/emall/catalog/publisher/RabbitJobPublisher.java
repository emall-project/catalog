package ps.emall.catalog.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import ps.emall.catalog.job.OutgoingJobConstant;
import ps.emall.catalog.job.ProductJob;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitJobPublisher implements JobPublisher {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishProductCreatedJob(ProductJob job) {
        log.info("Publishing product created job {}", job.getId());
        rabbitTemplate.convertAndSend(
                OutgoingJobConstant.EXCHANGE_NAME,
                OutgoingJobConstant.CATALOG_PRODUCT_CREATED_ROUTING_KEY,
                job
        );
    }

    @Override
    public void publishProductUpdatedJob(ProductJob job) {
        log.info("Publishing product updated job {}", job.getId());
        rabbitTemplate.convertAndSend(
                OutgoingJobConstant.EXCHANGE_NAME,
                OutgoingJobConstant.CATALOG_PRODUCT_UPDATED_ROUTING_KEY,
                job
        );
    }

    @Override
    public void publishProductDeletedJob(Long productId) {
        log.info("Publishing product deleted job {}", productId);
        rabbitTemplate.convertAndSend(
                OutgoingJobConstant.EXCHANGE_NAME,
                OutgoingJobConstant.CATALOG_PRODUCT_DELETED_ROUTING_KEY,
                productId
        );
    }
}
