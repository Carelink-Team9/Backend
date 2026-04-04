CREATE TABLE IF NOT EXISTS community_post (
    community_post_id BIGINT       NOT NULL AUTO_INCREMENT,
    user_id           BIGINT       NOT NULL,
    title             VARCHAR(255) NOT NULL,
    content           TEXT         NOT NULL,
    translated_content JSON,
    language          VARCHAR(20)  NOT NULL,
    created_at        DATETIME,
    tag               VARCHAR(255),
    category          VARCHAR(255) NOT NULL,
    PRIMARY KEY (community_post_id),
    KEY idx_community_post_user_id (user_id),
    KEY idx_community_post_category (category),
    KEY idx_community_post_language (language),
    CONSTRAINT fk_community_post_user FOREIGN KEY (user_id) REFERENCES user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `comment` (
    comment_id         BIGINT       NOT NULL AUTO_INCREMENT,
    user_id            BIGINT       NOT NULL,
    community_post_id  BIGINT       NOT NULL,
    content            TEXT         NOT NULL,
    translated_content TEXT,
    language           VARCHAR(20)  NOT NULL,
    created_at         DATETIME,
    PRIMARY KEY (comment_id),
    KEY idx_comment_user_id (user_id),
    KEY idx_comment_post_id (community_post_id),
    KEY idx_comment_language (language),
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES user (user_id),
    CONSTRAINT fk_comment_community_post FOREIGN KEY (community_post_id) REFERENCES community_post (community_post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
