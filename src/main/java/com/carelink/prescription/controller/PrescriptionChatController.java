package com.carelink.prescription.controller;

import com.carelink.global.annotation.CurrentUserId;
import com.carelink.prescription.dto.PrescriptionChatDto;
import com.carelink.prescription.dto.PrescriptionSummaryResponse;
import com.carelink.global.infra.openai.service.OpenAIService;
import com.carelink.prescription.service.PrescriptionService;
import com.carelink.prescriptionDrug.entity.PrescriptionDrugEntity;
import com.carelink.prescription.entity.PrescriptionEntity;
import com.carelink.prescriptionDrug.repository.PrescriptionDrugRepository;
import com.carelink.prescription.repository.PrescriptionRepository;
import com.carelink.user.entity.UserEntity;
import com.carelink.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionChatController {

    private final OpenAIService openAIService;
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionDrugRepository prescriptionDrugRepository;
    private final UserRepository userRepository;
    private final PrescriptionService prescriptionService; // 서비스 주입 필요

    /**
     * 1. 처방전 요약 정보 조회 (약 개수, 처방일)
     */
    @GetMapping("/{prescriptionId}/summary")
    public ResponseEntity<PrescriptionSummaryResponse> getSummary(@PathVariable Long prescriptionId) {
        PrescriptionEntity prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        List<PrescriptionDrugEntity> drugs = prescriptionDrugRepository.findByPrescription_PrescriptionId(prescriptionId);

        return ResponseEntity.ok(PrescriptionSummaryResponse.builder()
                .prescriptionId(prescriptionId)
                .totalDrugCount(drugs.size())
                .prescribedAt(prescription.getCreatedAt())
                .build());
    }

    /**
     * 2. 처방전 관련 챗봇 질문
     */
    @PostMapping("/{prescriptionId}/chat")
    public ResponseEntity<PrescriptionChatDto.Response> chat(
            @PathVariable Long prescriptionId,
            @CurrentUserId Long userId,
            @RequestBody PrescriptionChatDto.Request request) {

        // DB에서 약 정보 긁어와서 GPT에게 줄 맥락(Context) 만들기
        List<PrescriptionDrugEntity> drugs = prescriptionDrugRepository.findByPrescription_PrescriptionId(prescriptionId);
        String drugContext = drugs.stream()
                .map(d -> {
                    String name = d.getDrug() != null ? d.getDrug().getName() : d.getOriginalName();
                    return name + ": " + d.getTranslatedContent();
                })
                .collect(Collectors.joining(", "));

        // 유저 언어 설정 가져오기
        UserEntity user = userRepository.findById(userId).orElseThrow();

        // GPT 답변 생성
        String answer = openAIService.getPrescriptionChatAnswer(request.getMessage(), drugContext, user.getLanguage());

        return ResponseEntity.ok(new PrescriptionChatDto.Response(answer));
    }

    /**
     * 3. 사용자의 전체 처방전 이력 조회 (날짜, 약 개수 리스트)
     * 화면: 처방전 히스토리 목록
     */
    @GetMapping("/history")
    public ResponseEntity<List<PrescriptionSummaryResponse>> getHistory(@CurrentUserId Long userId) {
        List<PrescriptionSummaryResponse> history = prescriptionService.getAllPrescriptionSummaries(userId);
        return ResponseEntity.ok(history);
    }

    /**
     * 4. 사용자의 가장 최근 처방전 요약 조회
     * 화면: 메인 대시보드 상단
     */
    @GetMapping("/latest")
    public ResponseEntity<PrescriptionSummaryResponse> getLatest(@CurrentUserId Long userId) {
        PrescriptionSummaryResponse latest = prescriptionService.getLatestPrescriptionSummary(userId);
        return ResponseEntity.ok(latest);
    }
}