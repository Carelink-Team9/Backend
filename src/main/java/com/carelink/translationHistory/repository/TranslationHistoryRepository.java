package com.carelink.translationHistory.repository;

import com.carelink.prescription.entity.PrescriptionEntity;
import com.carelink.translationHistory.entity.TranslationHistoryEntity;
import com.carelink.translationHistory.entity.TranslationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TranslationHistoryRepository extends JpaRepository<TranslationHistoryEntity, Long> {
    Optional<TranslationHistoryEntity> findByTranslationHistoryId(Long translationHistoryId);
    List<TranslationHistoryEntity> findByPrescription(PrescriptionEntity prescription);
    List<TranslationHistoryEntity> findByPrescription_PrescriptionId(Long prescriptionId);
    List<TranslationHistoryEntity> findByStatus(TranslationStatus status);
}
