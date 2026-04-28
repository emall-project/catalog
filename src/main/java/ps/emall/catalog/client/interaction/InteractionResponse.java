package ps.emall.catalog.client.interaction;

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
public class InteractionResponse<T> {

    List<ErrorCode> errorCodes;
    private HttpStatus status;
    private String message;
    private T data;

    public static <T> InteractionResponse<T> of(
            HttpStatus status,
            String message,
            T data
    ) {
        return InteractionResponse.<T>builder()
                .status(status)
                .message(message)
                .data(data)
                .build();
    }
}
