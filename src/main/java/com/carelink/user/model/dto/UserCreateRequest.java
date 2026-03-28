package com.carelink.user.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

// 사용자 최초 등록 요청 DTO
@Getter
public class UserCreateRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 25)
    private String nationality;

    @NotBlank
    @Size(max = 10)
    private String language;
}
