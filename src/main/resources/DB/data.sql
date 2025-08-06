
USE timespace;
SET FOREIGN_KEY_CHECKS = 0;

-- 1) Admin 계정 (비밀번호는 나중에 인코딩해서 넣으세요)
INSERT INTO Admin (id, email, password, admin_name) VALUES
    (1, 'admin1@example.com', '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', 'Administrator One'),
    (2, 'admin2@example.com', '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', 'Administrator Two'),
    (3, 'admin3@example.com', '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', 'Administrator Three');

-- 2) 사용자 5명
INSERT INTO `User` (user_name, email, password, university, phone_number, self_memo, birth_date, profile_image_url) VALUES
    ('김민수',    'aa@test.com',   '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', 1, '010-1111-1111', 'Hi, I am Alice.',                     '1995-03-15', 'https://sampleImgUrl'),
    ('이지훈',    'bb@test.com',   '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', 2, '010-2222-2222', 'Bob here!',                          '1992-07-20', 'https://sampleImgUrl'),
    ('박서연',    'cc@test.com',   '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', 3, '010-3333-3333', NULL,                                 '1990-12-05', 'https://sampleImgUrl'),
    ('최민호',    'dd@test.com',   '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', 1, NULL,                                 'Diana''s memo',                     '1998-11-02', 'https://sampleImgUrl'),
    ('정윤아',    'ee@test.com',   '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', 2, '010-5555-5555', NULL,                                 '1993-05-30', 'https://sampleImgUrl'),
    ('유준호',    'ff@test.com',   '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', 3, '010-6666-6666', 'Hello, I am Frank.',                 '1994-09-12', 'https://sampleImgUrl'),
    ('박하영',    'gg@test.com',   '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', 2, NULL,                                 NULL,                                 '1996-02-28', 'https://sampleImgUrl');

INSERT INTO`User`(user_name,email,password,university,phone_number,self_memo,birth_date,profile_image_url) VALUES
      ('seongjin','glisvast@hanyang.ac.kr','$2a$10$8wdZt86wsOEFxzsWucmJmexAn6ZelqWNM55VEb2d4jD2sL9As1WWO',4,NULL,NULL,NULL,NULL),
      ('seongjincho','sj01120223@gmail.com','$2a$10$zJg6xWwKvdg1Ko81dhRmpe3qDYPEiwFGwx2JCo0c9Kv1adh2VG.bG',4,NULL,NULL,NULL,NULL);


INSERT INTO SocialAccount (user_id, provider, provider_user_id) VALUES
    (2, 'kakao', 'kakao_2002'),
    (3, 'google','google_3003');

USE timespace;

-- User 1
INSERT INTO SingleSchedule (user_id, title, color, date, day, start_time, end_time) VALUES
    (1, '데이터베이스시스템및응용',               1, '2025-08-05', 2, '09:00:00', '10:30:00'),
    (1, '이산수학',                             2, '2025-08-05', 2, '13:00:00', '14:30:00'),
    (1, '컴파일러설계',                         3, '2025-08-05', 2, '14:30:00', '16:00:00'),
    (1, '프론트 개발',                          4, '2025-08-05', 2, '18:00:00', '19:30:00'),
    (1, '컴퓨터네트워크',                       5, '2025-08-06', 3, '13:00:00', '16:00:00'),
    (1, '커리어개발II·취·창업진로포트폴리오',    6, '2025-08-06', 3, '17:00:00', '18:00:00'),
    (1, '컴파일러설계',                         3, '2025-08-07', 4, '09:00:00', '10:30:00'),
    (1, '컴퓨터네트워크',                       5, '2025-08-07', 4, '10:30:00', '12:00:00'),
    (1, '이산수학',                             2, '2025-08-07', 4, '16:00:00', '17:30:00'),
    (1, '데이터베이스시스템및응용',             1, '2025-08-08', 5, '09:00:00', '10:30:00'),
    (1, '소프트웨어공학',                       7, '2025-08-08', 5, '13:00:00', '16:00:00');

    -- User 2
