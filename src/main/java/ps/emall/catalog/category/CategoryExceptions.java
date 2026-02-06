package ps.emall.catalog.category;

import org.springframework.http.HttpStatus;
import ps.emall.catalog.common.exception.EMallsException;
import ps.emall.catalog.common.response.ErrorCode;
import java.util.List;
import ps.emall.catalog.common.message.MessageKey;

public final class CategoryExceptions {

    private CategoryExceptions() {}

    public static EMallsException categoryNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.CATEGORY_NOT_FOUND.getKey())
                .build();
    }

    public static EMallsException categoryHasChildren() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.CATEGORY_HAS_CHILDREN.getKey())
                .build();
    }

    public static EMallsException categoryHasProducts() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.CATEGORY_HAS_PRODUCTS.getKey())
                .build();
    }

    public static EMallsException slugExists() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.CATEGORY_SLUG_EXISTS.getKey())
                .errorCode(List.of(
                        new ErrorCode("slug", MessageKey.CATEGORY_SLUG_EXISTS.getKey())
                ))
                .build();
    }

    public static EMallsException parentNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.CATEGORY_PARENT_NOT_FOUND.getKey())
                .errorCode(List.of(
                        new ErrorCode("parentId", MessageKey.CATEGORY_PARENT_NOT_FOUND.getKey())
                ))
                .build();
    }

    public static EMallsException selfParenting() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.CATEGORY_SELF_PARENT.getKey())
                .errorCode(List.of(
                        new ErrorCode("parentId", MessageKey.CATEGORY_SELF_PARENT.getKey())
                ))
                .build();
    }

    public static EMallsException circularHierarchy() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.CATEGORY_CIRCULAR_HIERARCHY.getKey())
                .errorCode(List.of(
                        new ErrorCode("parentId", MessageKey.CATEGORY_CIRCULAR_HIERARCHY.getKey())
                ))
                .build();
    }
}
