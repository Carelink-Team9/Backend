package com.carelink.translation.service;

import com.carelink.translation.client.TranslationClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
//TranslationService 구현체로 번역 필요한지 판단
public class GeminiTranslationService implements TranslationService {

    //API 호출은 Client에서
    private final TranslationClient translationClient;

    @Override
    public String translate(String content, String sourceLanguage, String targetLanguage) {
        if (sourceLanguage.equals(targetLanguage)) {
            return content;
        }

        return translationClient.translate(content, sourceLanguage, targetLanguage);
    }
}