INSERT INTO SingleSchedule (user_id, title, color, date, day, start_time, end_time) VALUES
    (2, '시스템프로그래밍',           8, '2025-08-04', 1, '13:00:00', '15:00:00'),
    (2, '알고리즘및문제해결기법',     9, '2025-08-05', 2, '10:30:00', '12:00:00'),
    (2, '인공지능',                 10, '2025-08-05', 2, '14:30:00', '16:00:00'),
    (2, '시스템프로그래밍',           8, '2025-08-05', 2, '17:00:00', '19:00:00'),
    (2, '알로하',                   11, '2025-08-05', 2, '19:00:00', '20:30:00'),
    (2, '기계학습이론',             12, '2025-08-06', 3, '13:00:00', '14:30:00'),
    (2, '인공지능',                 10, '2025-08-07', 4, '13:00:00', '14:30:00'),
    (2, '기계학습이론',             12, '2025-08-07', 4, '14:30:00', '16:00:00'),
    (2, '알고리즘및문제해결기법',     9, '2025-08-08', 5, '10:30:00', '12:00:00');

    -- User 3
INSERT INTO SingleSchedule (user_id, title, color, date, day, start_time, end_time) VALUES
    (3, '컴퓨터그래픽스',                       1, '2025-08-04', 1, '09:00:00', '10:30:00'),
    (3, '필라테스',                             2, '2025-08-05', 2, '09:00:00', '10:30:00'),
    (3, '컴퓨터구조론',                         3, '2025-08-05', 2, '11:00:00', '12:30:00'),
    (3, 'AI+X:인공지능',                        4, '2025-08-07', 4, '09:00:00', '10:30:00'),
    (3, '테크노경영학(스타트업종합설계)',        5, '2025-08-08', 5, '09:00:00', '10:30:00'),
    (3, '컴퓨터구조론',                         3, '2025-08-08', 5, '11:00:00', '12:30:00'),
    (3, '운영체제',                             6, '2025-08-08', 5, '15:00:00', '16:30:00'),
    (3, '운영체제',                             6, '2025-08-09', 6, '17:00:00', '18:30:00');


    -- Image #4 → User 4 일정 전체 (2025-08-04~10, 기본 1.5시간, 동일 과목 동일 color)
INSERT INTO SingleSchedule (user_id, title, color, date, day, start_time, end_time) VALUES
    (4, '미분적분학2',                4, '2025-08-04', 1, '10:30:00', '12:00:00'),
    (4, '선형대수',                  7, '2025-08-04', 1, '13:00:00', '14:30:00'),
    (4, '창의적소프트웨어프로그래밍', 2, '2025-08-05', 2, '10:00:00', '11:30:00'),
    (4, '교양댄스스포츠',             9, '2025-08-05', 2, '15:00:00', '17:00:00'),
    (4, '미분적분학2',                4, '2025-08-06', 3, '09:00:00', '10:30:00'),
    (4, '선형대수',                  7, '2025-08-06', 3, '13:00:00', '14:30:00'),
    (4, '창의적소프트웨어프로그래밍', 2, '2025-08-06', 3, '15:00:00', '17:00:00'),
    (4, '창의적소프트웨어프로그래밍', 2, '2025-08-07', 4, '15:00:00', '17:00:00'),
    (4, '미시경제원론',              6, '2025-08-08', 5, '10:00:00', '11:30:00'),
    (4, '헌법의이해',                10,'2025-08-08', 5, '13:00:00', '16:00:00');

    -- Image #5 → User 5 일정 전체 (2025-08-04~10, 기본 1.5시간, 동일 과목 동일 color)
