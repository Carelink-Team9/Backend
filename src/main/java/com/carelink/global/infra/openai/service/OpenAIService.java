package com.carelink.global.infra.openai.service;

import com.carelink.global.infra.openai.client.OpenAIClient;
import com.carelink.global.infra.openai.dto.ChatRequest;
import com.carelink.global.infra.openai.dto.ChatResponse;
import com.carelink.global.infra.upstage.client.UpstageOcrClient;
import com.carelink.recommendation.dto.DepartmentRecommendResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAIService {

    private final OpenAIClient openAIClient;
    private final UpstageOcrClient upstageOcrClient;
    private final ObjectMapper objectMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public record ParsedDrug(
            String drugName,
            String originalName,
            String dosage,
            String frequency,
            String duration,
            String translatedContent,
            String sideEffects,
            String precautions,
            String foodInteraction,
            String handwrittenNote
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
     * 2. 처방전 이미지 분석 (Upstage OCR → GPT 파싱)
     */
    public List<ParsedDrug> parsePrescriptionImage(String imageRelativePath, String targetLanguage) {
        try {
            // Step 1: Upstage OCR로 텍스트 추출
            String fileName = imageRelativePath.substring(imageRelativePath.lastIndexOf("/") + 1);
            Path imagePath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(fileName);
            String extractedText = upstageOcrClient.extractText(imagePath);

            if (extractedText == null || extractedText.isBlank()) {
                log.warn("OCR 추출 텍스트가 비어있습니다. 빈 결과 반환.");
                return List.of();
            }

            // Step 2: GPT로 구조화된 약 정보 파싱
            String systemMessage = "You are a professional Korean pharmacist and medical assistant. " +
                    "Your task is to parse Korean prescription OCR text into comprehensive structured drug information. " +
                    "The OCR text may contain handwritten annotations such as circled numbers (①②③ or (1)(2)(3)), " +
                    "pen-written numbers beside drug names indicating quantity/dosage, or circled drug names indicating doctor selection. " +
                    "Use your medical knowledge to fill in side effects, precautions, and food interactions for each drug based on its name, " +
                    "even if not explicitly written in the prescription.";
            String userMessage = String.format(
                    "Extract ALL drug information from the following prescription OCR text.\n\n" +
                    "CRITICAL RULES:\n" +
                    "- Extract EVERY drug found, including those with only handwritten annotations.\n" +
                    "- Handwritten circled numbers (①②③) or pen numerals beside a drug name = quantity or selection marker.\n" +
                    "- Use your pharmacological knowledge to provide sideEffects, precautions, foodInteraction even if not in the text.\n" +
                    "- If a value is truly unknown or not applicable, use null.\n\n" +
                    "For each drug, extract these fields:\n" +
                    "1. 'drugName': Clean Korean drug name for DB search (strip circled numbers/symbols).\n" +
                    "2. 'originalName': Exact name as it appears in the prescription including any annotation.\n" +
                    "3. 'dosage': Amount per dose (e.g., '500mg', '1정', '2캡슐'). Use handwritten number if present.\n" +
                    "4. 'frequency': Full schedule in Korean (e.g., '1일 3회 식후 30분').\n" +
                    "5. 'duration': Duration in Korean (e.g., '3일분', '7일').\n" +
                    "6. 'translatedContent': 1–2 sentence plain explanation of this drug's purpose in %s.\n" +
                    "7. 'sideEffects': Common side effects in %s (2–3 items, concise). Use pharmacological knowledge.\n" +
                    "8. 'precautions': Key warnings/precautions in %s (e.g., 'Do not drive', '임산부 복용 금지'). Use pharmacological knowledge.\n" +
                    "9. 'foodInteraction': Food/drink to avoid in %s (e.g., '알코올 금지', '자몽 주스 피할 것'). Use pharmacological knowledge. null if none.\n" +
                    "10. 'handwrittenNote': Any handwritten annotation detected near this drug (e.g., '①', '동그라미', '2정 추가'). null if none.\n\n" +
                    "Return ONLY a valid JSON array — no markdown fences, no extra text:\n" +
                    "[{\"drugName\":\"...\",\"originalName\":\"...\",\"dosage\":\"...\",\"frequency\":\"...\",\"duration\":\"...\",\"translatedContent\":\"...\",\"sideEffects\":\"...\",\"precautions\":\"...\",\"foodInteraction\":\"...\",\"handwrittenNote\":null}]\n\n" +
                    "Prescription OCR text:\n%s",
                    targetLanguage, targetLanguage, targetLanguage, targetLanguage, extractedText
            );

            ChatRequest request = new ChatRequest("gpt-4o", List.of(
                    new ChatRequest.Message("system", systemMessage),
                    new ChatRequest.Message("user", userMessage)
            ));

            ChatResponse response = openAIClient.sendChatRequest(request);
            String jsonResult = extractJson(response.getChoices().get(0).getMessage().getContent());
            return objectMapper.readValue(jsonResult, new TypeReference<List<ParsedDrug>>() {});
        } catch (Exception e) {
            log.error("Upstage OCR / GPT Parsing Failed: ", e);
            return List.of();
        }
    }

    /**
     * 3. [추가] 증상 기반 진료과 추천 (Recommendation)
     * 버튼(키워드 리스트)과 직접 입력(문장)을 모두 지원합니다.
     */
    public DepartmentRecommendResponse recommendDepartment(String symptomInput, String targetLanguage) {
        try {
            // 시스템 메시지: 키워드와 문장 모두 분석 가능함을 명시
            String systemMessage = "You are a professional medical triage assistant for 'CareLink'. " +
                    "Analyze the user's symptoms, which may be provided as a list of keywords or a natural language sentence. " +
                    "Provide the most appropriate Korean medical department.";

            // 유저 메시지: 의사용 요약(doctorSummary)을 포함한 명확한 JSON 구조 요청
            String userMessage = String.format(
                    "Symptoms Data: [%s]. Target Language: %s. " +
                            "Please analyze the symptoms and return the recommendation in this JSON format: " +
                            "{" +
                            "  \"mainDepartment\": \"(Korean Name, e.g., 내과)\", " +
                            "  \"translatedMainDepartment\": \"(Name in %s)\", " +
                            "  \"mainConfidence\": 95, " +
                            "  \"reason\": \"(Reason in Korean)\", " +
                            "  \"translatedReason\": \"(Reason in %s)\", " +
                            "  \"doctorSummary\": \"(Summarize the patient's symptoms professionally in 1-2 Korean sentences for a doctor)\", " +
                            "  \"alternatives\": [" +
                            "    {\"departmentName\": \"(Korean)\", \"translatedDepartmentName\": \"(Translated in %s)\", \"confidence\": 70}" +
                            "  ]" +
                            "}",
                    symptomInput, targetLanguage, targetLanguage, targetLanguage, targetLanguage
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
            // 에러 발생 시 기본값 반환
            return DepartmentRecommendResponse.builder()
                    .mainDepartment("내과")
                    .translatedMainDepartment("Internal Medicine")
                    .mainConfidence(50)
                    .reason("Error occurred during analysis.")
                    .doctorSummary("증상 분석 중 오류가 발생했습니다.")
                    .build();
        }
    }

    private String extractJson(String content) {
        content = content.trim();
        if (content.startsWith("```json")) {
            return content.substring(7, content.length() - 3).trim();
        }
        return content;
    }

    /**
     * 4. [신규] 처방전 기반 챗봇 답변 생성
     */
    public String getPrescriptionChatAnswer(String userMessage, String drugContext, String targetLanguage) {
        try {
            String systemMessage = String.format(
                    "You are a professional and kind pharmacist assistant for 'CareLink'. " +
                            "Answer the user's question ONLY based on the following prescription drug information: [%s]. " +
                            "If the question is not about these drugs, kindly ask them to stay on topic. " +
                            "Provide the answer in %s language.",
                    drugContext, targetLanguage
            );

            ChatRequest request = new ChatRequest("gpt-4o-mini", List.of(
                    new ChatRequest.Message("system", systemMessage),
                    new ChatRequest.Message("user", userMessage)
            ));

            ChatResponse response = openAIClient.sendChatRequest(request);
            return response.getChoices().get(0).getMessage().getContent().trim();
        } catch (Exception e) {
            log.error("Prescription Chat Failed: ", e);
            return "죄송합니다. 답변을 생성하는 중 오류가 발생했습니다.";
        }
    }

}