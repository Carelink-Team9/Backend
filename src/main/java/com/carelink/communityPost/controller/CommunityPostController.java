package com.carelink.communityPost.controller;

import com.carelink.communityPost.entity.CommunityPostCategory;
import com.carelink.communityPost.entity.dto.CommunityPostCreateRequest;
import com.carelink.communityPost.entity.dto.CommunityPostResponse;
import com.carelink.communityPost.entity.dto.CommunityPostUpdateRequest;
import com.carelink.communityPost.service.CommunityPostService;
import com.carelink.global.annotation.CurrentUserId;
import com.carelink.global.response.ApiResponse;
import com.carelink.global.type.ResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
@Tag(name = "Community Post", description = "커뮤니티 게시글 API")
public class CommunityPostController {

    private final CommunityPostService communityPostService;

    @Operation(summary = "게시글 작성")
    @SecurityRequirement(name = "sessionCookieAuth")
    @PostMapping
    public ResponseEntity<ApiResponse<CommunityPostResponse>> create(
            @CurrentUserId Long userId,
            @RequestBody @Valid CommunityPostCreateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                communityPostService.create(userId, request)
        ));
    }

    @Operation(summary = "게시글 단건 조회")
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<CommunityPostResponse>> getById(
            @PathVariable Long postId,
            @RequestParam String targetLanguage
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                communityPostService.getById(postId, targetLanguage)
        ));
    }

    @Operation(summary = "태그로 게시글 검색")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CommunityPostResponse>>> getByTag(
            @RequestParam String tag,
            @RequestParam String targetLanguage
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                communityPostService.getByTag(tag, targetLanguage)
        ));
    }


    @Operation(summary = "언어별 게시글 조회")
    @GetMapping("/filter/language")
    public ResponseEntity<ApiResponse<List<CommunityPostResponse>>> getByLanguage(
            @RequestParam String language,
            @RequestParam String targetLanguage
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                communityPostService.getByLanguage(language, targetLanguage)
        ));
    }


    @Operation(summary = "게시글 수정")
    @SecurityRequirement(name = "sessionCookieAuth")
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

    @Operation(summary = "게시글 삭제")
    @SecurityRequirement(name = "sessionCookieAuth")
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @CurrentUserId Long userId,
            @PathVariable Long postId
    ) {
        communityPostService.delete(userId, postId);
        return ResponseEntity.ok(ApiResponse.ok(ResponseMessage.POST_DELETE_SUCCESS));
    }

    @Operation(summary = "전체 게시글 조회")
    @GetMapping("list")
    public ResponseEntity<ApiResponse<List<CommunityPostResponse>>> getAllPosts(
            @RequestParam String targetLanguage
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                communityPostService.getAllPosts(targetLanguage)
        ));
    }

    //제목 / 내용 검색
    @Operation(summary = "키워드로 게시글 검색")
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
    @Operation(summary = "카테고리별 게시글 조회")
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
