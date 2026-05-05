package ps.emall.catalog.product.review.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ps.emall.catalog.product.review.comment.ProductCommentDto;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreCommentSummaryDto {
    private Long storeId;
    private long totalComments;
    private long approvedComments;
    private long pendingComments;
    private long reportedComments;
    private long flaggedComments;
    private long rejectedComments;
    private List<ProductCommentDto> recentComments;
}
