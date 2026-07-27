package com.recruitment.engine.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitment.engine.exception.AIProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Component
public class GeminiClient {

    private final RestClient geminiRestClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api-key}")
    private String apiKey;

    public GeminiClient(RestClient geminiRestClient) {
        this.geminiRestClient = geminiRestClient;
    }

    public String callGemini(String prompt) {
        try {
            Map<String, Object> body = Map.of(
                    "contents", new Object[] {
                            Map.of("parts", new Object[] { Map.of("text", prompt) })
                    });

            String response = geminiRestClient.post()
                    .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                    .body(body)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);
            String text = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            return cleanJson(text);

        } catch (RestClientException e) {
            throw new AIProcessingException("Gemini API call failed or timed out: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new AIProcessingException("Failed to parse Gemini response: " + e.getMessage(), e);
        }
    }

    private String cleanJson(String text) {
        return text.replace("```json", "").replace("```", "").trim();
    }
}