package ps.emall.catalog.publisher;

import ps.emall.catalog.job.ProductJob;

public interface JobPublisher {
    void publishProductCreatedJob(ProductJob job);

    void publishProductUpdatedJob(ProductJob job);

    void publishProductDeletedJob(Long productId);
}
