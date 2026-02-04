package ps.emall.catalog.common.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageKey {

    // ==================== User Errors ====================
    USER_NOT_FOUND("user.not.found"),
    PHONE_EXISTS("phone.exists"),
    EMAIL_EXISTS("email.exists"),

    // ==================== Category Errors ====================
    CATEGORY_NOT_FOUND("category.not.found"),
    CATEGORY_SLUG_EXISTS("category.slug.exists"),
    CATEGORY_PARENT_NOT_FOUND("category.parent.not.found"),
    CATEGORY_SELF_PARENT("category.self.parent"),
    CATEGORY_CIRCULAR_HIERARCHY("category.circular.hierarchy"),
    CATEGORY_HAS_CHILDREN("category.has.children"),

    // ==================== CategoryDto Validation ====================
    CATEGORY_ID_NULL("category.id.null"),
    CATEGORY_ID_NOT_NULL("category.id.notnull"),
    CATEGORY_NAME_NOT_BLANK("category.name.notblank"),
    CATEGORY_SLUG_NOT_BLANK("category.slug.notblank"),
    CATEGORY_IMAGE_FILE_KEY_NOT_BLANK("category.imageFileKey.notnull"),
    CATEGORY_PARENT_ID_NOT_NULL("category.parentId.notnull"),

    // ==================== HTTP Status Messages ====================
    HTTP_OK("http.ok"),
    HTTP_CREATED("http.created"),
    HTTP_NO_CONTENT("http.no.content"),

    HTTP_BAD_REQUEST("http.bad.request"),
    HTTP_UNAUTHORIZED("http.unauthorized"),
    HTTP_FORBIDDEN("http.forbidden"),
    HTTP_NOT_FOUND("http.not.found"),
    HTTP_CONFLICT("http.conflict"),

    HTTP_INTERNAL_SERVER_ERROR("http.internal.server.error"),
    HTTP_SERVICE_UNAVAILABLE("http.service.unavailable");


    private final String key;
}
