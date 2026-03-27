package ps.emall.catalog.media;


import lombok.*;
import ps.emall.catalog.common.Entity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reference {
    private Entity entity;
    private Long entityId;
    private String entityName;
}
