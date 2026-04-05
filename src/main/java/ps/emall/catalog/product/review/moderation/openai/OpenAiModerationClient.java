package ps.emall.catalog.product.review.moderation.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ps.emall.catalog.product.review.moderation.ModerationDecision;
import ps.emall.catalog.product.review.moderation.ModerationProvider;
import ps.emall.catalog.product.review.moderation.ModerationResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
@Slf4j
public class OpenAiModerationClient {

    private static final String OPENAI_MODERATION_URL = "https://api.openai.com/v1/moderations";
    private static final String OPENAI_CHAT_URL = "https://api.openai.com/v1/chat/completions";
    private static final Pattern ARABIC_SCRIPT_PATTERN = Pattern.compile("[\\u0600-\\u06FF]");

    private static final String ARABIC_SYSTEM_PROMPT = """
            أنت مصنف محتوى متخصص في اللغة العربية. مهمتك تحليل تعليق المستخدم لتحديد إذا كان يحتوي على محتوى ممنوع:

            المحتويات الممنوعة:
            - التحرش أو السب أو اللغة المسيئة (harassment)
            - التهديد بالعنف (violence/threat)
            - خطاب الكراهية (hate speech)
            - المحتوى الجنسي أو الفاحش (sexual content)
            - إيذاء النفس أو الآخرين (self-harm / harm)
            - أي محتوى غير قانوني أو ضار (illicit / illegal content)

            نقاط مهمة:
            - النقد البناء أو التعبير عن الرأي مثل "ما أعجبني اللون، لكن الخامة ممتازة" مسموح به دائمًا.
            - قرر بعناية قبل رفض التعليقات التي تحتوي على رأي شخصي.

            تنسيق الرد:
            - إذا كان التعليق آمن:
            {
              "decision": "APPROVED",
              "category": null,
              "confidence": 1.0
            }
            - إذا كان التعليق يحتوي على محتوى ممنوع:
            {
              "decision": "REJECTED",
              "category": "<فئة المحتوى الممنوع>",
              "confidence": <قيمة من 0 إلى 1>
            }

            لا تقدم أي كلمات إضافية أو شرح خارج الـ JSON.
            """;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public OpenAiModerationClient(RestTemplate restTemplate, ObjectMapper objectMapper,
                                  @Value("${moderation.openai.api-key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    public ModerationResult moderate(String text) {
        if (!isConfigured()) {
            return errorResult("OpenAI API key not configured — rule-based result used");
        }

        ModerationResult result = callModerationApi(text);

        if (result.isApiError()) return result;
        if (result.getDecision() == ModerationDecision.REJECTED) return result;

        // fallback for Arabic
        if (containsArabic(text) && result.getDecision() == ModerationDecision.APPROVED) {
            ModerationResult arabicResult = callChatApiForArabic(text);
            return arabicResult.isApiError() ? result : arabicResult;
        }

        return result;
    }

    private ModerationResult callModerationApi(String text) {
        try {
            OpenAiModerationRequest request = new OpenAiModerationRequest(text, "omni-moderation-latest");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            ResponseEntity<OpenAiModerationResponse> responseEntity = restTemplate.exchange(
                    OPENAI_MODERATION_URL, HttpMethod.POST,
                    new HttpEntity<>(request, headers), OpenAiModerationResponse.class
            );

            OpenAiModerationResponse response = responseEntity.getBody();
            if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
                return errorResult("Empty response from OpenAI Moderation API");
            }

            OpenAiModerationResponse.Result r = response.getResults().get(0);
            boolean flagged = r.isFlagged();
            String topCategory = r.getTopFlaggedCategory();
            double maxScore = r.getMaxScore();

            return ModerationResult.builder()
                    .provider(ModerationProvider.OPENAI)
                    .decision(flagged ? ModerationDecision.REJECTED : ModerationDecision.APPROVED)
                    .reason(flagged ? topCategory : null)
                    .confidence(BigDecimal.valueOf(maxScore).setScale(4, RoundingMode.HALF_UP))
                    .rawResponse(toJson(response))
                    .apiError(false)
                    .build();

        } catch (Exception e) {
            log.warn("OpenAI Moderation API call failed: {}", e.getMessage());
            return errorResult(e.getMessage());
        }
    }

