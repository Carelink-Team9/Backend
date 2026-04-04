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
    private String symptoms; // 입력받은 증상 리스트 (JSON이나 콤마 구분자)

    @Column(name = "recommended_dept")
    private String recommendedDept;

    @Column(name = "confidence")
    private Integer confidence;

    @Column(name = "recommended_dept_ko")
    private String recommendedDeptKo;

    @Column(name = "recommended_dept_tr")
    private String recommendedDeptTr;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}