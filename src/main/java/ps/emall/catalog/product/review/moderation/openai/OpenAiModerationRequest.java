package ps.emall.catalog.product.review.moderation.openai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiModerationRequest {

    @JsonProperty("input")
    private String input;

    @JsonProperty("model")
    private String model = "omni-moderation-latest";
}