package com.carelink.drug.entity;

import com.carelink.drugHistory.entity.DrugHistoryEntity;
import com.carelink.prescriptiondrug.entity.PrescriptionDrugEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "drug")
public class DrugEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "drug_id")
    private Long drugId;

    @Column(name = "item_seq", unique = true, nullable = false, length = 50)
    private String itemSeq;

    @Column(columnDefinition = "TEXT")
    private String name;

    //상호작용
    @Column(name = "intrc_qesitm", columnDefinition = "TEXT")
    private String intrcQesitm;

    //부작용
    @Column(name = "se_qesitm", columnDefinition = "TEXT")
    private String seQesitm;

    @Column(columnDefinition = "TEXT")
    private String efficacy;

    @Column(columnDefinition = "TEXT")
    private String caution;

    @Column(name = "use_method", columnDefinition = "TEXT")
    private String useMethod;

    @Column(name = "opened_at")
    private LocalDateTime openedAt;
}
