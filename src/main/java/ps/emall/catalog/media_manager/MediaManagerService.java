package ps.emall.catalog.media_manager;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaManagerService {

    private final MediaManagerClient client;
    public boolean isFileExist(UUID fileKey) {
        return client.isFileExist(fileKey);
    }
}
