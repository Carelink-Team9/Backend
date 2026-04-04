package com.carelink.hospital.controller;

import com.carelink.global.response.ApiResponse;
import com.carelink.hospital.dto.HospitalNearbyResponse;
import com.carelink.hospital.service.HospitalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
@Tag(name = "Hospital", description = "병원 검색 API")
public class HospitalController {

    private final HospitalService hospitalService;

    /**
     * 현재 위치 기준 가까운 병원 목록 반환
     *
     * @param lat      위도 (예: 37.5665)
     * @param lng      경도 (예: 126.9780)
     * @param radius   반경 km (기본값: 5.0)
     * @param limit    최대 결과 수 (기본값: 20)
     */
    @Operation(summary = "주변 병원 조회")
    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<HospitalNearbyResponse>>> getNearbyHospitals(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5.0") double radius,
            @RequestParam(defaultValue = "20") int limit
    ) {
        List<HospitalNearbyResponse> result = hospitalService.findNearby(lat, lng, radius, limit);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
