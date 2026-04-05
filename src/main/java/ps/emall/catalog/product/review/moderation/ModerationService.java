package ps.emall.catalog.product.review.moderation;

import ps.emall.catalog.product.review.comment.ProductComment;

public interface ModerationService {

    void enqueue(ProductComment comment);

    void moderateSync(ProductComment comment);
}