package ps.emall.catalog.product.review.comment;

import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ps.emall.catalog.common.exception.EMallsException;
import ps.emall.catalog.common.message.MessageKey;
import ps.emall.catalog.common.response.EMallsResponseEntity;
import ps.emall.catalog.common.validation.OnCreate;
import ps.emall.catalog.common.validation.OnUpdate;
import ps.emall.catalog.security.SecurityContextUtil;

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

    @GetMapping("/users/me/comments")
    @PreAuthorize("@auth.isCustomer()")
    public EMallsResponseEntity<List<ProductCommentDto>> getMyComments() {
        Long userId = SecurityContextUtil.getCurrentUserId();
        return EMallsResponseEntity.ok(commentService.getMyComments(userId));
    }

    @GetMapping("/products/{productId}/comments/me")
    @PreAuthorize("@auth.isCustomer()")
    public EMallsResponseEntity<ProductCommentDto> getMyCommentForProduct(
            @PathVariable Long productId) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        try {
            return EMallsResponseEntity.ok(commentService.getByProductIdAndUserId(productId, userId));
        } catch (EMallsException e) {
            if (MessageKey.COMMENT_NOT_FOUND.getKey().equals(e.getMessage())) {
                return EMallsResponseEntity.ok(null);
            }
            throw e;
        }
    }

    // USER — WRITE

    @PostMapping("/products/{productId}/comments")
    @PreAuthorize("@auth.isCustomer()")
    public EMallsResponseEntity<ProductCommentDto> create(
            @PathVariable Long productId,
            @RequestBody @Validated({Default.class, OnCreate.class}) ProductCommentDto dto) {
        return EMallsResponseEntity.created(commentService.create(productId, dto));
    }

    @PutMapping("/products/{productId}/comments")
    @PreAuthorize("@auth.isCustomer()")
    public EMallsResponseEntity<ProductCommentDto> update(
            @PathVariable Long productId,
            @RequestBody @Validated({Default.class, OnUpdate.class}) ProductCommentDto dto) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        return EMallsResponseEntity.ok(commentService.update(productId, userId, dto));
    }

    @DeleteMapping("/products/{productId}/comments")
    @PreAuthorize("@auth.isCustomer()")
    public EMallsResponseEntity<Void> delete(@PathVariable Long productId) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        commentService.delete(productId, userId);
        return EMallsResponseEntity.noContent(null);
    }

    @PostMapping("/products/{productId}/comments/{commentId}/report")
    @PreAuthorize("@auth.isCustomer()")
    public EMallsResponseEntity<ProductCommentDto> report(
            @PathVariable Long productId,
            @PathVariable Long commentId) {
        Long reporterUserId = SecurityContextUtil.getCurrentUserId();
        return EMallsResponseEntity.ok(
                commentService.report(productId, commentId, reporterUserId));
    }

    // ADMIN

    @GetMapping("/admin/comments/stats")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<Map<CommentStatus, Long>> getModerationStats() {
        return EMallsResponseEntity.ok(commentService.getModerationStats());
    }

    @GetMapping("/admin/comments")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<List<ProductCommentDto>> getByStatus(
            @RequestParam CommentStatus status) {
        return EMallsResponseEntity.ok(commentService.getByStatus(status));
    }

    @GetMapping("/admin/products/{productId}/comments")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<List<ProductCommentDto>> getAllByProduct(
            @PathVariable Long productId) {
        return EMallsResponseEntity.ok(commentService.getAllByProductId(productId));
    }

    @PatchMapping("/admin/comments/{commentId}/approve")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<ProductCommentDto> approve(@PathVariable Long commentId) {
        return EMallsResponseEntity.ok(commentService.adminApprove(commentId));
    }

    @PatchMapping("/admin/comments/{commentId}/reject")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<ProductCommentDto> reject(@PathVariable Long commentId) {
        return EMallsResponseEntity.ok(commentService.adminReject(commentId));
    }

    @PatchMapping("/admin/comments/{commentId}/flag")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<ProductCommentDto> flag(@PathVariable Long commentId) {
        return EMallsResponseEntity.ok(commentService.adminFlag(commentId));
    }

    @PatchMapping("/admin/comments/{commentId}/remoderate")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<ProductCommentDto> remoderate(@PathVariable Long commentId) {
        return EMallsResponseEntity.ok(commentService.adminRemoderate(commentId));
    }
}
