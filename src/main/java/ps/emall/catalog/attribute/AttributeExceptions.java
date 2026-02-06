package ps.emall.catalog.attribute;

import org.springframework.http.HttpStatus;
import ps.emall.catalog.common.exception.EMallsException;
import ps.emall.catalog.common.message.MessageKey;
import ps.emall.catalog.common.response.ErrorCode;

import java.util.List;

public final class AttributeExceptions {

    private AttributeExceptions() {}

    public static EMallsException attributeNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.ATTRIBUTE_NOT_FOUND.getKey())
                .build();
    }

    public static EMallsException slugExists() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.ATTRIBUTE_SLUG_EXISTS.getKey())
                .errorCode(List.of(
                        new ErrorCode("slug", MessageKey.ATTRIBUTE_SLUG_EXISTS.getKey())
                ))
                .build();
    }

    public static EMallsException attributeInactive() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.ATTRIBUTE_INACTIVE.getKey())
                .build();
    }
}
