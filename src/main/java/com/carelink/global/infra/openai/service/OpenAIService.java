package com.carelink.global.infra.openai.service;

import com.carelink.global.infra.openai.client.OpenAIClient;
import com.carelink.global.infra.openai.dto.ChatRequest;
import com.carelink.global.infra.openai.dto.ChatResponse;
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
    private final ObjectMapper objectMapper; // JSON 파싱을 위해 추가

    @Value("${file.upload-dir}")
    private String uploadDir; // 파일 경로 읽기용

    /**
     * 처방전 분석 결과 데이터 구조 (DTO)
     * PrescriptionService에서 'OpenAIService.ParsedDrug'로 참조하게 됩니다.
     */
    public record ParsedDrug(
            String drugName,       // DB 매핑용 한국어 약 이름
            String originalName,   // 이미지상의 원문 이름
            String dosage,         // 용량 (예: 1정)
            String frequency,      // 횟수 (예: 1일 3회)
            String duration,       // 기간 (예: 3일)
            String translatedContent // 사용자의 모국어로 된 간단한 설명
    ) {}

    /**
     * 1. [기존 기능] 텍스트 번역
     */
    @Cacheable(value = "translations", key = "#text + '_' + #targetLanguage", unless = "#result == 'Translation Failed'")
    public String translate(String text, String targetLanguage) {
        if (text == null || text.isBlank()) return text;

        log.info("캐시 미존재 - GPT 번역 호출 중... (Language: {}, Text: {})", targetLanguage, text.substring(0, Math.min(text.length(), 10)));

        String systemMessage = "You are a professional medical translator for 'CareLink' app. Translate naturally.";
        String userMessage = String.format("Translate to '%s': %s", targetLanguage, text);

        ChatRequest request = new ChatRequest(
                "gpt-4o-mini",
                List.of(
                        new ChatRequest.Message("system", systemMessage),
                        new ChatRequest.Message("user", userMessage)
                )
        );

        ChatResponse response = openAIClient.sendChatRequest(request);
        return (response != null && !response.getChoices().isEmpty())
                ? response.getChoices().get(0).getMessage().getContent().trim()
                : "Translation Failed";
    }

    /**
     * 2. [신규 기능] 처방전 이미지 분석 (Vision)
     */
    public List<ParsedDrug> parsePrescriptionImage(String imageRelativePath, String targetLanguage) {
        try {
            // 이미지 파일을 Base64로 인코딩
            String base64Image = encodeImageToBase64(imageRelativePath);

            String systemMessage = "You are a professional medical assistant. Analyze the prescription image.";
            String userMessage = String.format(
                    "Extract drug information. " +
                            "1. 'drugName': Precise Korean medicine name for database searching. " +
                            "2. 'translatedContent': 1-sentence explanation of the drug's purpose in %s language. " +
                            "Return ONLY a JSON array: [{\"drugName\":\"...\", \"originalName\":\"...\", \"dosage\":\"...\", \"frequency\":\"...\", \"duration\":\"...\", \"translatedContent\":\"...\"}]",
                    targetLanguage
            );

            // Vision 전송용 ChatRequest 생성 (content가 List인 형태)
            ChatRequest request = new ChatRequest(
                    "gpt-4o", // Vision은 성능을 위해 gpt-4o 권장
                    List.of(
                            new ChatRequest.Message("system", systemMessage),
                            new ChatRequest.Message("user", List.of(
                                    Map.of("type", "text", "text", userMessage),
                                    Map.of("type", "image_url", "image_url", Map.of("url", "data:image/jpeg;base64," + base64Image))
                            ))
                    )
            );

            ChatResponse response = openAIClient.sendChatRequest(request);
            String jsonResult = response.getChoices().get(0).getMessage().getContent().trim();

            // 마크다운 태그 제거 (```json ... ```)
            if (jsonResult.startsWith("```json")) {
                jsonResult = jsonResult.substring(7, jsonResult.length() - 3);
            }

            return objectMapper.readValue(jsonResult, new TypeReference<List<ParsedDrug>>() {});

        } catch (Exception e) {
            log.error("GPT Vision Analysis Failed: ", e);
            return List.of();
        }
    }

    private String encodeImageToBase64(String imageRelativePath) throws IOException {
        // 경로에서 파일명만 추출하여 실제 저장 경로와 결합
        String fileName = imageRelativePath.substring(imageRelativePath.lastIndexOf("/") + 1);
        Path path = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(fileName);
        return Base64.getEncoder().encodeToString(Files.readAllBytes(path));
    }
}