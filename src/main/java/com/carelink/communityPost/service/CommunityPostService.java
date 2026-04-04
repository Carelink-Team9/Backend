package com.carelink.communityPost.service;

import com.carelink.comment.repository.CommentRepository;
import com.carelink.communityPost.entity.CommunityPostCategory;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.List;


@Slf4j
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
                .language(user.getLanguage())
                .tag(request.getTag())
                .category(request.getCategory())
                .build();

        CommunityPostEntity savePost = communityPostRepository.save(post);

        return CommunityPostResponse.from(
                savePost,
                savePost.getTitle(),
                savePost.getContent(),
                getCommentCount(savePost.getCommunityPostId())
        );
    }

    @Transactional
    public CommunityPostResponse getById(Long postId, String targetLanguage) {
        CommunityPostEntity post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));

        String title = getOrTranslateTitle(post, targetLanguage);
        String content = getOrTranslateContent(post, targetLanguage);

        return CommunityPostResponse.from(
                post,
                title,
                content,
                getCommentCount(post.getCommunityPostId())
        );
    }

    @Transactional
    public List<CommunityPostResponse> getByTag(String tag, String targetLanguage) {
        return communityPostRepository.findByTagContaining(tag).stream()
                .map(post -> CommunityPostResponse.from(
                        post,
                        getOrTranslateTitle(post, targetLanguage),
                        getOrTranslateContent(post, targetLanguage),
                        getCommentCount(post.getCommunityPostId())
                ))
                .toList();
    }

    @Transactional
    public List<CommunityPostResponse> getByLanguage(String language, String targetLanguage) {
        return communityPostRepository.findByLanguage(language).stream()
                .map(post -> CommunityPostResponse.from(
                        post,
                        getOrTranslateTitle(post, targetLanguage),
                        getOrTranslateContent(post, targetLanguage),
                        getCommentCount(post.getCommunityPostId())
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
        post.setTag(request.getTag());
        post.setCategory(request.getCategory());
        post.setTranslatedTitle(null);
        post.setTranslatedContent(null);

        return CommunityPostResponse.from(
                post,
                post.getTitle(),
                post.getContent(),
                getCommentCount(post.getCommunityPostId())
        );
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

    // 커뮤니티 전체 조회
    @Transactional
    public List<CommunityPostResponse> getAllPosts(String targetLanguage) {
        return communityPostRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(post -> {
                    ensureTranslated(post, targetLanguage);
                    return CommunityPostResponse.from(
                            post,
                            getCachedTitle(post, targetLanguage),
                            getCachedContent(post, targetLanguage),
                            getCommentCount(post.getCommunityPostId())
                    );
                })
                .toList();
    }

    // 커뮤니티 제목 / 내용 검색
    @Transactional
    public List<CommunityPostResponse> searchByKeyword(String keyword, String targetLanguage) {
        return communityPostRepository
                .findByTitleContainingOrContentContainingOrderByCreatedAtDesc(keyword, keyword)
                .stream()
                .map(post -> {
                    ensureTranslated(post, targetLanguage);
                    return CommunityPostResponse.from(
                            post,
                            getCachedTitle(post, targetLanguage),
                            getCachedContent(post, targetLanguage),
                            getCommentCount(post.getCommunityPostId())
                    );
                })
                .toList();
    }

    // 카테고리별 검색
    @Transactional
    public List<CommunityPostResponse> getByCategory(
            CommunityPostCategory category,
            String targetLanguage
    ) {
        return communityPostRepository.findByCategory(category).stream()
                .map(post -> {
                    ensureTranslated(post, targetLanguage);
                    return CommunityPostResponse.from(
                            post,
                            getCachedTitle(post, targetLanguage),
                            getCachedContent(post, targetLanguage),
                            getCommentCount(post.getCommunityPostId())
                    );
                })
                .toList();
    }

    private long getCommentCount(Long postId) {
        return commentRepository.countByCommunityPost_CommunityPostId(postId);
    }

    /**
     * 제목과 본문을 한 번의 Gemini 호출로 번역하고 DB에 캐시합니다.
     * 이미 캐시된 경우 호출하지 않습니다.
     * 429 등 에러 발생 시 원문을 캐시에 저장하고 반환합니다 (크래시 방지).
     */
    private void ensureTranslated(CommunityPostEntity post, String targetLanguage) {
        if (post.getLanguage().equals(targetLanguage)) return;

        Map<String, String> titleMap = readTranslations(post.getTranslatedTitle());
        Map<String, String> contentMap = readTranslations(post.getTranslatedContent());

        boolean needTitle = !titleMap.containsKey(targetLanguage);
        boolean needContent = !contentMap.containsKey(targetLanguage);

        if (!needTitle && !needContent) return;

        try {
            if (needTitle && needContent) {
                String[] translated = translationService.translatePair(
                        post.getTitle(), post.getContent(), post.getLanguage(), targetLanguage);
                titleMap.put(targetLanguage, translated[0]);
                contentMap.put(targetLanguage, translated[1]);
            } else if (needTitle) {
                titleMap.put(targetLanguage, translationService.translate(
                        post.getTitle(), post.getLanguage(), targetLanguage));
            } else {
                contentMap.put(targetLanguage, translationService.translate(
                        post.getContent(), post.getLanguage(), targetLanguage));
            }
        } catch (Exception e) {
            log.warn("Translation failed for post {}, falling back to original: {}",
                    post.getCommunityPostId(), e.getMessage());
            if (needTitle) titleMap.put(targetLanguage, post.getTitle());
            if (needContent) contentMap.put(targetLanguage, post.getContent());
        }

        post.setTranslatedTitle(writeTranslations(titleMap));
        post.setTranslatedContent(writeTranslations(contentMap));
    }

    private String getCachedTitle(CommunityPostEntity post, String targetLanguage) {
        if (post.getLanguage().equals(targetLanguage)) return post.getTitle();
        return readTranslations(post.getTranslatedTitle()).getOrDefault(targetLanguage, post.getTitle());
    }

    private String getCachedContent(CommunityPostEntity post, String targetLanguage) {
        if (post.getLanguage().equals(targetLanguage)) return post.getContent();
        return readTranslations(post.getTranslatedContent()).getOrDefault(targetLanguage, post.getContent());
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
