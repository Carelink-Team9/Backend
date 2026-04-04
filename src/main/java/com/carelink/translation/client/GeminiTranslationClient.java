package com.carelink.translation.client;

import com.carelink.global.exception.RestApiException;
import com.carelink.global.type.ErrorCode;
import com.carelink.translation.dto.GeminiTranslateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GeminiTranslationClient implements TranslationClient {

    private final RestClient.Builder restClientBuilder;

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    @Value("${gemini.base-url}")
    private String baseUrl;

    @Override
    public String translate(String content, String sourceLanguage, String targetLanguage) {
        // Gemini에 보낼 프롬포트 예를 들어서 한국어에서 영어로 번역해서 번역문만 줘
        String prompt = """
                Translate the following text from %s to %s.
                Return only the translated text without explanation.

                Text:
                %s
                """.formatted(sourceLanguage, targetLanguage, content);

        // Gemini API 요청 body 생성
        // Gemini가 요구하는 JSON 구조에 맞춤
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        GeminiTranslateResponse response = restClientBuilder.build()
                .post()
                .uri(baseUrl + "/v1beta/models/" + model + ":generateContent?key=" + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(GeminiTranslateResponse.class);

        if (response == null
                || response.candidates() == null
                || response.candidates().isEmpty()
                || response.candidates().get(0).content() == null
                || response.candidates().get(0).content().parts() == null
                || response.candidates().get(0).content().parts().isEmpty()) {
            throw new RestApiException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return response.candidates().get(0).content().parts().get(0).text();
    }

    @Override
    public String[] translatePair(String title, String content, String sourceLanguage, String targetLanguage) {
        String prompt = """
                Translate the following title and content from %s to %s.
                Return ONLY the two translated texts separated by exactly "---SPLIT---" on its own line. No other output.

                Title: %s
                Content: %s
                """.formatted(sourceLanguage, targetLanguage, title, content);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

        GeminiTranslateResponse response = restClientBuilder.build()
                .post()
                .uri(baseUrl + "/v1beta/models/" + model + ":generateContent?key=" + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(GeminiTranslateResponse.class);

        if (response == null
                || response.candidates() == null
                || response.candidates().isEmpty()
                || response.candidates().get(0).content() == null
                || response.candidates().get(0).content().parts() == null
                || response.candidates().get(0).content().parts().isEmpty()) {
            return new String[]{title, content};
        }

        String result = response.candidates().get(0).content().parts().get(0).text();
        String[] parts = result.split("---SPLIT---", 2);
        if (parts.length == 2) {
            return new String[]{parts[0].trim(), parts[1].trim()};
        }
        return new String[]{result.trim(), content};
    }
}