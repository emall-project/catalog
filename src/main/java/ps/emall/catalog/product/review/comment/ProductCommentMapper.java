package ps.emall.catalog.product.review.comment;

import ps.emall.catalog.product.Product;
import ps.emall.catalog.product.ProductMapper;
import ps.emall.catalog.product.review.moderation.CommentModerationLogDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductCommentMapper {

    private ProductCommentMapper() {}

    public static ProductCommentDto toDto(ProductComment entity) {
        return Optional.ofNullable(entity)
                .map(e -> ProductCommentDto.builder()
                        .commentId(e.getCommentId())
                        .productId(e.getProduct() != null ? e.getProduct().getId() : null)
                        .productName(e.getProduct() != null ? e.getProduct().getName() : null)
                        .product(e.getProduct() != null ? ProductMapper.toDto(e.getProduct()) : null)
                        .userId(e.getUserId())
                        .content(e.getContent())
                        .status(e.getStatus())
                        .rejectionReason(
                                e.getStatus() == CommentStatus.REJECTED
                                        ? e.getRejectionReason()
                                        : null)
                        .productUrl(e.getProductUrl())
                        .createdAt(e.getCreatedAt())
                        .build())
                .orElse(null);
    }

    public static ProductCommentDto toDtoWithProductInfo(ProductComment entity) {
        ProductCommentDto dto = toDto(entity);
        if (dto == null) return null;

        Product product = entity.getProduct();
        if (product != null) {
            dto.setProductName(entity.getProduct().getName());
            if (entity.getStatus() == CommentStatus.APPROVED
                    && entity.getProductUrl() != null) {
                dto.setProductUrl(entity.getProductUrl() + "#comment-" + entity.getCommentId());
            }
        }
        return dto;
    }

    public static ProductCommentDto toDtoWithLogs(ProductComment entity) {
        ProductCommentDto dto = toDto(entity);
        if (dto == null) return null;

        dto.setModerationRetryCount(entity.getModerationRetryCount());

        if (entity.getModerationLogs() != null) {
            List<CommentModerationLogDto> logs = entity.getModerationLogs().stream()
                    .map(log -> CommentModerationLogDto.builder()
                            .logId(log.getLogId())
                            .commentId(entity.getCommentId())
                            .provider(log.getProvider())
                            .decision(log.getDecision())
                            .reason(log.getReason())
                            .confidence(log.getConfidence())
                            .createdAt(log.getCreatedAt())
                            .build())
                    .collect(Collectors.toList());
            dto.setModerationLogs(logs);
        }
        return dto;
    }

    public static ProductComment toEntity(ProductCommentDto dto, Product product) {
        return Optional.ofNullable(dto)
                .map(d -> ProductComment.builder()
                        .product(product)
                        .userId(d.getUserId())
                        .content(d.getContent())
                        .productUrl(d.getProductUrl())
                        .status(CommentStatus.PENDING_MODERATION)
                        .moderationRetryCount(0)
                        .build())
                .orElse(null);
    }
}