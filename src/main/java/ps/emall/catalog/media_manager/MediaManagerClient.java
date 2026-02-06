package ps.emall.catalog.media_manager;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ps.emall.catalog.media_manager.MediaConfig;
import ps.emall.catalog.media_manager.MediaManagerEndpoints;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MediaManagerClient {

    private final MediaConfig config;
    private final WebClient webClient;

    public boolean isFileExist(UUID fileKey) {
        return webClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(config.getBaseUrl() + MediaManagerEndpoints.IS_FILE_EXIST)
                                .build(fileKey)
                )
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }

}