    private ModerationResult callChatApiForArabic(String text) {
        log.info("Calling OpenAI Chat API (gpt-4o-mini) for Arabic moderation");

        try {
            OpenAiChatRequest.Message systemMsg = new OpenAiChatRequest.Message("system", ARABIC_SYSTEM_PROMPT);
            OpenAiChatRequest.Message userMsg = new OpenAiChatRequest.Message("user", text);
            OpenAiChatRequest request = new OpenAiChatRequest(
                    "gpt-4o-mini",
                    List.of(systemMsg, userMsg),
                    50,
                    0.0
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            ResponseEntity<OpenAiChatResponse> responseEntity = restTemplate.exchange(
                    OPENAI_CHAT_URL, HttpMethod.POST, new HttpEntity<>(request, headers), OpenAiChatResponse.class
            );

            OpenAiChatResponse response = responseEntity.getBody();
            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                return errorResult("Empty response from OpenAI Chat API");
            }

            String content = response.getChoices().get(0).getMessage().getContent().trim();
            Map<String, Object> json = objectMapper.readValue(content, Map.class);

            String decisionStr = ((String) json.get("decision")).toUpperCase();
            ModerationDecision decision = decisionStr.equals("REJECTED") ? ModerationDecision.REJECTED : ModerationDecision.APPROVED;
            String category = (String) json.get("category");
            double confidence = ((Number) json.getOrDefault("confidence", 1.0)).doubleValue();

            return ModerationResult.builder()
                    .provider(ModerationProvider.OPENAI)
                    .decision(decision)
                    .reason(category)
                    .confidence(BigDecimal.valueOf(confidence).setScale(4, RoundingMode.HALF_UP))
                    .rawResponse(toJson(response))
                    .apiError(false)
                    .build();

        } catch (Exception e) {
            log.warn("Chat API fallback failed: {}", e.getMessage());
            return errorResult(e.getMessage());
        }
    }

    private boolean containsArabic(String text) {
        return text != null && ARABIC_SCRIPT_PATTERN.matcher(text).find();
    }

    private ModerationResult errorResult(String reason) {
        return ModerationResult.builder()
                .provider(ModerationProvider.OPENAI)
                .decision(ModerationDecision.ERROR)
                .reason(reason)
                .apiError(true)
                .build();
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    // Inner classes for Chat API
    private static class OpenAiChatRequest {
        private String model;
        private List<Message> messages;
        private int max_tokens;
        private double temperature;

        public OpenAiChatRequest(String model, List<Message> messages, int max_tokens, double temperature) {
            this.model = model;
            this.messages = messages;
            this.max_tokens = max_tokens;
            this.temperature = temperature;
        }

        public String getModel() { return model; }
        public List<Message> getMessages() { return messages; }
        public int getMax_tokens() { return max_tokens; }
        public double getTemperature() { return temperature; }

        public static class Message {
            private String role;
            private String content;

            public Message(String role, String content) {
                this.role = role;
                this.content = content;
            }

            public String getRole() { return role; }
            public String getContent() { return content; }
        }
    }

    private static class OpenAiChatResponse {
        private List<Choice> choices;
        public List<Choice> getChoices() { return choices; }
        public void setChoices(List<Choice> choices) { this.choices = choices; }

        public static class Choice {
            private Message message;
            public Message getMessage() { return message; }
            public void setMessage(Message message) { this.message = message; }
        }

        public static class Message {
            private String content;
            public String getContent() { return content; }
            public void setContent(String content) { this.content = content; }
        }
    }
}