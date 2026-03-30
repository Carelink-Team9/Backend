CREATE TABLE user
(
    user_id     BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    nationality VARCHAR(25)  NOT NULL,
    language    VARCHAR(10)  NOT NULL,
    created_at  DATETIME,
    updated_at  DATETIME,
    PRIMARY KEY (user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
