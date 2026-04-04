package com.carelink.recommendation.entity;

import com.carelink.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recommendation")
@EntityListeners(AuditingEntityListener.class)
public class RecommendationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommendation_id")
    private Long recommendationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "symptoms", columnDefinition = "TEXT")
    private String symptoms; // 이제 "배가 아파요" 같은 문장 데이터가 들어옵니다.

    @Column(name = "recommended_dept_ko")
    private String recommendedDeptKo; // 추천 진료과 (한국어)

    @Column(name = "recommended_dept_tr")
    private String recommendedDeptTr; // 추천 진료과 (번역본)

    @Column(name = "doctor_summary", columnDefinition = "TEXT")
    private String doctorSummary; // [추가] 의사에게 전달할 한국어 증상 요약 리포트

    @Column(name = "confidence")
    private Integer confidence;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}