package com.carelink.recommendation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentRecommendRequest {

    @Schema(
            description = "Symptoms as either a free-text string or an array of symptom strings",
            example = "[\"headache\", \"fever\"]"
    )
    private Object symptoms;
}
