package ps.emall.catalog.product.review.comment;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ps.emall.catalog.client.accounts.AccountsClient;
import ps.emall.catalog.common.exception.EMallsException;
import ps.emall.catalog.product.Product;
import ps.emall.catalog.product.ProductExceptions;
import ps.emall.catalog.product.ProductRepository;
import ps.emall.catalog.product.review.moderation.ModerationService;
import ps.emall.catalog.security.SecurityContextUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCommentServiceImpl implements ProductCommentService {

    private final ProductCommentRepository commentRepository;
    private final ProductRepository productRepository;
    private final ModerationService moderationService;
    private final AccountsClient accountsClient;

    private static final Set<CommentStatus> EDITABLE_STATUSES = Set.of(
            CommentStatus.PENDING_MODERATION,
            CommentStatus.APPROVED,
            CommentStatus.REJECTED
    );

    // USER: READ

    @Override
    @Transactional(readOnly = true)
    public List<ProductCommentDto> getApprovedByProductId(Long productId) {
        verifyProductExists(productId);

        return commentRepository
                .findByProduct_IdAndStatusOrderByCreatedAtDesc(productId, CommentStatus.APPROVED)
                .stream()
                .map(ProductCommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductCommentDto getByProductIdAndUserId(Long productId, Long userId) {
        ProductComment comment = commentRepository
                .findByProduct_IdAndUserId(productId, userId)
                .orElseThrow(CommentExceptions::commentNotFound);

        return ProductCommentMapper.toDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductCommentDto> getMyComments(Long userId) {
        return commentRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(ProductCommentMapper::toDtoWithProductInfo)
                .collect(Collectors.toList());
    }

    // USER: WRITE

    @Override
    @Transactional
    public ProductCommentDto create(Long productId, ProductCommentDto dto) {
        Product product = verifyProductExists(productId);

        Long userId = SecurityContextUtil.getCurrentUserId();

        if (commentRepository.existsByProduct_IdAndUserId(productId, userId)) {
            throw CommentExceptions.commentAlreadyExists();
        }

        dto.setUserId(userId);

        ProductComment comment = ProductCommentMapper.toEntity(dto, product);
        ProductComment saved = commentRepository.save(comment);

        log.info("Comment created: id={}, productId={}, userId={} — queued for moderation",
                saved.getCommentId(), productId, dto.getUserId());

        moderationService.enqueue(saved);

        return ProductCommentMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ProductCommentDto update(Long productId, Long userId, ProductCommentDto dto) {
        ProductComment existing = commentRepository
                .findByProduct_IdAndUserId(productId, userId)
                .orElseThrow(CommentExceptions::commentNotFound);

        if (!existing.getUserId().equals(userId)) {
            throw CommentExceptions.notYourComment();
        }

        if (!EDITABLE_STATUSES.contains(existing.getStatus())) {
            throw CommentExceptions.editNotAllowedWhileUnderInvestigation();
        }

        CommentStatus previousStatus = existing.getStatus();

        existing.setContent(dto.getContent());
        existing.setStatus(CommentStatus.PENDING_MODERATION);
        existing.setRejectionReason(null);
        existing.setModerationRetryCount(0);
        existing.setLastModerationAttemptAt(null);

        ProductComment saved = commentRepository.save(existing);

        log.info("Comment updated: id={}, productId={}, userId={}, previousStatus={} — requeued for moderation",
                saved.getCommentId(), productId, userId, previousStatus);

        moderationService.enqueue(saved);

        return ProductCommentMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long productId, Long userId) {
        ProductComment comment = commentRepository
                .findByProduct_IdAndUserId(productId, userId)
                .orElseThrow(CommentExceptions::commentNotFound);

        if (!comment.getUserId().equals(userId)) {
            throw CommentExceptions.notYourComment();
        }

        commentRepository.delete(comment);
        log.info("Comment deleted: id={}, productId={}, userId={}, wasStatus={}",
                comment.getCommentId(), productId, userId, comment.getStatus());
    }

    @Override
    @Transactional
    public ProductCommentDto report(Long productId, Long commentId, Long reporterUserId) {
        ProductComment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentExceptions::commentNotFound);

        if (!comment.getProduct().getId().equals(productId)) {
            throw CommentExceptions.commentNotFound();
        }

        if (comment.getUserId().equals(reporterUserId)) {
            throw CommentExceptions.cannotReportOwnComment();
        }

        if (comment.getStatus() != CommentStatus.APPROVED) {
            throw CommentExceptions.commentNotApprovedForReport();
        }

        comment.setStatus(CommentStatus.REPORTED);
        ProductComment saved = commentRepository.save(comment);

        log.info("Comment {} REPORTED by userId={} — pulled from public view",
                commentId, reporterUserId);

        return ProductCommentMapper.toDto(saved);
    }

    // ADMIN: READ

    @Override
    @Transactional(readOnly = true)
    public List<ProductCommentDto> getAllByProductId(Long productId) {
        verifyProductExists(productId);

        return commentRepository.findByProduct_IdOrderByCreatedAtDesc(productId)
                .stream()
                .map(ProductCommentMapper::toDtoWithLogs)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductCommentDto> getByStatus(CommentStatus status) {
        return commentRepository.findByStatusOrderByCreatedAtAsc(status)
                .stream()
                .map(ProductCommentMapper::toDtoWithLogs)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<CommentStatus, Long> getModerationStats() {
        return Arrays.stream(CommentStatus.values())
                .collect(Collectors.toMap(
                        status -> status,
                        commentRepository::countByStatus
                ));
    }

    // ADMIN: ACTIONS

    @Override
    @Transactional
    public ProductCommentDto adminApprove(Long commentId) {
        ProductComment comment = findCommentOrThrow(commentId);
        CommentStatus previous = comment.getStatus();

        comment.setStatus(CommentStatus.APPROVED);
        comment.setRejectionReason(null);

        ProductComment saved = commentRepository.save(comment);

        log.info("Comment {} manually APPROVED by admin (was: {})", commentId, previous);
        return ProductCommentMapper.toDtoWithLogs(saved);
    }

    @Override
    @Transactional
    public ProductCommentDto adminReject(Long commentId) {
        ProductComment comment = findCommentOrThrow(commentId);
        CommentStatus previous = comment.getStatus();

        comment.setStatus(CommentStatus.REJECTED);
        if (comment.getRejectionReason() == null) {
            comment.setRejectionReason("Rejected by admin after review");
        }

        ProductComment saved = commentRepository.save(comment);

        log.info("Comment {} manually REJECTED by admin (was: {})", commentId, previous);
        return ProductCommentMapper.toDtoWithLogs(saved);
    }

    @Override
    @Transactional
    public ProductCommentDto adminFlag(Long commentId) {
        ProductComment comment = findCommentOrThrow(commentId);
        CommentStatus previous = comment.getStatus();

        comment.setStatus(CommentStatus.FLAGGED);

        ProductComment saved = commentRepository.save(comment);

        log.info("Comment {} FLAGGED by admin (was: {})", commentId, previous);
        return ProductCommentMapper.toDtoWithLogs(saved);
    }

    @Override
    @Transactional
    public ProductCommentDto adminRemoderate(Long commentId) {
        ProductComment comment = findCommentOrThrow(commentId);
        CommentStatus previous = comment.getStatus();

        comment.setStatus(CommentStatus.PENDING_MODERATION);
        comment.setRejectionReason(null);
        comment.setModerationRetryCount(0);
        comment.setLastModerationAttemptAt(null);

        commentRepository.save(comment);

        moderationService.moderateSync(comment);

        ProductComment refreshed = commentRepository.findById(commentId)
                .orElseThrow(CommentExceptions::commentNotFound);

        log.info("Comment {} re-moderated by admin (was: {}, now: {})",
                commentId, previous, refreshed.getStatus());

        return ProductCommentMapper.toDtoWithLogs(refreshed);
    }

    // HELPERS

    private Product verifyProductExists(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(ProductExceptions::productNotFound);
    }

    private ProductComment findCommentOrThrow(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(CommentExceptions::commentNotFound);
    }

    private void validateUserExists(Long userId) {
        try {
            var response = accountsClient.getUserById(userId);
            var user = response != null ? response.getData() : null;
            if (user == null) throw CommentExceptions.userNotFound();
            if (Boolean.FALSE.equals(user.getIsActive())) throw CommentExceptions.userNotFound();
        } catch (FeignException.NotFound e) {
            log.warn("User {} not found in accounts service", userId);
            throw CommentExceptions.userNotFound();
        } catch (FeignException e) {
            log.warn("Accounts service unreachable for userId={}, status={}. Allowing request.",
                    userId, e.status());
        } catch (EMallsException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Unexpected error validating userId={}. Allowing request.", userId);
        }
    }
}