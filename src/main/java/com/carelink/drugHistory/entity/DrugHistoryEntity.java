package com.carelink.drugHistory.entity;

import com.carelink.drug.entity.DrugEntity;
import com.carelink.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "drug_history")
public class DrugHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "drug_history_id")
    private Long drugHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id")
    private DrugEntity drug;

    @Column(name = "taken_at")
    private LocalDateTime takenAt;

    private String note;
}
