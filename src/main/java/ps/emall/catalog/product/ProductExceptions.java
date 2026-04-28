package ps.emall.catalog.product;

import org.springframework.http.HttpStatus;
import ps.emall.catalog.common.exception.EMallsException;
import ps.emall.catalog.common.message.MessageKey;
import ps.emall.catalog.common.response.ErrorCode;

import java.util.List;

public final class ProductExceptions {

    private ProductExceptions() {
    }

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

    public static EMallsException productNotActive() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
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

    public static EMallsException defaultVariantRequired() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.DEFAULT_VARIANTS_REQUIRED.getKey())
                .build();
    }

    public static EMallsException variantShouldHasAttribute() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.PRODUCT_VARIANT_SHOULD_HAS_ATTRIBUTE.getKey())
                .build();
    }

    public static EMallsException invalidProductAudienceForCategory() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.INVALID_PRODUCT_AUDIENCE_FOR_CATEGORY.getKey())
                .build();
    }

    public static EMallsException invalidProductAgeGroupForCategory() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.INVALID_PRODUCT_AGE_GROUP_FOR_CATEGORY.getKey())
                .build();
    }

    public static EMallsException interactionServiceNotAvailable() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(MessageKey.INTERACTION_SERVICE_NOT_AVAILABLE.getKey())
                .build();
    }

    public static EMallsException productDoesNotBelongToMall() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .message(MessageKey.PRODUCT_DOSE_NOT_BELONG_TO_MALL.getKey())
                .build();
    }

    public static EMallsException productDoesNotBelongToStore() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .message(MessageKey.PRODUCT_DOSE_NOT_BELONG_TO_STORE.getKey())
                .build();
    }

    public static EMallsException subscriptionWriteAccessDenied() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .message(MessageKey.SUBSCRIPTION_WRITE_ACCESS_DENIED.getKey())
                .errorCode(List.of(new ErrorCode("shopId",
                        MessageKey.SUBSCRIPTION_WRITE_ACCESS_DENIED.getKey())))
                .build();
    }

}