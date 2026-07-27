package com.recruitment.engine.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitment.engine.exception.AIProcessingException;
import org.springframework.stereotype.Service;

@Service
public class GeminiResumeParsingServiceImpl implements ResumeParsingService {

    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeminiResumeParsingServiceImpl(GeminiClient geminiClient) {
        this.geminiClient = geminiClient;
    }

    @Override
    public ParsedResumeData parseResume(String resumeText) {
        String prompt = """
                You are a resume parser. Extract the following fields from the resume text below
                and return ONLY valid JSON, no markdown, no explanation, in this exact shape:
                {
                  "skills": "comma separated list",
                  "experienceYears": number,
                  "education": "summary text",
                  "projects": "summary text",
                  "certifications": "comma separated list or empty string"
                }

                Resume text:
                %s
                """.formatted(resumeText);

        String json = geminiClient.callGemini(prompt);

        try {
            JsonNode node = objectMapper.readTree(json);
            return new ParsedResumeData(
                    node.path("skills").asText(null),
                    node.has("experienceYears") && !node.path("experienceYears").isNull()
                            ? node.path("experienceYears").asDouble()
                            : null,
                    node.path("education").asText(null),
                    node.path("projects").asText(null),
                    node.path("certifications").asText(null));
        } catch (Exception e) {
            throw new AIProcessingException("Malformed resume parsing response from Gemini", e);
        }
    }
}