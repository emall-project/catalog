package ps.emall.catalog.client.media_manager;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ps.emall.catalog.config.service.MediaManagerFeignConfig;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "media-manager-service",
        url = "${services.media-manager.host}:${services.media-manager.port}",
        configuration = MediaManagerFeignConfig.class
)
public interface MediaManagerClient {
    @GetMapping("internal/files/{id}/exists")
    MediaResponse<Boolean> exists(@PathVariable("id") UUID id);

    @GetMapping("/internal/files/{id}")
    MediaResponse<FileDto> getById(@PathVariable("id") UUID id);

    @PostMapping("internal/files/list")
    MediaResponse<List<FileDto>> getByIds(@RequestBody List<UUID> ids);
}
