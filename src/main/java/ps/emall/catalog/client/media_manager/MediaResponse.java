package ps.emall.catalog.client.media_manager;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import org.springframework.http.HttpStatus;
import ps.emall.catalog.common.response.ErrorCode;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@JsonPropertyOrder({
        "status",
        "message",
        "errorCodes",
        "data"
})
public class MediaResponse<T> {

    List<ErrorCode> errorCodes;
    private HttpStatus status;
    private String message;
    private T data;

    public static <T> MediaResponse<T> of(
            HttpStatus status,
            String message,
            T data
    ) {
        return MediaResponse.<T>builder()
                .message(message)
                .data(data)
                .build();
    }
}
