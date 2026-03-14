package ps.emall.catalog.product;

import org.springframework.http.HttpStatus;
import ps.emall.catalog.common.exception.EMallsException;
import ps.emall.catalog.common.message.MessageKey;

public final class ProductExceptions {

    private ProductExceptions() {}

    // ----------------------------
    // Not Found Exceptions
    // ----------------------------
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

    // ----------------------------
    // Bad Request Exceptions
    // ----------------------------
    public static EMallsException slugExistsInTheSameStore() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.PRODUCT_SLUG_EXISTS_IN_THE_SAME_STORE.getKey())
                .build();
    }

    public static EMallsException multipleDefaultVariants() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.PRODUCT_HAS_MULTIPLE_DEFAULT_VARIANTS.getKey())
                .build();
    }
}