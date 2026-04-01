
-- =============================================================
-- 1. 처방전(Prescription) 데이터 생성
-- =============================================================

INSERT INTO prescription (prescription_id, user_id) VALUES
(1, 1), -- 김지수 처방전 A
(2, 1), -- 김지수 처방전 B (과거 이력)
(3, 2), -- Lan 처방전 A
(4, 2); -- Lan 처방전 B (과거 이력)

-- =============================================================
-- 2. 상세 정보 (prescription_drug_id는 자동 생성이므로 제외)
-- =============================================================

INSERT INTO prescription_drug (prescription_id, drug_id, dosage, frequency, duration) VALUES
-- [처방전 1번 상세]
(1, (SELECT drug_id FROM drug WHERE item_seq = '201502206'), '1정', '1일 3회', '3일'),
(1, (SELECT drug_id FROM drug WHERE item_seq = '201606897'), '1정', '4~6시간 간격', '3일'),

-- [처방전 2번 상세]
(2, (SELECT drug_id FROM drug WHERE item_seq = '201504861'), '2캡슐', '1일 3회', '5일'),
(2, (SELECT drug_id FROM drug WHERE item_seq = '201504877'), '1정', '필요 시', '5일'),

-- [처방전 3번 상세]
(3, (SELECT drug_id FROM drug WHERE item_seq = '201306112'), '1정', '1일 1회', '30일'),
(3, (SELECT drug_id FROM drug WHERE item_seq = '202400075'), '1정', '1일 2회', '30일'),

-- [처방전 4번 상세]
(4, (SELECT drug_id FROM drug WHERE item_seq = '202400079'), '10mg', '1일 1회', '7일');