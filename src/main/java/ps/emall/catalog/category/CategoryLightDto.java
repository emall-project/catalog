package ps.emall.catalog.category;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ps.emall.catalog.client.media_manager.FileLightDto;

import java.util.UUID;

@Builder
@Getter
@Setter
public class CategoryLightDto {
    private Long id;
    private String name;
    private String slug;
    private UUID imageId;
    private FileLightDto image;
}
