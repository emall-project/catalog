package ps.emall.catalog.brand;

import org.springframework.http.HttpStatus;
import ps.emall.catalog.common.exception.EMallsException;
import ps.emall.catalog.common.response.ErrorCode;
import ps.emall.catalog.common.message.MessageKey;

import java.util.List;

public final class BrandExceptions {

    private BrandExceptions() {}

    // ----------------------------
    // Not Found Exceptions
    // ----------------------------
    public static EMallsException brandNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.BRAND_NOT_FOUND.getKey())
                .build();
    }

    public static EMallsException imageNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND) // Changed from BAD_REQUEST to NOT_FOUND
                .message(MessageKey.BRAND_IMAGE_NOT_FOUND.getKey())
                .errorCode(List.of(new ErrorCode("imageId", MessageKey.BRAND_IMAGE_NOT_FOUND.getKey())))
                .build();
    }

    // ----------------------------
    // Bad Request Exceptions
    // ----------------------------
    public static EMallsException invalidFileType() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.BRAND_IMAGE_INVALID.getKey())
                .errorCode(List.of(new ErrorCode("imageId", MessageKey.BRAND_IMAGE_INVALID.getKey())))
                .build();
    }

    public static EMallsException imageCouldNotBeValidated() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR) // Changed from BAD_REQUEST to INTERNAL_SERVER_ERROR
                .message(MessageKey.BRAND_IMAGE_COULD_NOT_BE_VALIDATED.getKey())
                .errorCode(List.of(new ErrorCode("imageId", MessageKey.BRAND_IMAGE_COULD_NOT_BE_VALIDATED.getKey())))
                .build();
    }

    public static EMallsException brandHasProducts() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.BRAND_HAS_PRODUCTS.getKey())
                .build();
    }

    public static EMallsException brandInactive() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.BRAND_INACTIVE.getKey())
                .build();
    }

    // ----------------------------
    // Conflict Exceptions
    // ----------------------------
    public static EMallsException slugExists() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.BRAND_SLUG_EXISTS.getKey())
                .errorCode(List.of(new ErrorCode("slug", MessageKey.BRAND_SLUG_EXISTS.getKey())))
                .build();
    }
}