package ps.emall.catalog.product.review.comment;

import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ps.emall.catalog.common.response.EMallsResponseEntity;
import ps.emall.catalog.common.validation.OnCreate;
import ps.emall.catalog.common.validation.OnUpdate;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProductCommentController {

    private final ProductCommentService commentService;

    // USER — PUBLIC PRODUCT PAGE

    @GetMapping("/products/{productId}/comments")
    public EMallsResponseEntity<List<ProductCommentDto>> getApproved(
            @PathVariable Long productId) {
        return EMallsResponseEntity.ok(commentService.getApprovedByProductId(productId));
    }

    // USER — "MY COMMENTS" PAGE

    // TODO: remove userId param, read from security context after Spring Security is added.
    @GetMapping("/users/me/comments")
    public EMallsResponseEntity<List<ProductCommentDto>> getMyComments(
            @RequestParam Long userId) {
        return EMallsResponseEntity.ok(commentService.getMyComments(userId));
    }

    @GetMapping("/products/{productId}/comments/user/{userId}")
    public EMallsResponseEntity<ProductCommentDto> getByUser(
            @PathVariable Long productId,
            @PathVariable Long userId) {
        return EMallsResponseEntity.ok(commentService.getByProductIdAndUserId(productId, userId));
    }

    // USER — WRITE

    @PostMapping("/products/{productId}/comments")
    public EMallsResponseEntity<ProductCommentDto> create(
            @PathVariable Long productId,
            @RequestBody @Validated({Default.class, OnCreate.class}) ProductCommentDto dto) {
        return EMallsResponseEntity.created(commentService.create(productId, dto));
    }

    @PutMapping("/products/{productId}/comments/user/{userId}")
    public EMallsResponseEntity<ProductCommentDto> update(
            @PathVariable Long productId,
            @PathVariable Long userId,
            @RequestBody @Validated({Default.class, OnUpdate.class}) ProductCommentDto dto) {
        return EMallsResponseEntity.ok(commentService.update(productId, userId, dto));
    }

    @DeleteMapping("/products/{productId}/comments/user/{userId}")
    public EMallsResponseEntity<Void> delete(
            @PathVariable Long productId,
            @PathVariable Long userId) {
        commentService.delete(productId, userId);
        return EMallsResponseEntity.noContent(null);
    }

    // TODO: remove reporterUserId param, read from security context after Spring Security.
    @PostMapping("/products/{productId}/comments/{commentId}/report")
    public EMallsResponseEntity<ProductCommentDto> report(
            @PathVariable Long productId,
            @PathVariable Long commentId,
            @RequestParam Long reporterUserId) {
        return EMallsResponseEntity.ok(
                commentService.report(productId, commentId, reporterUserId));
    }

    // ADMIN
    // TODO: @PreAuthorize("hasRole('ADMIN')") after Spring Security

    @GetMapping("/admin/comments/stats")
    public EMallsResponseEntity<Map<CommentStatus, Long>> getModerationStats() {
        return EMallsResponseEntity.ok(commentService.getModerationStats());
    }

    @GetMapping("/admin/comments")
    public EMallsResponseEntity<List<ProductCommentDto>> getByStatus(
            @RequestParam CommentStatus status) {
        return EMallsResponseEntity.ok(commentService.getByStatus(status));
    }

    @GetMapping("/admin/products/{productId}/comments")
    public EMallsResponseEntity<List<ProductCommentDto>> getAllByProduct(
            @PathVariable Long productId) {
        return EMallsResponseEntity.ok(commentService.getAllByProductId(productId));
    }

    @PatchMapping("/admin/comments/{commentId}/approve")
    public EMallsResponseEntity<ProductCommentDto> approve(@PathVariable Long commentId) {
        return EMallsResponseEntity.ok(commentService.adminApprove(commentId));
    }

    @PatchMapping("/admin/comments/{commentId}/reject")
    public EMallsResponseEntity<ProductCommentDto> reject(@PathVariable Long commentId) {
        return EMallsResponseEntity.ok(commentService.adminReject(commentId));
    }

    @PatchMapping("/admin/comments/{commentId}/flag")
    public EMallsResponseEntity<ProductCommentDto> flag(@PathVariable Long commentId) {
        return EMallsResponseEntity.ok(commentService.adminFlag(commentId));
    }

    @PatchMapping("/admin/comments/{commentId}/remoderate")
    public EMallsResponseEntity<ProductCommentDto> remoderate(@PathVariable Long commentId) {
        return EMallsResponseEntity.ok(commentService.adminRemoderate(commentId));
    }
}