INSERT INTO SingleSchedule (user_id, title, color, date, day, start_time, end_time) VALUES
    (5, '일반물리학및실험1',           1, '2025-08-04', 1, '09:00:00', '10:30:00'),
    (5, '말과글',                     3, '2025-08-05', 2, '10:30:00', '12:00:00'),
    (5, '소프트웨어입문설계',         2, '2025-08-06', 3, '10:00:00', '11:30:00'),
    (5, '말과글',                     3, '2025-08-07', 4, '09:00:00', '10:30:00'),
    (5, '일반물리학및실험1',           1, '2025-08-07', 4, '13:00:00', '14:30:00'),
    (5, '소프트웨어입문설계',         2, '2025-08-07', 4, '15:00:00', '17:00:00'),
    (5, '소프트웨어입문설계',         2, '2025-08-08', 5, '10:00:00', '12:00:00'),
    (5, '미분적분학1',                4, '2025-08-08', 5, '16:00:00', '17:30:00'),
    (5, '미분적분학1',                4, '2025-08-05', 2, '14:30:00', '16:00:00'),
    (5, '과학기술의철학적이해',        6, '2025-08-05', 2, '16:30:00', '18:00:00');


    -- Image #6 → User 6 일정 전체 (2025-08-04~10, 기본 1.5시간, 동일 과목 동일 color)
INSERT INTO SingleSchedule (user_id, title, color, date, day, start_time, end_time) VALUES
    (6, '자료조직론',               3, '2025-08-04', 1, '10:00:00', '13:00:00'),
    (6, '진로탐색 및 자기계발',     7, '2025-08-04', 1, '14:00:00', '16:00:00'),
    (6, '채플',                    11,'2025-08-04', 1, '16:00:00', '17:00:00'),
    (6, '문헌정보학원론',           4, '2025-08-06', 3, '10:00:00', '13:00:00'),
    (6, '정보능력',                6, '2025-08-06', 3, '14:00:00', '16:00:00'),
    (6, '전공실무영어',             9, '2025-08-07', 4, '10:00:00', '13:00:00'),
    (6, '기독교와윤리',             12,'2025-08-07', 4, '14:00:00', '15:00:00'),
    (6, '장서개발론',               5, '2025-08-08', 5, '13:00:00', '14:30:00');

    -- Image #7 → User 7 일정 전체 (2025-08-04~10, 기본 1.5시간, 동일 과목 동일 color)
INSERT INTO SingleSchedule (user_id, title, color, date, day, start_time, end_time) VALUES
    (7, '글로벌히스토리의재조명',       3, '2025-08-04', 1, '11:00:00', '12:30:00'),
    (7, '미술실기2',                   5, '2025-08-04', 1, '14:00:00', '15:30:00'),
    (7, '체육실기2',                   8, '2025-08-04', 1, '16:00:00', '17:30:00'),
    (7, '특수교육학개론',             4, '2025-08-05', 2, '11:00:00', '12:30:00'),
    (7, '영어교육론1',                6, '2025-08-05', 2, '14:00:00', '15:30:00'),
    (7, '사회교육론1',                2, '2025-08-06', 3, '11:00:00', '12:30:00'),
    (7, '수학교육론1',                7, '2025-08-06', 3, '14:00:00', '15:30:00'),
    (7, '미술과교육론1',              4, '2025-08-07', 4, '11:00:00', '12:30:00'),
    (7, '기독교와윤리',               1, '2025-08-07', 4, '13:00:00', '14:30:00'),
    (7, '장서개발론',                 5, '2025-08-08', 5, '13:00:00', '14:30:00'),
    (7, '동서양윤리사상비교',         7, '2025-08-08', 5, '15:00:00', '16:30:00');

