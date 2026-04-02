package com.carelink.drugCard.service;

import com.carelink.drugCard.dto.DrugCardDetailResponse;
import com.carelink.drugCard.dto.DrugCardListItemResponse;
import com.carelink.global.exception.RestApiException;
import com.carelink.global.type.ErrorCode;
import com.carelink.prescription.entity.PrescriptionEntity;
import com.carelink.prescription.repository.PrescriptionRepository;
import com.carelink.prescriptionDrug.repository.PrescriptionDrugRepository;
import com.carelink.prescriptionDrug.entity.PrescriptionDrugEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DrugCardService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionDrugRepository prescriptionDrugRepository;

    public List<DrugCardListItemResponse> getDrugCards(Long userId, Long prescriptionId) {
        validateOwnedPrescription(userId, prescriptionId);

        return prescriptionDrugRepository.findAllByPrescriptionIdWithDrug(prescriptionId)
                .stream()
                .map(DrugCardListItemResponse::from)
                .toList();
    }

    public DrugCardDetailResponse getDrugCard(Long userId, Long prescriptionId, String prescriptionDrugId) {
        PrescriptionDrugEntity prescriptionDrug = prescriptionDrugRepository.findByIdWithDrugAndPrescription(prescriptionDrugId)
                .orElseThrow(() -> new RestApiException(ErrorCode.DRUG_CARD_NOT_FOUND));

        if (!prescriptionDrug.getPrescription().getPrescriptionId().equals(prescriptionId)) {
            throw new RestApiException(ErrorCode.DRUG_CARD_NOT_FOUND);
        }

        if (!prescriptionDrug.getPrescription().getUser().getUserId().equals(userId)) {
            throw new RestApiException(ErrorCode.FORBIDDEN);
        }

        return DrugCardDetailResponse.from(prescriptionDrug);
    }

    private void validateOwnedPrescription(Long userId, Long prescriptionId) {
        PrescriptionEntity prescription = prescriptionRepository.findByPrescriptionId(prescriptionId)
                .orElseThrow(() -> new RestApiException(ErrorCode.PRESCRIPTION_NOT_FOUND));

        if (!prescription.getUser().getUserId().equals(userId)) {
            throw new RestApiException(ErrorCode.FORBIDDEN);
        }
    }
}
