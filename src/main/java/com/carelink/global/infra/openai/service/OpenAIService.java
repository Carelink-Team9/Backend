package com.carelink.global.infra.openai.service;

import com.carelink.global.infra.openai.client.OpenAIClient;
import com.carelink.global.infra.openai.dto.ChatRequest;
import com.carelink.global.infra.openai.dto.ChatResponse;
import com.carelink.recommendation.dto.DepartmentRecommendResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAIService {

    private final OpenAIClient openAIClient;
    private final ObjectMapper objectMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public record ParsedDrug(
            String drugName,
            String originalName,
            String dosage,
            String frequency,
            String duration,
            String translatedContent
    ) {}

    /**
     * 1. 텍스트 번역
     */
    @Cacheable(value = "translations", key = "#text + '_' + #targetLanguage", unless = "#result == 'Translation Failed'")
    public String translate(String text, String targetLanguage) {
        if (text == null || text.isBlank()) return text;
        log.info("캐시 미존재 - GPT 번역 호출 중... (Language: {}, Text: {})", targetLanguage, text.substring(0, Math.min(text.length(), 10)));
        String systemMessage = "You are a professional medical translator for 'CareLink' app. Translate naturally.";
        String userMessage = String.format("Translate to '%s': %s", targetLanguage, text);

        ChatRequest request = new ChatRequest("gpt-4o-mini", List.of(
                new ChatRequest.Message("system", systemMessage),
                new ChatRequest.Message("user", userMessage)
        ));

        ChatResponse response = openAIClient.sendChatRequest(request);
        return (response != null && !response.getChoices().isEmpty())
                ? response.getChoices().get(0).getMessage().getContent().trim()
                : "Translation Failed";
    }

    /**
     * 2. 처방전 이미지 분석 (Vision)
     */
    public List<ParsedDrug> parsePrescriptionImage(String imageRelativePath, String targetLanguage) {
        try {
            String base64Image = encodeImageToBase64(imageRelativePath);
            String systemMessage = "You are a professional medical assistant. Analyze the prescription image.";
            String userMessage = String.format(
                    "Extract drug information. 1. 'drugName': Precise Korean medicine name for database searching. " +
                            "2. 'translatedContent': 1-sentence explanation in %s. " +
                            "Return ONLY JSON array: [{\"drugName\":\"...\", \"originalName\":\"...\", \"dosage\":\"...\", \"frequency\":\"...\", \"duration\":\"...\", \"translatedContent\":\"...\"}]",
                    targetLanguage
            );

            ChatRequest request = new ChatRequest("gpt-4o", List.of(
                    new ChatRequest.Message("system", systemMessage),
                    new ChatRequest.Message("user", List.of(
                            Map.of("type", "text", "text", userMessage),
                            Map.of("type", "image_url", "image_url", Map.of("url", "data:image/jpeg;base64," + base64Image))
                    ))
            ));

            ChatResponse response = openAIClient.sendChatRequest(request);
            String jsonResult = extractJson(response.getChoices().get(0).getMessage().getContent());
            return objectMapper.readValue(jsonResult, new TypeReference<List<ParsedDrug>>() {});
        } catch (Exception e) {
            log.error("GPT Vision Analysis Failed: ", e);
            return List.of();
        }
    }

    /**
     * 3. [추가] 증상 기반 진료과 추천 (Recommendation)
     */
    public DepartmentRecommendResponse recommendDepartment(List<String> symptoms, String targetLanguage) {
        try {
            String symptomText = String.join(", ", symptoms);
            String systemMessage = "You are a medical triage assistant. You must provide department names and reasons in both Korean and the user's target language.";

            String userMessage = String.format(
                    "Symptoms: [%s]. Target Language: %s. " +
                            "Please provide the recommendation in the following JSON format: " +
                            "{" +
                            "  \"mainDepartment\": \"(Korean Name)\", " +
                            "  \"translatedMainDepartment\": \"(Translated Name in %s)\", " +
                            "  \"mainConfidence\": 95, " +
                            "  \"reason\": \"(Reason in Korean)\", " +
                            "  \"translatedReason\": \"(Reason in %s)\", " +
                            "  \"alternatives\": [" +
                            "    {\"departmentName\": \"(Korean)\", \"translatedDepartmentName\": \"(Translated in %s)\", \"confidence\": 70}" +
                            "  ]" +
                            "}",
                    symptomText, targetLanguage, targetLanguage, targetLanguage, targetLanguage
            );

            ChatRequest request = new ChatRequest("gpt-4o-mini", List.of(
                    new ChatRequest.Message("system", systemMessage),
                    new ChatRequest.Message("user", userMessage)
            ));

            ChatResponse response = openAIClient.sendChatRequest(request);
            String jsonResult = extractJson(response.getChoices().get(0).getMessage().getContent());
            return objectMapper.readValue(jsonResult, DepartmentRecommendResponse.class);
        } catch (Exception e) {
            log.error("Recommendation Failed: ", e);
            return DepartmentRecommendResponse.builder().mainDepartment("내과").mainConfidence(50).reason("Error").build();
        }
    }

    private String extractJson(String content) {
        content = content.trim();
        if (content.startsWith("```json")) {
            return content.substring(7, content.length() - 3).trim();
        }
        return content;
    }

    private String encodeImageToBase64(String imageRelativePath) throws IOException {
        String fileName = imageRelativePath.substring(imageRelativePath.lastIndexOf("/") + 1);
        Path path = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(fileName);
        return Base64.getEncoder().encodeToString(Files.readAllBytes(path));
    }
}