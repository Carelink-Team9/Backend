package com.carelink.hospital.repository;

import java.math.BigDecimal;

public interface HospitalDistanceProjection {
    Long getHospitalId();
    String getName();
    String getAddress();
    String getDepartment();
    String getPhone();
    BigDecimal getLatitude();
    BigDecimal getLongitude();
    String getSidoNm();
    String getSgguNm();
    String getHomepage();
    Double getDistance();
}
