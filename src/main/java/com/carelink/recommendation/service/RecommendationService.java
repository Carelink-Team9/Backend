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

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final OpenAIService openAIService;
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;

    @Transactional
    public DepartmentRecommendResponse getAndSaveRecommendation(Long userId, List<String> symptoms) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // AI 호출 (OpenAIService의 수정된 메서드 호출)
        DepartmentRecommendResponse response = openAIService.recommendDepartment(symptoms, user.getLanguage());

        // 이력 저장 (두 언어 모두 기록)
        RecommendationEntity history = RecommendationEntity.builder()
                .user(user)
                .symptoms(String.join(", ", symptoms))
                .recommendedDeptKo(response.getMainDepartment()) // 한국어
                .recommendedDeptTr(response.getTranslatedMainDepartment()) // 번역본
                .confidence(response.getMainConfidence())
                .build();

        recommendationRepository.save(history);
        return response;
    }
}