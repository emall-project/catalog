package ps.emall.catalog.tag;

import org.springframework.http.HttpStatus;
import ps.emall.catalog.common.exception.EMallsException;
import ps.emall.catalog.common.message.MessageKey;

public class TagExceptions {

    public static EMallsException tagNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.TAG_NOT_FOUND.getKey())
                .build();
    }

    public static EMallsException nameExists() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.TAG_NAME_EXIST.getKey())
                .build();
    }
}
