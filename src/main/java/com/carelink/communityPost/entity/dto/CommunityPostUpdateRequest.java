package com.carelink.communityPost.entity.dto;

import com.carelink.communityPost.entity.CommunityPostCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CommunityPostUpdateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    private String language;

    private String tag;

    @NotNull
    private CommunityPostCategory category;
}
