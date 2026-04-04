package com.carelink.communityPost.controller;

import com.carelink.communityPost.entity.CommunityPostCategory;
import com.carelink.communityPost.entity.dto.CommunityPostCreateRequest;
import com.carelink.communityPost.entity.dto.CommunityPostResponse;
import com.carelink.communityPost.entity.dto.CommunityPostUpdateRequest;
import com.carelink.communityPost.service.CommunityPostService;
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
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
public class CommunityPostController {

    private final CommunityPostService communityPostService;
    private final UserRepository userRepository;

    private String getUserLanguage(Long userId) {
        return userRepository.findById(userId)
                .map(UserEntity::getLanguage)
                .orElse("ko");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CommunityPostResponse>> create(
            @CurrentUserId Long userId,
            @RequestBody @Valid CommunityPostCreateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                communityPostService.create(userId, request)
        ));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<CommunityPostResponse>> getById(
            @CurrentUserId Long userId,
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                communityPostService.getById(postId, getUserLanguage(userId))
        ));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CommunityPostResponse>>> getByTag(
            @CurrentUserId Long userId,
            @RequestParam String tag
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                communityPostService.getByTag(tag, getUserLanguage(userId))
        ));
    }


    @GetMapping("/filter/language")
    public ResponseEntity<ApiResponse<List<CommunityPostResponse>>> getByLanguage(
            @CurrentUserId Long userId,
            @RequestParam String language
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                communityPostService.getByLanguage(language, getUserLanguage(userId))
        ));
    }


    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<CommunityPostResponse>> update(
            @CurrentUserId Long userId,
            @PathVariable Long postId,
            @RequestBody @Valid CommunityPostUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                communityPostService.update(userId, postId, request)
        ));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @CurrentUserId Long userId,
            @PathVariable Long postId
    ) {
        communityPostService.delete(userId, postId);
        return ResponseEntity.ok(ApiResponse.ok(ResponseMessage.POST_DELETE_SUCCESS));
    }

    @GetMapping("list")
    public ResponseEntity<ApiResponse<List<CommunityPostResponse>>> getAllPosts(
            @CurrentUserId Long userId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                communityPostService.getAllPosts(getUserLanguage(userId))
        ));
    }

    //제목 / 내용 검색
    @GetMapping("/search/keyword")
    public ResponseEntity<ApiResponse<List<CommunityPostResponse>>> searchByKeyword(
            @CurrentUserId Long userId,
            @RequestParam String keyword
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                communityPostService.searchByKeyword(keyword, getUserLanguage(userId))
        ));
    }

    // 카테고리별 검색
    @GetMapping("/filter/category")
    public ResponseEntity<ApiResponse<List<CommunityPostResponse>>> getByCategory(
            @CurrentUserId Long userId,
            @RequestParam CommunityPostCategory category
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                communityPostService.getByCategory(category, getUserLanguage(userId))
        ));
    }




}