package ps.emall.catalog.common.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageKey {

    // =========================================================
    // USER
    // =========================================================
    USER_NOT_FOUND("user.not.found"),
    PHONE_EXISTS("phone.exists"),
    EMAIL_EXISTS("email.exists"),

    // =========================================================
    // CATEGORY - ERRORS
    // =========================================================
    CATEGORY_NOT_FOUND("category.not.found"),
    CATEGORY_SLUG_EXISTS("category.slug.exists"),
    CATEGORY_PARENT_NOT_FOUND("category.parent.not.found"),
    CATEGORY_SELF_PARENT("category.self.parent"),
    CATEGORY_CIRCULAR_HIERARCHY("category.circular.hierarchy"),
    CATEGORY_HAS_CHILDREN("category.has.children"),
    CATEGORY_HAS_PRODUCTS("category.has.product"),
    CATEGORY_IMAGE_NOT_FOUND("category.image.not.found"),
    CATEGORY_IMAGE_COULD_NOT_BE_VALIDATED("category.image.could.not.be.validated"),
    CATEGORY_IMAGE_INVALID("category.image.invalid"),

    // =========================================================
    // CATEGORY - DTO VALIDATION
    // =========================================================
    CATEGORY_ID_NULL("category.id.null"),
    CATEGORY_ID_NOT_NULL("category.id.notnull"),

    CATEGORY_NAME_NOT_BLANK("category.name.notblank"),
    CATEGORY_NAME_SIZE("category.name.size"), // min=5, max=50

    CATEGORY_SLUG_NOT_BLANK("category.slug.notblank"),
    CATEGORY_SLUG_SHOULD_NOT_HAVE_WHITE_SPACES("category.slug.white.spaces"),
    CATEGORY_SLUG_LOWERCASE("category.slug.lowercase"),
    CATEGORY_SLUG_START_END_LETTER("category.slug.start.end.letter"),
    CATEGORY_SLUG_SIZE("category.slug.size"), // min=5, max=50

    CATEGORY_IMAGE_ID_BLANK("category.imageId.notnull"),

    CATEGORY_PARENT_ID_NOT_NULL("category.parentId.notnull"),

    CATEGORY_IS_ACTIVE_NOT_BLANK("category.isActive.notnull"),

    CATEGORY_TARGETED_AUDIENCE_NOT_NULL("category.targetedAudience.notnull"),

    CATEGORY_AGE_GROUP_NOT_NULL("category.ageGroup.notnull"),

    // =========================================================
    // BRAND - ERRORS
    // =========================================================
    BRAND_NOT_FOUND("brand.not.found"),
    BRAND_SLUG_EXISTS("brand.slug.exists"),
    BRAND_IMAGE_NOT_FOUND("brand.image.notfound"),
    BRAND_IMAGE_COULD_NOT_BE_VALIDATED("brand.image.could.not.be.validated"),
    BRAND_IMAGE_INVALID("brand.image.invalid"),
    BRAND_INACTIVE("brand.inactive"),
    BRAND_HAS_PRODUCTS("brand.has.products"),

    // =========================================================
    // BRAND - DTO VALIDATION
    // =========================================================
    BRAND_ID_NULL("brand.id.null"),
    BRAND_ID_NOT_NULL("brand.id.notnull"),

    BRAND_NAME_NOT_BLANK("brand.name.notblank"),
    BRAND_NAME_SIZE("brand.name.size"), // min=5, max=50

    BRAND_SLUG_NOT_BLANK("brand.slug.notblank"),
    BRAND_SLUG_WHITE_SPACES("brand.slug.white.spaces"),
    BRAND_SLUG_LOWERCASE("brand.slug.lowercase"),
    BRAND_SLUG_START_END_LETTER("brand.slug.start.end.letter"),
    BRAND_SLUG_SIZE("brand.slug.size"), // min=5, max=50

    BRAND_TARGETED_AUDIENCE_NOT_NULL("brand.targetedAudience.notnull"),

    BRAND_AGE_GROUP_NOT_NULL("brand.ageGroup.notnull"),

    BRAND_IS_ACTIVE_NOT_BLANK("brand.isActive.notnull"),

    BRAND_IMAGE_ID_BLANK("brand.imageId.notnull"),

    // =========================================================
    // TAG - ERRORS
    // =========================================================
    TAG_NOT_FOUND("tag.not.found"),
    TAG_NAME_EXIST("tag.name.exist"),
    TAG_HAS_PRODUCTS("tag.has.products"),

    // =========================================================
    // TAG - DTO VALIDATION
    // =========================================================
    TAG_ID_NULL("tag.id.null"),
    TAG_ID_NOT_NULL("tag.id.notnull"),

    TAG_NAME_NOT_BLANK("tag.name.notblank"),
    TAG_NAME_SIZE("tag.name.size"), // min=5, max=50

    // =========================================================
    // ATTRIBUTE OPTION - ERRORS
    // =========================================================
    DUPLICATION_IN_ORDER_SORT("attribute.options.orderSort.duplication"),
    DUPLICATION_IN_VALUE("attribute.options.value.duplication"),
    ATTRIBUTE_OPTION_NO_FOUND("attribute.options.no.found"),

    // =========================================================
    // ATTRIBUTE OPTION - DTO VALIDATION
    // =========================================================
    ATTRIBUTE_OPTION_ID_NULL("attribute.option.id.null"),
    ATTRIBUTE_OPTION_ID_NOT_NULL("attribute.option.id.notnull"),

    ATTRIBUTE_OPTION_VALUE_NOT_BLANK("attribute.option.value.notblank"),
    ATTRIBUTE_OPTION_VALUE_SIZE("attribute.option.value.size"),

    ATTRIBUTE_OPTION_SORT_ORDER_NOT_NULL("attribute.option.sortOrder.notnull"),

    // =========================================================
    // ATTRIBUTE - ERRORS
    // =========================================================
    ATTRIBUTE_NOT_FOUND("attribute.not.found"),
    ATTRIBUTE_SLUG_EXISTS("attribute.slug.exists"),
    ATTRIBUTE_INACTIVE("attribute.inactive"),

    // =========================================================
    // ATTRIBUTE - DTO VALIDATION
    // =========================================================
    ATTRIBUTE_ID_NULL("attribute.id.null"),
    ATTRIBUTE_ID_NOT_NULL("attribute.id.notnull"),

    ATTRIBUTE_NAME_NOT_BLANK("attribute.name.notblank"),
    ATTRIBUTE_NAME_SIZE("attribute.name.size"),

    ATTRIBUTE_SLUG_NOT_BLANK("attribute.slug.notblank"),
    ATTRIBUTE_SLUG_WHITE_SPACES("attribute.slug.white.spaces"),
    ATTRIBUTE_SLUG_LOWERCASE("attribute.slug.lowercase"),
    ATTRIBUTE_SLUG_START_END_LETTER("attribute.slug.start.end.letter"),
    ATTRIBUTE_SLUG_SIZE("attribute.slug.size"),

    ATTRIBUTE_IS_ACTIVE_NOT_NULL("attribute.isActive.notnull"),

    ATTRIBUTE_OPTIONS_NOT_NULL("attribute.options.notnull"),

    // =========================================================
    // PRODUCT - ERRORS
    // =========================================================
    PRODUCT_NOT_FOUND("product.not.found"),
    PRODUCT_SLUG_EXISTS("product.slug.exists"),
    PRODUCT_SLUG_EXISTS_IN_THE_SAME_STORE("product.slug.exists.in.the.same.store"),
    PRODUCT_INACTIVE("product.inactive"),
    PRODUCT_HAS_MULTIPLE_DEFAULT_VARIANTS("product.has.multiple.default.variants"),

    // =========================================================
    // PRODUCT - DTO VALIDATION
    // =========================================================
    PRODUCT_ID_NULL("product.id.null"),
    PRODUCT_ID_NOT_NULL("product.id.notnull"),

    PRODUCT_NAME_NOT_BLANK("product.name.notblank"),
    PRODUCT_NAME_SIZE("product.name.size"),

    PRODUCT_SLUG_NOT_BLANK("product.slug.notblank"),
    PRODUCT_SLUG_WHITE_SPACES("product.slug.white.spaces"),
    PRODUCT_SLUG_LOWERCASE("product.slug.lowercase"),
    PRODUCT_SLUG_START_END_LETTER("product.slug.start.end.letter"),
    PRODUCT_SLUG_SIZE("product.slug.size"),

    PRODUCT_TARGETED_AUDIENCE_NOT_NULL("product.targetedAudience.notnull"),
    PRODUCT_AGE_GROUP_NOT_NULL("product.ageGroup.notnull"),
    PRODUCT_IS_ACTIVE_NOT_NULL("product.isActive.notnull"),

    PRODUCT_SHORT_DESCRIPTION_NOT_BLANK("product.shortDescription.notblank"),
    PRODUCT_SHORT_DESCRIPTION_SIZE("product.shortDescription.size"),

    PRODUCT_DESCRIPTION_NOT_BLANK("product.description.notblank"),
    PRODUCT_DESCRIPTION_SIZE("product.description.size"),

    PRODUCT_MALL_ID_NULL("product.mallId.null"),
    PRODUCT_STORE_ID_NULL("product.storeId.null"),

    PRODUCT_VARIANTS_NOT_NULL("product.variants.not.null"),

    // =========================================================
    // PRODUCT VARIANT - ERRORS
    // =========================================================
    PRODUCT_VARIANT_NOT_FOUND("product.variant.not.found"),
    PRODUCT_VARIANT_DUPLICATE_ATTRIBUTE("product.variant.duplicate.attribute"),
    PRODUCT_VARIANT_DUPLICATE_MEDIUM_SORT("product.variant.duplicate.image.sort"),
    PRODUCT_VARIANT_MEDIA_LIMIT_EXCEEDED("product.variant.image.limitExceeded"),
    PRODUCT_VARIANT_MUST_HAVE_AT_LEAST_ONE_MEDIUM("product.variant.must.haveAtLeastOne"),
    PRODUCT_VARIANT_MEDIUM_NOT_FOUND("product.variant.image.not.found"),
    PRODUCT_VARIANT_MEDIUM_COULD_NOT_BE_VALIDATED("product.variant.image.couldNotBeValidated"),
    PRODUCT_VARIANT_MEDIUM_TYPE_INVALID("product.variant.type.invalid"),
    PRODUCT_VARIANT_SLUG_EXISTS("product.variant.slug.exists"),
    PRODUCT_VARIANT_INACTIVE("product.variant.inactive"),

    // =========================================================
    // PRODUCT VARIANT - DTO VALIDATION
    // =========================================================
    PRODUCT_VARIANT_ID_NULL("product.variant.id.null"),
    PRODUCT_VARIANT_ID_NOT_NULL("product.variant.id.notnull"),
    PRODUCT_VARIANT_NAME_NOT_BLANK("product.variant.name.notblank"),

    PRODUCT_VARIANT_BASE_PRICE_NOT_BLANK("product.variant.basePrice.notblank"),
    PRODUCT_VARIANT_BASE_PRICE_POSITIVE("product.variant.basePrice.positive"),

    PRODUCT_VARIANT_IS_DEFAULT_NOT_NULL("product.variant.isDefault.notnull"),

    PRODUCT_VARIANT_MEDIA_NOT_NULL("product.variant.media.not.null"),

    // =========================================================
    // HTTP STATUS
    // =========================================================
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