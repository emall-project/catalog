package ps.emall.catalog.product.product_variant;

import org.springframework.http.HttpStatus;
import ps.emall.catalog.common.exception.EMallsException;
import ps.emall.catalog.common.message.MessageKey;

public class ProductVariantExceptions {
    private ProductVariantExceptions() {}

    public static EMallsException variantNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.PRODUCT_VARIANT_NOT_FOUND.getKey())
                .build();
    }
}
