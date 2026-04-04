package com.carelink.prescription.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PrescriptionChatDto {
    @Getter
    @NoArgsConstructor
    public static class Request {
        private String message; // 사용자 질문
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private String answer;  // AI 답변
    }
}