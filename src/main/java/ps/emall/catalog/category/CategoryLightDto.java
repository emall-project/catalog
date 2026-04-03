package ps.emall.catalog.category;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ps.emall.catalog.client.media_manager.FileLightDto;


@Builder
@Getter
@Setter
public class CategoryLightDto {
    private Long id;
    private String name;
    private FileLightDto image;
    private Integer depthLevel;
}


