package com.carelink.communityPost.service;

import com.carelink.comment.repository.CommentRepository;
import com.carelink.communityPost.entity.CommunityPostEntity;
import com.carelink.communityPost.entity.dto.CommunityPostCreateRequest;
import com.carelink.communityPost.entity.dto.CommunityPostResponse;
import com.carelink.communityPost.entity.dto.CommunityPostUpdateRequest;
import com.carelink.communityPost.repository.CommunityPostRepository;
import com.carelink.global.exception.RestApiException;
import com.carelink.global.type.ErrorCode;
import com.carelink.translation.service.TranslationService;
import com.carelink.user.entity.UserEntity;
import com.carelink.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;


import java.util.List;


@Service
@RequiredArgsConstructor
public class CommunityPostService {

    private final CommunityPostRepository communityPostRepository;
    private final UserRepository userRepository;
    private final TranslationService translationService;
    private final CommentRepository commentRepository;
    private final ObjectMapper objectMapper;


    @Transactional
    public CommunityPostResponse create(Long userId, CommunityPostCreateRequest request) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));

        CommunityPostEntity post = CommunityPostEntity.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .language(request.getLanguage())
                .tag(request.getTag())
                .category(request.getCategory())
                .build();

        CommunityPostEntity savePost = communityPostRepository.save(post);

        return CommunityPostResponse.from(savePost, savePost.getTitle(), savePost.getContent());
    }

    @Transactional
    public CommunityPostResponse getById(Long postId, String targetLanguage) {
        CommunityPostEntity post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));

        String title = getOrTranslateTitle(post, targetLanguage);
        String content = getOrTranslateContent(post, targetLanguage);

        return CommunityPostResponse.from(post, title, content);
    }

    @Transactional
    public List<CommunityPostResponse> getByTag(String tag, String targetLanguage) {
        return communityPostRepository.findByTagContaining(tag).stream()
                .map(post -> CommunityPostResponse.from(
                        post,
                        getOrTranslateTitle(post, targetLanguage),
                        getOrTranslateContent(post, targetLanguage)
                ))
                .toList();
    }



    @Transactional
    public List<CommunityPostResponse> getByLanguage(String language, String targetLanguage) {
        return communityPostRepository.findByLanguage(language).stream()
                .map(post -> CommunityPostResponse.from(
                        post,
                        getOrTranslateTitle(post, targetLanguage),
                        getOrTranslateContent(post, targetLanguage)
                ))
                .toList();
    }


    @Transactional
    public CommunityPostResponse update(Long userId, Long postId, CommunityPostUpdateRequest request) {
        CommunityPostEntity post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));

        validateOwner(userId, post.getUser().getUserId());

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setLanguage(request.getLanguage());
        post.setTag(request.getTag());
        post.setCategory(request.getCategory());
        post.setTranslatedTitle(null);
        post.setTranslatedContent(null);

        return CommunityPostResponse.from(post, post.getTitle(), post.getContent());
    }

    @Transactional
    public void delete(Long userId, Long postId) {
        CommunityPostEntity post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));

        validateOwner(userId, post.getUser().getUserId());
        communityPostRepository.delete(post);
    }

    private void validateOwner(Long currentUserId, Long ownerId) {
        if (!currentUserId.equals(ownerId)) {
            throw new RestApiException(ErrorCode.FORBIDDEN);
        }
    }

    private String getOrTranslateTitle(CommunityPostEntity post, String targetLanguage) {
        if (post.getLanguage().equals(targetLanguage)) {
            return post.getTitle();
        }

        Map<String, String> titleMap = readTranslations(post.getTranslatedTitle());

        if (titleMap.containsKey(targetLanguage)) {
            return titleMap.get(targetLanguage);
        }

        String translatedTitle = translationService.translate(
                post.getTitle(),
                post.getLanguage(),
                targetLanguage
        );

        titleMap.put(targetLanguage, translatedTitle);
        post.setTranslatedTitle(writeTranslations(titleMap));

        return translatedTitle;
    }

    private String getOrTranslateContent(CommunityPostEntity post, String targetLanguage) {
        if (post.getLanguage().equals(targetLanguage)) {
            return post.getContent();
        }

        Map<String, String> contentMap = readTranslations(post.getTranslatedContent());

        if (contentMap.containsKey(targetLanguage)) {
            return contentMap.get(targetLanguage);
        }

        String translatedContent = translationService.translate(
                post.getContent(),
                post.getLanguage(),
                targetLanguage
        );

        contentMap.put(targetLanguage, translatedContent);
        post.setTranslatedContent(writeTranslations(contentMap));

        return translatedContent;
    }

    private Map<String, String> readTranslations(String json) {
        try {
            if (json == null || json.isBlank()) {
                return new HashMap<>();
            }
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private String writeTranslations(Map<String, String> translations) {
        try {
            return objectMapper.writeValueAsString(translations);
        } catch (Exception e) {
            throw new RestApiException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

}
