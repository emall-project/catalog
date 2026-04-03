package ps.emall.catalog.category;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ps.emall.catalog.client.media_manager.FileLightDto;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
public class CategoryTreeDto {
    private Long id;
    private String name;
    private FileLightDto image;
    private Integer depthLevel;

    @Builder.Default
    private List<CategoryTreeDto> children = new ArrayList<>();
}