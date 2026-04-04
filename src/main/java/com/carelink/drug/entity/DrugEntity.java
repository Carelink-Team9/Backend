package com.carelink.drug.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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

    // 1. item_seq (품목기준코드)
    @Column(name = "item_seq", unique = true, nullable = false)
    private String itemSeq;

    // 2. name (약 이름)
    @Column(columnDefinition = "TEXT")
    private String name;

    // 3. efficacy (효능)
    @Column(columnDefinition = "TEXT")
    private String efficacy;

    // 4. caution (주의사항)
    @Column(columnDefinition = "TEXT")
    private String caution;

    // 5. use_method (사용방법)
    @Column(name = "use_method", columnDefinition = "TEXT")
    private String useMethod;

    // 6. intrc_qesitm (상호작용)
    @Column(name = "intrc_qesitm", columnDefinition = "TEXT")
    private String intrcQesitm;

    // 7. se_qesitm (부작용)
    @Column(name = "se_qesitm", columnDefinition = "TEXT")
    private String seQesitm;

    // 8. opened_at (공개일자)
    @Column(name = "opened_at")
    private LocalDateTime openedAt;
}