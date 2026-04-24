package ps.emall.catalog.publisher;

import ps.emall.catalog.job.ProductCreatedJob;

public interface JobPublisher {
    void publishProductCreatedJob(ProductCreatedJob job);
}
