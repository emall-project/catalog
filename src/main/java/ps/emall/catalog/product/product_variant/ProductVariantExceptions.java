package ps.emall.catalog.product.product_variant;

import org.springframework.http.HttpStatus;
import ps.emall.catalog.common.exception.EMallsException;
import ps.emall.catalog.common.message.MessageKey;
import ps.emall.catalog.common.response.ErrorCode;

import java.util.List;

public final class ProductVariantExceptions {

    private ProductVariantExceptions() {}

    // ----------------------------
    // Not Found Exceptions
    // ----------------------------
    public static EMallsException variantNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.PRODUCT_VARIANT_NOT_FOUND.getKey())
                .build();
    }

    public static EMallsException mediumNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.PRODUCT_VARIANT_MEDIUM_NOT_FOUND.getKey())
                .build();
    }


    public static EMallsException mediumTypeInvalid() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST) // Changed from NOT_FOUND to BAD_REQUEST
                .message(MessageKey.PRODUCT_VARIANT_MEDIUM_TYPE_INVALID.getKey())
                .build();
    }

    // ----------------------------
    // Bad Request Exceptions
    // ----------------------------
    public static EMallsException duplicateAttribute() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.PRODUCT_VARIANT_DUPLICATE_ATTRIBUTE.getKey())
                .build();
    }

    public static EMallsException duplicateMediumSort() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.PRODUCT_VARIANT_DUPLICATE_MEDIUM_SORT.getKey())
                .build();
    }

    public static EMallsException mediaLimitExceeded() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.PRODUCT_VARIANT_MEDIA_LIMIT_EXCEEDED.getKey())
                .build();
    }

    public static EMallsException atLeastOneMediaRequired() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.PRODUCT_VARIANT_MUST_HAVE_AT_LEAST_ONE_MEDIUM.getKey())
                .build();
    }

    public static EMallsException defaultVariantDeletionNotAllowed() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.PRODUCT_DEFAULT_VARIANT_DELETION_NOT_ALLOWED.getKey())
                .build();
    }

    // ----------------------------
    // Internal Server Error Exceptions
    // ----------------------------
    public static EMallsException mediumCouldNotBeValidated() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR) // Changed from NOT_FOUND to INTERNAL_SERVER_ERROR
                .message(MessageKey.PRODUCT_VARIANT_MEDIUM_COULD_NOT_BE_VALIDATED.getKey())
                .build();
    }
}