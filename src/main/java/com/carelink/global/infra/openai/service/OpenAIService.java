package com.carelink.global.infra.openai.service;

import com.carelink.global.infra.openai.client.OpenAIClient;
import com.carelink.global.infra.openai.dto.ChatRequest;
import com.carelink.global.infra.openai.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 로그 확인용 추가
import org.springframework.cache.annotation.Cacheable; // 추가
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAIService {

    private final OpenAIClient openAIClient;

    /**
     * 특정 텍스트를 대상 언어로 번역합니다.
     * @Cacheable: Redis에서 먼저 값을 찾고, 없으면 메서드를 실행(GPT 호출)한 뒤 결과를 저장합니다.
     */
    @Cacheable(value = "translations", key = "#text + '_' + #targetLanguage", unless = "#result == 'Translation Failed'")
    public String translate(String text, String targetLanguage) {
        if (text == null || text.isBlank()) return text;

        // 캐시가 없을 때만 이 로그가 인텔리제이 콘솔에 찍힙니다.
        log.info("캐시 미존재 - GPT 번역 호출 중... (Language: {}, Text: {})", targetLanguage, text.substring(0, Math.min(text.length(), 10)));

        String systemMessage = "You are a professional medical translator for 'CareLink' app. " +
                "Translate the user's text naturally into the requested language.";

        String userMessage = String.format("Translate the following text to '%s' language: %s", targetLanguage, text);

        ChatRequest request = new ChatRequest(
                "gpt-4o-mini", // 가성비 모델 사용
                List.of(
                        new ChatRequest.Message("system", systemMessage),
                        new ChatRequest.Message("user", userMessage)
                )
        );

        ChatResponse response = openAIClient.sendChatRequest(request);

        if (response != null && !response.getChoices().isEmpty()) {
            return response.getChoices().get(0).getMessage().getContent().trim();
        }

        return "Translation Failed";
    }
}