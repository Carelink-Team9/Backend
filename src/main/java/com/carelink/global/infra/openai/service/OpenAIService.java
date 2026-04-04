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

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAIService {

    private final OpenAIClient openAIClient;
    private final UpstageOcrClient upstageOcrClient;
    private final ObjectMapper objectMapper;

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
     * ISO 언어 코드 → GPT가 확실히 이해하는 언어 이름으로 변환
     */
    private String toLanguageName(String code) {
        return switch (code) {
            case "ko" -> "Korean";
            case "en" -> "English";
            case "ja" -> "Japanese";
            case "zh" -> "Chinese (Simplified)";
            case "vi" -> "Vietnamese";
            case "th" -> "Thai";
            case "uz" -> "Uzbek";
            default   -> "English";
        };
    }

    /**
     * 1. 텍스트 번역
     */
    @Cacheable(value = "translations", key = "#text + '_' + #targetLanguage", unless = "#result == 'Translation Failed'")
    public String translate(String text, String targetLanguage) {
        if (text == null || text.isBlank()) return text;
        log.info("캐시 미존재 - GPT 번역 호출 중... (Language: {}, Text: {})", targetLanguage, text.substring(0, Math.min(text.length(), 10)));
        String langName = toLanguageName(targetLanguage);
        String systemMessage = "You are a professional medical translator for 'CareLink' app. Translate naturally.";
        String userMessage = String.format("Translate to %s: %s", langName, text);

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
    public List<ParsedDrug> parsePrescriptionImage(byte[] imageBytes, String fileName, String targetLanguage) {
        try {
            // Step 1: Upstage OCR로 텍스트 추출 (디스크 경유 없이 바이트 직접 전달)
            String extractedText = upstageOcrClient.extractText(imageBytes, fileName);

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
            String langName = toLanguageName(targetLanguage);
            String userMessage = String.format(
                    "Extract ALL drug information from the following prescription OCR text.\n\n" +
                    "CRITICAL RULES:\n" +
                    "- Extract EVERY drug found, including those with only handwritten annotations.\n" +
                    "- Handwritten circled numbers (①②③) or pen numerals beside a drug name = quantity or selection marker.\n" +
                    "- Use your pharmacological knowledge to provide sideEffects, precautions, foodInteraction even if not in the text.\n" +
                    "- If a value is truly unknown or not applicable, use null.\n\n" +
                    "For each drug, extract these fields:\n" +
                    "1. 'drugName': Clean Korean brand/product name only. Rules:\n" +
                    "   - The drug name is the Korean product/brand name including its strength (e.g., '엘리퀴스정2.5mg', '리피토정10mg'). Stop at the first token that is NOT part of the drug name.\n" +
                    "   - If the drug appears as 'BrandName(GenericName)', use the BRAND name before the parenthesis. Example: '크리맥액(돔페리돈)' → '크리맥액'\n" +
                    "   - Strip ALL of the following: circled numbers/symbols (①②③), controlled-substance markers like (향정)(향)(마), standalone digits and digit sequences, unit tokens like T/정/캡슐/ml, quantity/day counts (e.g., '1 T 2 140 280 T'), prescription-end markers ('-- 이하여백 --', '이하여백'), clinical notes after the name (e.g., '용량 조절(titration)', '용량조절'), slash-separated values, and any trailing annotation.\n" +
                    "   - Example: '엘리퀴스정2.5mg 1 T 2 140 280 T' → '엘리퀴스정2.5mg'\n" +
                    "   - Example: '자나팜정0.25mg(향정) 1 T 140' → '자나팜정0.25mg'\n" +
                    "   - Example: '콩코르정2.5mg 용량 조절(titration) 0.5 T 1 140 70 T' → '콩코르정2.5mg'\n" +
                    "   - Example: '리피토정 10mg -- 이하여백 -- 1 T 1 140 140 T' → '리피토정10mg'\n" +
                    "   - Example: '크리맥액(돔페리돈) 1 3 2' → '크리맥액'\n" +
                    "2. 'originalName': Exact name as it appears in the prescription including any annotation.\n" +
                    "3. 'dosage': Amount per dose extracted from the prescription (e.g., '500mg', '1정'). " +
                    "If the drug name had a parenthetical like (1/정) or (2정), extract that as the dosage here.\n" +
                    "4. 'frequency': Dosing schedule from the prescription. Translate to %s (e.g., if Korean says '1일 3회 식후 30분', write the equivalent in %s).\n" +
                    "5. 'duration': Duration from the prescription. Translate to %s (e.g., if Korean says '7일분', write the equivalent in %s).\n" +
                    "6. 'translatedContent': 1–2 sentence plain explanation of this drug's purpose. Write in %s.\n" +
                    "7. 'sideEffects': Common side effects (2–3 items, concise). Use pharmacological knowledge. Write in %s.\n" +
                    "8. 'precautions': Key warnings/precautions (e.g., 'Do not drive', 'Avoid during pregnancy'). Use pharmacological knowledge. Write in %s.\n" +
                    "9. 'foodInteraction': Food/drink to avoid (e.g., 'Avoid alcohol', 'No grapefruit juice'). Use pharmacological knowledge. null if none. Write in %s.\n" +
                    "10. 'handwrittenNote': Any handwritten annotation detected near this drug (e.g., '①', '동그라미', '2정 추가'). null if none.\n\n" +
                    "Return ONLY a valid JSON array — no markdown fences, no extra text:\n" +
                    "[{\"drugName\":\"...\",\"originalName\":\"...\",\"dosage\":\"...\",\"frequency\":\"...\",\"duration\":\"...\",\"translatedContent\":\"...\",\"sideEffects\":\"...\",\"precautions\":\"...\",\"foodInteraction\":\"...\",\"handwrittenNote\":null}]\n\n" +
                    "Prescription OCR text:\n%s",
                    langName, langName, langName, langName, langName, langName, langName, langName, extractedText
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
            // 시스템 메시지: 다국어 입력 명시적 처리
            String systemMessage = "You are a professional medical triage assistant for 'CareLink'. " +
                    "The user's symptoms may be provided as Korean keywords, a Korean sentence, or text in ANY language (English, Japanese, Chinese, Vietnamese, Thai, Uzbek, etc.). " +
                    "Regardless of the input language, internally interpret all symptoms and provide the most appropriate Korean medical department. " +
                    "The 'reason' and 'doctorSummary' fields MUST always be written in Korean. " +
                    "The 'translatedMainDepartment', 'translatedReason', and 'translatedDepartmentName' fields must be written in the specified Target Language.";

            // 유저 메시지: 의사용 요약(doctorSummary)을 포함한 명확한 JSON 구조 요청
            String langName = toLanguageName(targetLanguage);
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
                    symptomInput, langName, langName, langName, langName
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
            String langName = toLanguageName(targetLanguage);

            String systemMessage = String.format("""
You are CareLink's professional, careful, and kind pharmacist assistant.

Your job is to answer the user's question using the prescription drug information provided below.
Drug information:
[%s]

Rules:
1. Answer in %s.
2. Be specific, practical, and easy for a patient to understand.
3. If the question is related to the listed medicines, answer as helpfully as possible based on the provided drug information.
4. If the provided drug information is not enough to fully answer, do NOT simply refuse.
   Instead:
   - first explain what can be answered from the available information,
   - then clearly say what information is missing,
   - then recommend checking with a pharmacist or doctor if needed.
5. If the user asks whether the medicines can be taken together, about side effects, timing, precautions, storage, missed doses, or how to take them, treat that as on-topic if it relates to the listed medicines.
6. Only say the question is off-topic when it is clearly unrelated to the listed medicines.
7. Never invent facts that are not supported by the provided drug information.
8. When appropriate, organize the answer with these sections:
   - Summary
   - Important points
   - When to be careful
9. Keep a warm and reassuring tone, but remain medically cautious.
10. If there is a safety concern, state it clearly and recommend professional medical advice.

Answer style:
- Prefer 3 to 6 sentences.
- Use bullet points when they improve readability.
- Avoid overly short refusals.
""", drugContext, langName);

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