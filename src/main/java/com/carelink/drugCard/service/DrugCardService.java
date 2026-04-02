package com.carelink.drugCard.service;

import com.carelink.drugCard.dto.DrugCardDetailResponse;
import com.carelink.drugCard.dto.DrugCardListItemResponse;
import com.carelink.global.exception.RestApiException;
import com.carelink.global.infra.openai.service.OpenAIService;
import com.carelink.global.type.ErrorCode;
import com.carelink.prescription.entity.PrescriptionEntity;
import com.carelink.prescription.repository.PrescriptionRepository;
import com.carelink.prescriptionDrug.entity.PrescriptionDrugEntity;
import com.carelink.prescriptionDrug.repository.PrescriptionDrugRepository;
import com.carelink.user.entity.UserEntity;
import com.carelink.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DrugCardService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionDrugRepository prescriptionDrugRepository;
    private final UserRepository userRepository;
    private final OpenAIService openAIService;

    public List<DrugCardListItemResponse> getDrugCards(Long userId, Long prescriptionId) {
        validateOwnedPrescription(userId, prescriptionId);

        return prescriptionDrugRepository.findAllByPrescriptionIdWithDrug(prescriptionId)
                .stream()
                .map(DrugCardListItemResponse::from)
                .toList();
    }

    public DrugCardDetailResponse getDrugCard(Long userId, Long prescriptionId, String prescriptionDrugId, boolean translate) {
        // 1. 湲곕낯 議고쉶 諛?寃利?
        PrescriptionDrugEntity prescriptionDrug = prescriptionDrugRepository.findByIdWithDrugAndPrescription(prescriptionDrugId)
                .orElseThrow(() -> new RestApiException(ErrorCode.DRUG_CARD_NOT_FOUND));

        if (!prescriptionDrug.getPrescription().getPrescriptionId().equals(prescriptionId)) {
            throw new RestApiException(ErrorCode.DRUG_CARD_NOT_FOUND);
        }

        if (!prescriptionDrug.getPrescription().getUser().getUserId().equals(userId)) {
            throw new RestApiException(ErrorCode.FORBIDDEN);
        }

        // 2. 湲곕낯 ?쒓뎅???곗씠??湲곕컲 Response ?앹꽦
        DrugCardDetailResponse response = DrugCardDetailResponse.from(prescriptionDrug);

        // 3. 踰덉뿭 踰꾪듉???뚮????뚮쭔(translate=true) GPT ?몄텧
        if (translate) {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));

            String targetLang = user.getLanguage();

            try {
                // 鍮꾩슜 諛쒖깮 吏?? ?꾩슂???꾨뱶留?踰덉뿭
                String tEfficacy = openAIService.translate(response.getEfficacy(), targetLang);
                String tCaution = openAIService.translate(response.getCaution(), targetLang);
                String tUseMethod = openAIService.translate(response.getUseMethod(), targetLang);

                response.updateTranslations(tEfficacy, tCaution, tUseMethod);
            } catch (Exception e) {
                log.error("GPT 踰덉뿭 ?ㅽ뙣: {}. ?먮낯 ?곗씠?곕? 諛섑솚?⑸땲??", e.getMessage());
            }
        }

        return response;
    }

    private void validateOwnedPrescription(Long userId, Long prescriptionId) {
        PrescriptionEntity prescription = prescriptionRepository.findByPrescriptionId(prescriptionId)
                .orElseThrow(() -> new RestApiException(ErrorCode.PRESCRIPTION_NOT_FOUND));

        if (!prescription.getUser().getUserId().equals(userId)) {
            throw new RestApiException(ErrorCode.FORBIDDEN);
        }
    }
}

