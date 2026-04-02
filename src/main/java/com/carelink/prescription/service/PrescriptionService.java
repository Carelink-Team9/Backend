package com.carelink.prescription.service;

import com.carelink.drug.entity.DrugEntity;
import com.carelink.drug.repository.DrugRepository;
import com.carelink.prescription.entity.PrescriptionEntity;
import com.carelink.prescription.repository.PrescriptionRepository;
import com.carelink.prescriptionDrug.entity.PrescriptionDrugEntity;
import com.carelink.prescriptionDrug.repository.PrescriptionDrugRepository;
import com.carelink.prescriptionImage.entity.PrescriptionImageEntity;
import com.carelink.prescriptionImage.repository.PrescriptionImageRepository;
import com.carelink.user.entity.UserEntity;
import com.carelink.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionImageRepository prescriptionImageRepository;
    private final PrescriptionDrugRepository prescriptionDrugRepository;
    private final UserRepository userRepository;
    private final DrugRepository drugRepository; // 약 데이터 조회를 위해 필요

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

        // 3. Prescription 엔티티 생성
        PrescriptionEntity prescription = PrescriptionEntity.builder()
                .user(user)
                .build();
        prescriptionRepository.save(prescription);

        // 4. PrescriptionImage 엔티티 생성 (URL 저장)
        String imageUrl = "/uploads/prescriptions/" + fileName;
        PrescriptionImageEntity imageEntity = PrescriptionImageEntity.builder()
                .prescription(prescription)
                .imageUrl(imageUrl)
                .build();
        prescriptionImageRepository.save(imageEntity);

        // 5. [임시] GPT Vision 파싱 결과 Mock 데이터 (나중에 OpenAIService 호출로 교체)
        // 실제로는 Vision이 "201502206"(item_seq) 또는 약 이름을 줍니다.
        createMockPrescriptionDrugs(prescription);

        return prescription.getPrescriptionId();
    }

    private void createMockPrescriptionDrugs(PrescriptionEntity prescription) {
        // 테스트용: DB에 존재하는 약 하나를 조회해서 매핑 (item_seq '201502206' 가정)
        drugRepository.findByItemSeq("201502206").ifPresent(drug -> {
            PrescriptionDrugEntity drugEntity = PrescriptionDrugEntity.builder()
                    .prescription(prescription)
                    .drug(drug)
                    .dosage("1정")
                    .frequency("1일 3회")
                    .duration("3일")
                    .build();
            prescriptionDrugRepository.save(drugEntity);
        });
    }
}