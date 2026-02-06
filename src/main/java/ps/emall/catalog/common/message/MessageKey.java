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
    CATEGORY_HAS_PRODUCTS("category.has.product"),
    CATEGORY_IMAGE_NOT_FOUND("category.image.not.found"),

    // ==================== CategoryDto Validation ====================
    CATEGORY_ID_NULL("category.id.null"),
    CATEGORY_ID_NOT_NULL("category.id.notnull"),
    CATEGORY_NAME_NOT_BLANK("category.name.notblank"),
    CATEGORY_SLUG_NOT_BLANK("category.slug.notblank"),
    CATEGORY_IMAGE_FILE_KEY_NOT_BLANK("category.imageFileKey.notnull"),
    CATEGORY_PARENT_ID_NOT_NULL("category.parentId.notnull"),
    CATEGORY_IS_ACTIVE_NOT_BLANK("category.isActive.notnull"),
    CATEGORY_TARGETED_AUDIENCE_NOT_NULL("category.targetedAudience.notnull"),
    CATEGORY_AGE_GROUP_NOT_NULL("category.ageGroup.notnull"),
    CATEGORY_SLUG_SHOULD_NOT_HAVE_WHITE_SPACES("category.slug.white.spaces"),
    CATEGORY_NAME_SIZE("category.name.size"),// min= 5, max= 50
    CATEGORY_SLUG_SIZE("category.slug.size"),// min= 5, max= 50

    // ==================== Category Errors ====================
    BRAND_NOT_FOUND("brand.not.found"),
    BRAND_SLUG_EXISTS("brand.slug.exists"),
    BRAND_IMAGE_NOT_FOUND("brand.image.notfound"),
    BRAND_INACTIVE("brand.inactive"),
    BRAND_HAS_PRODUCTS("brand.has.products"),

    // ==================== BrandDto Validation ====================
    BRAND_ID_NULL("brand.id.null"),
    BRAND_ID_NOT_NULL("brand.id.notnull"),
    BRAND_NAME_NOT_BLANK("brand.name.notblank"),
    BRAND_SLUG_NOT_BLANK("brand.slug.notblank"),
    BRAND_IS_ACTIVE_NOT_BLANK("brand.isActive.notnull"),
    BRAND_IMAGE_FILE_KEY_NOT_BLANK("brand.imageFileKey.notnull"),
    BRAND_TARGETED_AUDIENCE_NOT_NULL("brand.targetedAudience.notnull"),
    BRAND_AGE_GROUP_NOT_NULL("brand.ageGroup.notnull"),
    BRAND_SLUG_SHOULD_NOT_HAVE_WHITE_SPACES("brand.slug.white.spaces"),
    BRAND_NAME_SIZE("brand.name.size"),// min= 5, max= 50
    BRAND_SLUG_SIZE("brand.slug.size"),// min= 5, max= 50

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
