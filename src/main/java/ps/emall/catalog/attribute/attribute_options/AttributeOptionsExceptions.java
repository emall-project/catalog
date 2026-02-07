package ps.emall.catalog.attribute.attribute_options;

import org.springframework.http.HttpStatus;
import ps.emall.catalog.common.exception.EMallsException;
import ps.emall.catalog.common.message.MessageKey;
import ps.emall.catalog.common.response.ErrorCode;

import java.util.List;

public final class AttributeOptionsExceptions {

    private AttributeOptionsExceptions() {}

    public static EMallsException duplicationInOrderSort() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.DUPLICATION_IN_ORDER_SORT.getKey())
                .build();
    }
    public static EMallsException duplicationInValue() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.DUPLICATION_IN_VALUE.getKey())
                .build();
    }


}
