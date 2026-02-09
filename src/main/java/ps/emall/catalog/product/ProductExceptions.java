package ps.emall.catalog.product;

import org.springframework.http.HttpStatus;
import ps.emall.catalog.common.exception.EMallsException;
import ps.emall.catalog.common.message.MessageKey;

public class ProductExceptions {
    private ProductExceptions() {}

    public static EMallsException brandNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.BRAND_NOT_FOUND.getKey())
                .build();
    }
    public static EMallsException productNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.PRODUCT_NOT_FOUND.getKey())
                .build();
    }
    public static EMallsException categoryNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.CATEGORY_NOT_FOUND.getKey())
                .build();
    }
}
