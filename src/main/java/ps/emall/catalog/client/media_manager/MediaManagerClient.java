package ps.emall.catalog.client.media_manager;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "media-manager-service", url = "${services.media-manager.host}:${services.media-manager.port}")
public interface MediaManagerClient {
    @GetMapping("files/{id}/exists")
    Boolean exists(@PathVariable("id") UUID id);

    @GetMapping("/files/{id}")
    MediaResponse<FileDto> getById(@PathVariable("id") UUID id);

    @GetMapping("files")
    List<MediaResponse<FileDto>> getById(@RequestParam("ids") List<UUID> ids);
}
