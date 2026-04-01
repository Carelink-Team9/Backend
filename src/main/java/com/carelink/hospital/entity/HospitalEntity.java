package com.carelink.hospital.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "hospital")
public class HospitalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hospital_id")
    private Long hospitalId;

    private String name;

    private String address;

    private String department;

    private String phone;

    private BigDecimal latitude;

    private BigDecimal longitude;

    @Column(name = "sido_nm")
    private String sidoNm;

    @Column(name = "sggu_nm")
    private String sgguNm;

    private String homePage;
}
