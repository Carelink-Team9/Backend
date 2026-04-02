package com.carelink.global.infra.openai.client;

import com.carelink.global.infra.openai.dto.ChatRequest;
import com.carelink.global.infra.openai.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class OpenAIClient {

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model:gpt-4o-mini}")
    private String model;

    private final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final RestTemplate restTemplate = new RestTemplate();

    public ChatResponse sendChatRequest(ChatRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);

        return restTemplate.postForObject(API_URL, entity, ChatResponse.class);
    }
}