INSERT INTO Friend (user_id, friend_id, is_favorite, visibility, nickname) VALUES
    (1, 2, FALSE, 'SECRET', '이지훈'),
    (1, 3, FALSE, 'ALL',    '박서연'),
    (1, 4, FALSE, 'ALL',    '최민호'),
    (1, 5, FALSE, 'ALL',    '정윤아'),
    (1, 6, FALSE, 'ALL',    '유준호'),
    (1, 7, FALSE, 'ALL',    '박하영'),

    (2, 1, FALSE, 'SECRET', '김민수'),
    (2, 3, FALSE, 'ALL',    '박서연'),
    (2, 4, FALSE, 'ALL',    '최민호'),
    (2, 5, FALSE, 'ALL',    '정윤아'),
    (2, 6, FALSE, 'ALL',    '유준호'),
    (2, 7, FALSE, 'ALL',    '박하영'),

    (3, 1, FALSE, 'ALL',    '김민수'),
    (3, 2, FALSE, 'ALL',    '이지훈'),
    (3, 4, FALSE, 'ALL',    '최민호'),
    (3, 5, FALSE, 'ALL',    '정윤아'),
    (3, 6, FALSE, 'ALL',    '유준호'),
    (3, 7, FALSE, 'ALL',    '박하영'),

    (4, 1, FALSE, 'ALL',    '김민수'),
    (4, 2, FALSE, 'ALL',    '이지훈'),
    (4, 3, FALSE, 'ALL',    '박서연'),
    (4, 5, FALSE, 'ALL',    '정윤아'),
    (4, 6, FALSE, 'ALL',    '유준호'),
    (4, 7, FALSE, 'ALL',    '박하영'),

    (5, 1, FALSE, 'ALL',    '김민수'),
    (5, 2, FALSE, 'ALL',    '이지훈'),
    (5, 3, FALSE, 'ALL',    '박서연'),
    (5, 4, FALSE, 'ALL',    '최민호'),
    (5, 6, FALSE, 'ALL',    '유준호'),
    (5, 7, FALSE, 'ALL',    '박하영'),

    (6, 1, FALSE, 'ALL',    '김민수'),
    (6, 2, FALSE, 'ALL',    '이지훈'),
    (6, 3, FALSE, 'ALL',    '박서연'),
    (6, 4, FALSE, 'ALL',    '최민호'),
    (6, 5, FALSE, 'ALL',    '정윤아'),
    (6, 7, FALSE, 'ALL',    '박하영'),

    (7, 1, FALSE, 'ALL',    '김민수'),
    (7, 2, FALSE, 'ALL',    '이지훈'),
    (7, 3, FALSE, 'ALL',    '박서연'),
    (7, 4, FALSE, 'ALL',    '최민호'),
    (7, 5, FALSE, 'ALL',    '정윤아'),
    (7, 6, FALSE, 'ALL',    '유준호');


-- 9) 그룹 생성 (alice master, bob master)
-- 1) 그룹 생성: 동아리 1조
INSERT INTO `Group` (master_id, group_name, group_type, max_member, category, unique_code) VALUES
    (1, '동아리 1조', 'NORMAL', 7, 4, 'DONGARI1JO'),
    (1, '밥약 2조', 'NORMAL', 7, 4, '1회성 밥약'),
    (4, '연합학회', 'NORMAL', 7, 4, '1회성 밥약');

-- 2) GroupMembers
INSERT INTO GroupMembers (group_id, user_id, is_favorite) VALUES
    (1, 1, FALSE),
    (1, 2, FALSE),
    (1, 3, FALSE),
    (1, 4, FALSE),
    (1, 5, FALSE),
    (1, 6, FALSE),
    (1, 7, FALSE),
    (2, 1, FALSE),
    (2, 2, FALSE),
    (2, 3, FALSE),
    (2, 4, FALSE),
    (3, 7, FALSE),
    (3, 6, FALSE),
    (3, 5, FALSE),
    (3, 4, FALSE);



-- 14) 시스템 공지 (SystemNotices)
INSERT INTO SystemNotices (title, content) VALUES
    ('시스템 점검 안내',  '7/30 02:00 ~ 04:00 시스템 점검이 예정되어 있습니다.'),
    ('새 기능 출시',      '캘린더 공유 기능이 추가되었습니다.');

SET FOREIGN_KEY_CHECKS = 1;

