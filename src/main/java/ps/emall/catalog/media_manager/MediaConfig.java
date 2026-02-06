package ps.emall.catalog.media_manager;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "services.media-manager")
@Data
public class MediaConfig {

    private String host;
    private int port;
    private String username;
    private String password;

    public String getBaseUrl() {
        return host + ":" + port;
    }

}
