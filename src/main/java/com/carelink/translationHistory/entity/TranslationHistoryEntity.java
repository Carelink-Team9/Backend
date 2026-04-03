package com.carelink.translationHistory.entity;

import com.carelink.prescription.entity.PrescriptionEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "translation_history")
@EntityListeners(AuditingEntityListener.class)
public class TranslationHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "translation_history_id")
    private Long translationHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id")
    private PrescriptionEntity prescription;

    @Column(name = "language_code")
    private String languageCode;

    @Column(name = "source_content", columnDefinition = "TEXT")
    private String sourceContent;

    @Column(name = "translated_content", columnDefinition = "json")
    private String translatedContent;

    @Enumerated(EnumType.STRING)
    private com.carelink.translationHistory.entity.TranslationStatus status;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
