package com.carelink.global.infra.upstage.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Component
@RequiredArgsConstructor
public class UpstageOcrClient {

    @Value("${upstage.api-key}")
    private String apiKey;

    @Value("${upstage.ocr-url}")
    private String ocrUrl;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 이미지 파일을 Upstage Document Parse API에 전송하여 텍스트를 추출합니다.
     */
    public String extractText(byte[] fileBytes, String fileName) throws Exception {

        ByteArrayResource fileResource = new ByteArrayResource(fileBytes) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("document", fileResource);
        body.add("model", "document-parse");
        body.add("ocr", "force"); // 손글씨·필기 주석 포함 전체 OCR 강제 적용

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(apiKey);

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

        // 원본 응답을 String으로 받아 구조 파악
        ResponseEntity<String> response = restTemplate.exchange(
                ocrUrl, HttpMethod.POST, entity, String.class
        );

        String rawBody = response.getBody();
        log.info("=== Upstage OCR 원본 응답 ===\n{}", rawBody);

        if (rawBody == null || rawBody.isBlank()) {
            throw new RuntimeException("Upstage OCR 응답이 비어 있습니다.");
        }

        JsonNode root = objectMapper.readTree(rawBody);
        JsonNode contentNode = root.path("content");

        // text → markdown → html(태그 제거) 순으로 시도
        String text = contentNode.path("text").asText("").trim();

        if (text.isBlank()) {
            text = contentNode.path("markdown").asText("").trim();
        }

        if (text.isBlank()) {
            String html = contentNode.path("html").asText("").trim();
            text = html.replaceAll("<[^>]+>", " ").replaceAll("\\s+", " ").trim();
        }

        log.info("=== Upstage OCR 추출 텍스트 ===\n{}", text);
        return text;
    }
}
