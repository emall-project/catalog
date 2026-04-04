package ps.emall.catalog.product.review.comment;

import org.springframework.http.HttpStatus;
import ps.emall.catalog.common.exception.EMallsException;
import ps.emall.catalog.common.message.MessageKey;
import ps.emall.catalog.common.response.ErrorCode;

import java.util.List;

public final class CommentExceptions {

    private CommentExceptions() {}

    public static EMallsException commentNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.COMMENT_NOT_FOUND.getKey())
                .errorCode(List.of(new ErrorCode("commentId", MessageKey.COMMENT_NOT_FOUND.getKey())))
                .build();
    }

    public static EMallsException commentAlreadyExists() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.COMMENT_ALREADY_EXISTS.getKey())
                .errorCode(List.of(new ErrorCode("userId", MessageKey.COMMENT_ALREADY_EXISTS.getKey())))
                .build();
    }

    public static EMallsException editNotAllowedWhileUnderInvestigation() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.COMMENT_EDIT_NOT_ALLOWED_UNDER_INVESTIGATION.getKey())
                .errorCode(List.of(new ErrorCode("commentId",
                        MessageKey.COMMENT_EDIT_NOT_ALLOWED_UNDER_INVESTIGATION.getKey())))
                .build();
    }

    public static EMallsException notYourComment() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.COMMENT_NOT_FOUND.getKey())
                .errorCode(List.of(new ErrorCode("commentId", MessageKey.COMMENT_NOT_FOUND.getKey())))
                .build();
    }

    public static EMallsException userNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.USER_NOT_FOUND_FOR_REVIEW.getKey())
                .errorCode(List.of(new ErrorCode("userId", MessageKey.USER_NOT_FOUND_FOR_REVIEW.getKey())))
                .build();
    }

    public static EMallsException cannotReportOwnComment() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.COMMENT_CANNOT_REPORT_OWN.getKey())
                .errorCode(List.of(new ErrorCode("userId", MessageKey.COMMENT_CANNOT_REPORT_OWN.getKey())))
                .build();
    }

    public static EMallsException commentNotApprovedForReport() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.COMMENT_NOT_APPROVED_FOR_REPORT.getKey())
                .errorCode(List.of(new ErrorCode("commentId",
                        MessageKey.COMMENT_NOT_APPROVED_FOR_REPORT.getKey())))
                .build();
    }
}