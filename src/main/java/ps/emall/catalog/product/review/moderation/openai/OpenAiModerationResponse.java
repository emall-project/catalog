package ps.emall.catalog.product.review.moderation.openai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAiModerationResponse {

    @JsonProperty("results")
    private List<Result> results;

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {

        @JsonProperty("flagged")
        private boolean flagged;

        @JsonProperty("categories")
        private Map<String, Boolean> categories;

        @JsonProperty("category_scores")
        private Map<String, Double> categoryScores;

        public String getTopFlaggedCategory() {
            if (categories == null || categoryScores == null) return null;
            return categories.entrySet().stream()
                    .filter(Map.Entry::getValue)
                    .map(Map.Entry::getKey)
                    .max((a, b) -> Double.compare(
                            categoryScores.getOrDefault(a, 0.0),
                            categoryScores.getOrDefault(b, 0.0)))
                    .orElse(null);
        }

        public double getMaxScore() {
            if (categoryScores == null) return 0.0;
            return categoryScores.values().stream()
                    .mapToDouble(Double::doubleValue)
                    .max()
                    .orElse(0.0);
        }
    }
}