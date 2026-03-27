package ps.emall.catalog.media;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MediaUsageDto {
    private Boolean inUse;
    private List<Reference> references = new ArrayList<>();
}
