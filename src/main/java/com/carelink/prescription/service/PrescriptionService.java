package com.carelink.prescription.service;

import com.carelink.drug.entity.DrugEntity;
import com.carelink.drug.repository.DrugRepository;
import com.carelink.prescription.dto.PrescriptionResponse;
import com.carelink.prescription.entity.PrescriptionEntity;
import com.carelink.prescription.repository.PrescriptionRepository;
import com.carelink.prescriptionDrug.entity.PrescriptionDrugEntity;
import com.carelink.prescriptionDrug.repository.PrescriptionDrugRepository;
import com.carelink.prescriptionImage.entity.PrescriptionImageEntity;
import com.carelink.prescriptionImage.repository.PrescriptionImageRepository;
import com.carelink.translationHistory.entity.TranslationHistoryEntity;
import com.carelink.translationHistory.repository.TranslationHistoryRepository;
import com.carelink.translationHistory.entity.TranslationStatus;
import com.carelink.global.infra.openai.service.OpenAIService;
import com.carelink.user.entity.UserEntity;
import com.carelink.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionImageRepository prescriptionImageRepository;
    private final PrescriptionDrugRepository prescriptionDrugRepository;
    private final TranslationHistoryRepository translationHistoryRepository;
    private final UserRepository userRepository;
    private final DrugRepository drugRepository;
    private final OpenAIService openAIService;
    private final ObjectMapper objectMapper; // JSON 변환용

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public Long uploadAndProcessPrescription(Long userId, MultipartFile file) {
        // 1. 유저 확인
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. 파일 물리적 저장
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path targetLocation = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(fileName);

        try {
            Files.createDirectories(targetLocation.getParent());
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new RuntimeException("파일 저장 실패", ex);
        }

        // 3. 엔티티 생성
        PrescriptionEntity prescription = prescriptionRepository.save(
                PrescriptionEntity.builder().user(user).build()
        );

        String imageUrl = "/uploads/prescriptions/" + fileName;
        prescriptionImageRepository.save(PrescriptionImageEntity.builder()
                .prescription(prescription)
                .imageUrl(imageUrl)
                .build());

        // 4. GPT Vision 분석 호출 (사용자의 모국어 설정 반영)
        List<OpenAIService.ParsedDrug> parsedDrugs = openAIService.parsePrescriptionImage(imageUrl, user.getLanguage());
        log.info("=== GPT 파싱 결과 ({} 개) ===", parsedDrugs.size());
        for (OpenAIService.ParsedDrug p : parsedDrugs) {
            log.info("  drugName={}, originalName={}, dosage={}, frequency={}, duration={}, translatedContent={}",
                    p.drugName(), p.originalName(), p.dosage(), p.frequency(), p.duration(), p.translatedContent());
        }

        // 5. TranslationHistory에 분석 원문(JSON) 저장
        saveTranslationHistory(prescription, user.getLanguage(), parsedDrugs);

        // 6. 분석 결과를 DB 약 데이터와 매핑하여 저장
        // DB에 없는 약도 저장 (drug = null)
        for (OpenAIService.ParsedDrug parsed : parsedDrugs) {
            DrugEntity matchedDrug = drugRepository.findByNameContaining(parsed.drugName())
                    .stream().findFirst().orElse(null);
            prescriptionDrugRepository.save(PrescriptionDrugEntity.builder()
                    .prescription(prescription)
                    .drug(matchedDrug)
                    .originalName(parsed.originalName())
                    .dosage(parsed.dosage())
                    .frequency(parsed.frequency())
                    .duration(parsed.duration())
                    .translatedContent(parsed.translatedContent())
                    .sideEffects(parsed.sideEffects())
                    .precautions(parsed.precautions())
                    .foodInteraction(parsed.foodInteraction())
                    .handwrittenNote(parsed.handwrittenNote())
                    .build());
        }

        return prescription.getPrescriptionId();
    }

    @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionDetails(Long userId, Long prescriptionId) {
        PrescriptionEntity prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        if (!prescription.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Access Denied");
        }

        List<PrescriptionResponse.DrugDetailDto> drugs = prescriptionDrugRepository.findByPrescription(prescription)
                .stream()
                .map(pd -> new PrescriptionResponse.DrugDetailDto(
                        pd.getDrug() != null ? pd.getDrug().getName() : pd.getOriginalName(),
                        pd.getOriginalName(),
                        pd.getDosage(),
                        pd.getFrequency(),
                        pd.getDuration(),
                        pd.getTranslatedContent(),
                        pd.getSideEffects(),
                        pd.getPrecautions(),
                        pd.getFoodInteraction(),
                        pd.getHandwrittenNote()
                )).toList();

        String imageUrl = prescriptionImageRepository.findByPrescription(prescription)
                .stream().findFirst()
                .map(PrescriptionImageEntity::getImageUrl)
                .orElse(null);

        log.info("=== GET /prescriptions/{} 응답 ({} 개 약) ===", prescriptionId, drugs.size());
        for (PrescriptionResponse.DrugDetailDto d : drugs) {
            log.info("  drugName={}, originalName={}, dosage={}, frequency={}, duration={}, translatedContent={}",
                    d.getDrugName(), d.getOriginalName(), d.getDosage(), d.getFrequency(), d.getDuration(), d.getTranslatedContent());
        }
        return new PrescriptionResponse(prescriptionId, prescription.getCreatedAt(), imageUrl, drugs);
    }

    private void saveTranslationHistory(PrescriptionEntity prescription, String lang, List<OpenAIService.ParsedDrug> data) {
        try {
            String jsonContent = objectMapper.writeValueAsString(data);
            translationHistoryRepository.save(TranslationHistoryEntity.builder()
                    .prescription(prescription)
                    .languageCode(lang)
                    .sourceContent("AI Prescription Analysis")
                    .translatedContent(jsonContent)
                    .status(TranslationStatus.SUCCESS)
                    .build());
        } catch (Exception e) {
            // 히스토리 저장 실패가 핵심 로직을 방해하지 않도록 처리
        }
    }
}