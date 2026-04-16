package ps.emall.catalog.common.base;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EMallsBaseDto {
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
