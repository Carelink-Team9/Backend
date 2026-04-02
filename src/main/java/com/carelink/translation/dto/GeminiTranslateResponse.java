package com.carelink.translation.dto;

import java.util.List;

public record GeminiTranslateResponse(
        List<Candidate> candidates
) {
    public record Candidate(Content content) {}
    public record Content(List<Part> parts) {}
    public record Part(String text) {}
}
