-- Flyway 데이터 마이그레이션(V3~V5)에 필요한 테이블 생성
-- JPA ddl-auto가 Flyway보다 늦게 실행되므로 여기서 미리 생성

CREATE TABLE IF NOT EXISTS drug (
    drug_id      BIGINT       NOT NULL AUTO_INCREMENT,
    item_seq     VARCHAR(255) NOT NULL,
    name         TEXT,
    efficacy     TEXT,
    caution      TEXT,
    use_method   TEXT,
    intrc_qesitm TEXT,
    se_qesitm    TEXT,
    opened_at    DATETIME,
    PRIMARY KEY (drug_id),
    UNIQUE KEY uk_drug_item_seq (item_seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS hospital (
    hospital_id BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255),
    address     VARCHAR(255),
    department  VARCHAR(255),
    phone       VARCHAR(255),
    latitude    DECIMAL(19,7),
    longitude   DECIMAL(19,7),
    sido_nm     VARCHAR(255),
    sggu_nm     VARCHAR(255),
    homepage    VARCHAR(255),
    PRIMARY KEY (hospital_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS prescription (
    prescription_id BIGINT NOT NULL AUTO_INCREMENT,
    user_id         BIGINT,
    created_at      DATETIME,
    PRIMARY KEY (prescription_id),
    CONSTRAINT fk_prescription_user FOREIGN KEY (user_id) REFERENCES user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS prescription_drug (
    prescription_drug_id BIGINT NOT NULL AUTO_INCREMENT,
    prescription_id      BIGINT,
    drug_id              BIGINT,
    dosage               VARCHAR(255),
    frequency            VARCHAR(255),
    duration             VARCHAR(255),
    translated_content   TEXT,
    PRIMARY KEY (prescription_drug_id),
    CONSTRAINT fk_pd_prescription FOREIGN KEY (prescription_id) REFERENCES prescription (prescription_id),
    CONSTRAINT fk_pd_drug         FOREIGN KEY (drug_id)         REFERENCES drug (drug_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
