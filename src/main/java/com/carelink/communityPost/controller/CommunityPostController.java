package com.carelink.communityPost.controller;

import com.carelink.communityPost.entity.CommunityPostCategory;
import com.carelink.communityPost.entity.dto.CommunityPostCreateRequest;
import com.carelink.communityPost.entity.dto.CommunityPostResponse;
import com.carelink.communityPost.entity.dto.CommunityPostUpdateRequest;
import com.carelink.communityPost.service.CommunityPostService;
import com.carelink.global.annotation.CurrentUserId;
import com.carelink.global.response.ApiResponse;
import com.carelink.global.type.ResponseMessage;
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
            @PathVariable Long postId,
            @RequestParam String targetLanguage
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                communityPostService.getById(postId, targetLanguage)
        ));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CommunityPostResponse>>> getByTag(
            @RequestParam String tag,
            @RequestParam String targetLanguage
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                communityPostService.getByTag(tag, targetLanguage)
        ));
    }


    @GetMapping("/filter/language")
    public ResponseEntity<ApiResponse<List<CommunityPostResponse>>> getByLanguage(
            @RequestParam String language,
            @RequestParam String targetLanguage
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                communityPostService.getByLanguage(language, targetLanguage)
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
            @RequestParam String targetLanguage
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                communityPostService.getAllPosts(targetLanguage)
        ));
    }

    //제목 / 내용 검색
    @GetMapping("/search/keyword")
    public ResponseEntity<ApiResponse<List<CommunityPostResponse>>> searchByKeyword(
            @RequestParam String keyword,
            @RequestParam String targetLanguage
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                communityPostService.searchByKeyword(keyword, targetLanguage)
        ));
    }

    // 카테고리별 검색
    @GetMapping("/filter/category")
    public ResponseEntity<ApiResponse<List<CommunityPostResponse>>> getByCategory(
            @RequestParam CommunityPostCategory category,
            @RequestParam String targetLanguage
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                communityPostService.getByCategory(category, targetLanguage)
        ));
    }




}