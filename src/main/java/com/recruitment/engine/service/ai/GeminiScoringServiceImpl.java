package com.recruitment.engine.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitment.engine.exception.AIProcessingException;
import org.springframework.stereotype.Service;

@Service
public class GeminiScoringServiceImpl implements AIScoringService {

    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeminiScoringServiceImpl(GeminiClient geminiClient) {
        this.geminiClient = geminiClient;
    }

    @Override
    public AIScoreResult scoreCandidate(String candidateProfile, String jobDescriptionText) {
        String prompt = """
                You are a recruitment screening assistant. Compare the candidate profile against
                the job description and return ONLY valid JSON, no markdown, in this exact shape:
                {
                  "matchPercentage": number (0-100),
                  "confidence": "HIGH" | "MEDIUM" | "LOW",
                  "matchingSkills": "comma separated list",
                  "missingSkills": "comma separated list",
                  "strengths": "short text",
                  "weaknesses": "short text",
                  "summary": "2-3 sentence summary"
                }

                Candidate Profile:
                %s

                Job Description:
                %s
                """.formatted(candidateProfile, jobDescriptionText);

        String json = geminiClient.callGemini(prompt);

        try {
            JsonNode node = objectMapper.readTree(json);
            return new AIScoreResult(
                    node.path("matchPercentage").asDouble(),
                    node.path("confidence").asText("MEDIUM"),
                    node.path("matchingSkills").asText(null),
                    node.path("missingSkills").asText(null),
                    node.path("strengths").asText(null),
                    node.path("weaknesses").asText(null),
                    node.path("summary").asText(null));
        } catch (Exception e) {
            throw new AIProcessingException("Malformed scoring response from Gemini", e);
        }
    }
}