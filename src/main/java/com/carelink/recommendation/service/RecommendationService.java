package com.carelink.recommendation.service;

import com.carelink.global.infra.openai.service.OpenAIService;
import com.carelink.recommendation.dto.DepartmentRecommendResponse;
import com.carelink.recommendation.entity.RecommendationEntity;
import com.carelink.recommendation.repository.RecommendationRepository;
import com.carelink.user.entity.UserEntity;
import com.carelink.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final OpenAIService openAIService;
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;

    @Transactional
    public DepartmentRecommendResponse getAndSaveRecommendation(Long userId, String symptomInput) { // <--- 여기가 반드시 String이어야 합니다!
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. AI 추천 받기 (OpenAIService도 String을 받도록 수정되어 있어야 함)
        DepartmentRecommendResponse response = openAIService.recommendDepartment(symptomInput, user.getLanguage());

        // 2. 이력 저장 (Entity에 doctorSummary 필드가 있어야 함)
        RecommendationEntity history = RecommendationEntity.builder()
                .user(user)
                .symptoms(symptomInput)
                .recommendedDeptKo(response.getMainDepartment())
                .recommendedDeptTr(response.getTranslatedMainDepartment())
                .doctorSummary(response.getDoctorSummary()) // Entity 필드 확인!
                .confidence(response.getMainConfidence())
                .build();

        recommendationRepository.save(history);

        return response;
    }
}