package com.carelink.comment.entity.dto;

import lombok.Getter;
import lombok.Builder;
import java.time.LocalDateTime;
import com.carelink.comment.entity.CommentEntity;


@Getter
@Builder
public class CommentResponse {

    private Long commentId;
    private Long userId;
    private Long postId;
    private String content;
    private String language;
    private LocalDateTime createdAt;

    public static CommentResponse from(CommentEntity entity, String content) {
        return CommentResponse.builder()
                .commentId(entity.getCommentId())
                .userId(entity.getUser().getUserId())
                .postId(entity.getCommunityPost().getCommunityPostId())
                .content(content)
                .language(entity.getLanguage())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}