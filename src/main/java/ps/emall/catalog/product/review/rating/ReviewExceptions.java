package ps.emall.catalog.product.review.rating;

import org.springframework.http.HttpStatus;
import ps.emall.catalog.common.exception.EMallsException;
import ps.emall.catalog.common.message.MessageKey;
import ps.emall.catalog.common.response.ErrorCode;

import java.util.List;

public final class ReviewExceptions {

    private ReviewExceptions() {}

    public static EMallsException reviewNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.REVIEW_NOT_FOUND.getKey())
                .errorCode(List.of(new ErrorCode("reviewId", MessageKey.REVIEW_NOT_FOUND.getKey())))
                .build();
    }

    public static EMallsException reviewAlreadyExists() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.REVIEW_ALREADY_EXISTS.getKey())
                .errorCode(List.of(new ErrorCode("userId", MessageKey.REVIEW_ALREADY_EXISTS.getKey())))
                .build();
    }

    public static EMallsException cannotUpdateOtherUserReview() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.REVIEW_NOT_FOUND.getKey())
                .errorCode(List.of(new ErrorCode("reviewId", MessageKey.REVIEW_NOT_FOUND.getKey())))
                .build();
    }

    public static EMallsException userNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.USER_NOT_FOUND_FOR_REVIEW.getKey())
                .errorCode(List.of(new ErrorCode("userId", MessageKey.USER_NOT_FOUND_FOR_REVIEW.getKey())))
                .build();
    }
}