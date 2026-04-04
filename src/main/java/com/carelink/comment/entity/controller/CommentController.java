package com.carelink.comment.entity.controller;

import com.carelink.comment.entity.dto.CommentCreateRequest;
import com.carelink.comment.entity.dto.CommentResponse;
import com.carelink.comment.entity.dto.CommentUpdateRequest;
import com.carelink.comment.entity.service.CommentService;
import com.carelink.global.annotation.CurrentUserId;
import com.carelink.global.response.ApiResponse;
import com.carelink.global.type.ResponseMessage;
import com.carelink.user.entity.UserEntity;
import com.carelink.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserRepository userRepository;

    private String getUserLanguage(Long userId) {
        return userRepository.findById(userId)
                .map(UserEntity::getLanguage)
                .orElse("ko");
    }

    @PostMapping("/api/community/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> create(
            @CurrentUserId Long userId,
            @PathVariable Long postId,
            @RequestBody @Valid CommentCreateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                commentService.create(userId, postId, request)
        ));
    }

    @GetMapping("/api/community/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getByPostId(
            @CurrentUserId Long userId,
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                commentService.getByPostId(postId, getUserLanguage(userId))
        ));
    }


    @PutMapping("/api/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> update(
            @CurrentUserId Long userId,
            @PathVariable Long commentId,
            @RequestBody @Valid CommentUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                commentService.update(userId, commentId, request)
        ));
    }

    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @CurrentUserId Long userId,
            @PathVariable Long commentId
    ) {
        commentService.delete(userId, commentId);
        return ResponseEntity.ok(ApiResponse.ok(ResponseMessage.COMMENT_DELETE_SUCCESS));
    }

}