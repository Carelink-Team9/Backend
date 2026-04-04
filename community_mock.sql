-- community mock data aligned with the current backend entities
-- - community_post PK column: community_post_id
-- - comment FK column: community_post_id
-- - removed translated_title because the entity does not have that column
-- - category values follow CommunityPostCategory: NOTICE, QUESTION, REVIEW, FREE
-- - user seed is excluded because V2__insert_user_data.sql already creates user_id 1~5

INSERT INTO community_post (
    community_post_id,
    user_id,
    title,
    content,
    translated_content,
    language,
    tag,
    category,
    created_at
) VALUES
    (
        1001,
        1,
        '감기약 먹고 너무 졸려요',
        '감기약을 먹은 뒤 낮에 계속 졸립니다. 원래 이런가요?',
        JSON_OBJECT(
            'en', 'I feel sleepy all day after taking cold medicine. Is this normal?',
            'vi', 'Sau khi uống thuốc cảm tôi buồn ngủ cả ngày. Điều này có bình thường không?'
        ),
        'ko',
        JSON_ARRAY('medicine', 'side-effect'),
        'QUESTION',
        '2026-04-02 10:00:00'
    ),
    (
        1002,
        2,
        'Lan dau den benh vien Han Quoc can chuan bi gi?',
        'Toi sap di kham lan dau o Han Quoc. Ngoai ho chieu va the bao hiem thi can mang gi nua?',
        JSON_OBJECT(
            'ko', '한국 병원 첫 방문인데 여권과 보험 정보 외에 더 챙길 것이 있나요?',
            'en', 'This is my first hospital visit in Korea. Besides my passport and insurance information, what else should I bring?'
        ),
        'vi',
        JSON_ARRAY('hospital', 'first-visit'),
        'QUESTION',
        '2026-04-02 10:05:00'
    ),
    (
        1003,
        3,
        '韩国药房买感冒药的体验',
        '昨晚去药房买感冒药，药师会先问症状，沟通比想象中顺利。',
        JSON_OBJECT(
            'ko', '어젯밤 약국에서 감기약을 샀는데 약사가 증상을 먼저 물어봤고 생각보다 소통이 잘됐어요.',
            'en', 'I bought cold medicine at a pharmacy last night. The pharmacist asked about my symptoms first and the communication was smoother than expected.'
        ),
        'zh',
        JSON_ARRAY('pharmacy', 'review'),
        'REVIEW',
        '2026-04-02 10:10:00'
    ),
    (
        1004,
        4,
        'Kasalxonada tarjima ilovasidan foydalansa boladimi?',
        'Qabul oynasida savollar kop bolsa, tarjima ilovasini korsatsam muammo bolmaydimi?',
        JSON_OBJECT(
            'ko', '접수 창구에서 질문이 많을 때 번역 앱을 보여줘도 괜찮을까요?',
            'en', 'If there are many questions at reception, is it okay to show a translation app?'
        ),
        'uz',
        JSON_ARRAY('hospital', 'translation-app'),
        'QUESTION',
        '2026-04-02 10:15:00'
    ),
    (
        1005,
        5,
        'รอพบแพทย์นานมาก ปกติไหม',
        'วันนี้รอคิวนานเกือบหนึ่งชั่วโมง อยากรู้ว่าโรงพยาบาลที่นี่ปกติเป็นแบบนี้ไหม',
        JSON_OBJECT(
            'ko', '오늘 대기 시간이 거의 한 시간이었는데 보통 이 정도 기다리나요?',
            'en', 'I waited almost an hour today. Is that a normal wait time here?'
        ),
        'th',
        JSON_ARRAY('hospital', 'waiting-time'),
        'QUESTION',
        '2026-04-02 10:20:00'
    ),
    (
        1006,
        1,
        '응급 증상일 때는 먼저 119 또는 응급실을 이용하세요',
        '가슴 통증, 호흡 곤란, 의식 저하가 있으면 커뮤니티 답변을 기다리지 말고 바로 응급실이나 119에 연락하세요.',
        JSON_OBJECT(
            'en', 'If you have chest pain, trouble breathing, or reduced consciousness, do not wait for community replies. Contact emergency services immediately.',
            'vi', 'Nếu có đau ngực, khó thở hoặc giảm ý thức, đừng chờ câu trả lời trong cộng đồng. Hãy liên hệ cấp cứu ngay.'
        ),
        'ko',
        JSON_ARRAY('notice', 'emergency'),
        'NOTICE',
        '2026-04-02 10:25:00'
    ),
    (
        1007,
        2,
        'Thuoc nay nen uong truoc hay sau bua an?',
        'Toa thuoc khong ghi ro, nen toi dang hoi vi so uong sai gio.',
        JSON_OBJECT(
            'ko', '처방전에 식전인지 식후인지 명확하지 않아서 복용 시간을 헷갈리고 있습니다.',
            'en', 'The prescription does not clearly say whether I should take it before or after meals, so I am unsure about the timing.'
        ),
        'vi',
        JSON_ARRAY('medicine', 'dosage'),
        'QUESTION',
        '2026-04-02 10:30:00'
    ),
    (
        1008,
        3,
        '韩国医院初诊流程比想象中顺利',
        '第一次去医院时有点紧张，但挂号和问诊流程都比预想中清楚。',
        JSON_OBJECT(
            'ko', '처음 병원에 갈 때 긴장했는데 접수와 진료 과정이 생각보다 훨씬 명확했습니다.',
            'en', 'I was nervous on my first hospital visit, but the registration and consultation process was much clearer than I expected.'
        ),
        'zh',
        JSON_ARRAY('hospital', 'review', 'first-visit'),
        'REVIEW',
        '2026-04-02 10:35:00'
    ),
    (
        1009,
        4,
        'Dorining qopidagi korsatmalarni tez tarjima qilish usuli',
        'Men kamera tarjimasi bilan dozani va kuniga necha marta ichishni tekshiraman.',
        JSON_OBJECT(
            'ko', '저는 카메라 번역으로 복용량과 하루 복용 횟수를 먼저 확인합니다.',
            'en', 'I usually check dosage and daily frequency first with a camera translation app.'
        ),
        'uz',
        JSON_ARRAY('medicine', 'translation-tip'),
        'FREE',
        '2026-04-02 10:40:00'
    ),
    (
        1010,
        5,
        'อาการดีขึ้นแล้ว ต้องกินยาจนครบไหม',
        'ตอนนี้รู้สึกดีขึ้นมาก แต่ยังเหลือยาอีกสองวัน เลยลังเลว่าจะหยุดเองได้ไหม',
        JSON_OBJECT(
            'ko', '증상은 좋아졌지만 약이 이틀치 남아 있어서 끝까지 먹어야 하는지 고민입니다.',
            'en', 'I feel much better now, but I still have two days of medicine left. Should I finish it?'
        ),
        'th',
        JSON_ARRAY('medicine', 'course'),
        'QUESTION',
        '2026-04-02 10:45:00'
    );

