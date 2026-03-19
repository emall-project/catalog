package ps.emall.catalog.client.media_manager;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileDto {

    private UUID id;

    private String name;

    private Long folderId;

    private String mimeType;

    private String extension;

    private Integer size;


    private String originalFileUrl;
    private String mediumFileUrl;
    private String smallFileUrl;
}
