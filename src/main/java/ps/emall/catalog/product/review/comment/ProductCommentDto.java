package ps.emall.catalog.product.review.comment;

import jakarta.validation.constraints.*;
import lombok.*;
import ps.emall.catalog.common.validation.OnCreate;
import ps.emall.catalog.common.validation.OnUpdate;
import ps.emall.catalog.product.ProductDto;
import ps.emall.catalog.product.review.moderation.CommentModerationLogDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCommentDto {

    private Long commentId;
    private Long productId;

    @NotNull(groups = OnCreate.class, message = "comment.userId.notnull")
    @Positive(message = "comment.userId.positive")
    private Long userId;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "comment.content.notblank")
    @Size(min = 2, max = 2000, message = "comment.content.size")
    private String content;

    private CommentStatus status;

    private String rejectionReason;

    private LocalDateTime createdAt;

    private ProductDto product;

    private String productName;

    private String productUrl;

    private List<CommentModerationLogDto> moderationLogs;

    private Integer moderationRetryCount;

}