INSERT INTO `comment` (
    comment_id,
    user_id,
    community_post_id,
    content,
    translated_content,
    created_at
) VALUES
    (
        2001,
        3,
        1001,
        '항히스타민 성분이 있으면 졸릴 수 있어요.',
        CAST(JSON_OBJECT('en', 'Cold medicine can cause drowsiness if it contains antihistamines.') AS CHAR),
        '2026-04-02 13:00:00'
    ),
    (
        2002,
        5,
        1001,
        '운전 전에는 복용 시간을 조정하는 게 좋습니다.',
        CAST(JSON_OBJECT('en', 'If you need to drive, it is safer to adjust the time you take it.') AS CHAR),
        '2026-04-02 13:03:00'
    ),
    (
        2003,
        1,
        1002,
        '신분증, 보험 정보, 복용 중인 약 사진이 있으면 접수할 때 도움이 됩니다.',
        CAST(JSON_OBJECT('en', 'Bring your ID, insurance information, and photos of any medicine you are taking.') AS CHAR),
        '2026-04-02 13:06:00'
    ),
    (
        2004,
        4,
        1002,
        '주소와 연락처를 메모해 두면 처음 접수할 때 훨씬 빨라요.',
        CAST(JSON_OBJECT('en', 'It is faster at first registration if you have your address and phone number written down.') AS CHAR),
        '2026-04-02 13:09:00'
    ),
    (
        2005,
        2,
        1003,
        '약사 설명이 친절한 곳이 많아서 초보자도 비교적 편하게 살 수 있어요.',
        CAST(JSON_OBJECT('en', 'Many pharmacists explain things kindly, so even first-time visitors can buy medicine without too much stress.') AS CHAR),
        '2026-04-02 13:12:00'
    ),
    (
        2006,
        5,
        1003,
        '현재 먹는 약을 같이 보여주면 더 정확하게 안내해 주더라고요.',
        CAST(JSON_OBJECT('en', 'It helps to show the medicine you are already taking so they can guide you more accurately.') AS CHAR),
        '2026-04-02 13:15:00'
    ),
    (
        2007,
        1,
        1004,
        '번역 앱 화면을 보여주는 분들 많아요. 핵심 증상만 미리 적어두면 더 좋습니다.',
        CAST(JSON_OBJECT('en', 'Many people show translation app screens. It helps even more if you write your main symptoms in advance.') AS CHAR),
        '2026-04-02 13:18:00'
    ),
    (
        2008,
        3,
        1005,
        '대형 병원은 오전에 대기 시간이 길 수 있어요.',
        CAST(JSON_OBJECT('en', 'Large hospitals often have longer wait times in the morning.') AS CHAR),
        '2026-04-02 13:21:00'
    ),
    (
        2009,
        2,
        1006,
        '가슴 통증이나 호흡 곤란이면 지체하지 말고 바로 응급실로 가세요.',
        CAST(JSON_OBJECT('en', 'If you have chest pain or trouble breathing, do not delay and go straight to the emergency room.') AS CHAR),
        '2026-04-02 13:24:00'
    ),
    (
        2010,
        5,
        1007,
        '식후 복용인지 애매하면 처방전 사진이나 약 봉투 문구를 다시 확인해 보세요.',
        CAST(JSON_OBJECT('en', 'If meal timing is unclear, check the prescription photo or the text on the medicine bag again.') AS CHAR),
        '2026-04-02 13:27:00'
    ),
    (
        2011,
        1,
        1007,
        '빈속에 먹으면 속이 불편한 약도 있어서 안내 문구를 꼭 보는 편이 안전해요.',
        CAST(JSON_OBJECT('en', 'Some medicine can upset your stomach on an empty stomach, so it is safer to read the instructions carefully.') AS CHAR),
        '2026-04-02 13:30:00'
    ),
    (
        2012,
        4,
        1008,
        '접수에서 천천히 말해 달라고 부탁하면 훨씬 편합니다.',
        CAST(JSON_OBJECT('en', 'It is much easier if you ask the staff to speak slowly at reception.') AS CHAR),
        '2026-04-02 13:33:00'
    ),
    (
        2013,
        3,
        1009,
        '카메라 번역으로 복용 횟수를 먼저 읽는 방법이 꽤 유용합니다.',
        CAST(JSON_OBJECT('en', 'Using camera translation to check the daily dosage first is surprisingly useful.') AS CHAR),
        '2026-04-02 13:36:00'
    ),
    (
        2014,
        1,
        1010,
        '증상이 남아 있으면 보통은 처방받은 기간만큼 다 먹는 편이 안전합니다.',
        CAST(JSON_OBJECT('en', 'If symptoms remain, it is usually safer to finish the medicine for the prescribed duration.') AS CHAR),
        '2026-04-02 13:39:00'
    ),
    (
        2015,
        2,
        1010,
        '부작용이 심하면 스스로 중단하지 말고 병원이나 약국에 먼저 문의하세요.',
        CAST(JSON_OBJECT('en', 'If side effects are strong, do not stop on your own. Ask a hospital or pharmacy first.') AS CHAR),
        '2026-04-02 13:42:00'
    );
