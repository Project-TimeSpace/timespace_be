
USE timespace;
SET FOREIGN_KEY_CHECKS = 0;

-- 1) Admin 계정 (비밀번호는 나중에 인코딩해서 넣으세요)
INSERT INTO Admin (id, email, password, admin_name) VALUES
    (1, 'admin1@example.com', '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', 'Administrator One'),
    (2, 'admin2@example.com', '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', 'Administrator Two'),
    (3, 'admin3@example.com', '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', 'Administrator Three');

-- 2) 사용자 5명
INSERT INTO `User` (user_name, email, password, university, phone_number, self_memo, birth_date, profile_image_url) VALUES
    ('Alice Kim',   'aa@test.com',   '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', 1, '010-1111-1111', 'Hi, I am Alice.',   '1995-03-15', 'https://sampleImgUrl'),
    ('Bob Lee',     'bb@test.com',     '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', 2, '010-2222-2222', 'Bob here!',          '1992-07-20', 'https://sampleImgUrl'),
    ('Charlie Park','cc@test.com', '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', 3, '010-3333-3333', NULL,                '1990-12-05', 'https://sampleImgUrl'),
    ('Diana Choi',  'dd@test.com',   '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', 1, NULL,            'Diana\'s memo',      '1998-11-02', 'https://sampleImgUrl'),
    ('Evan Jung',   'ee@test.com',    '$2a$10$KqNntwd5aFUOPTj1gj62r.8BtmaUeUiae0H7r6Dj8tOlX9HuPgbNS', 2, '010-5555-5555', NULL,                '1993-05-30', 'https://sampleImgUrl');


-- 4) SocialAccount (user_id → 2,3)
INSERT INTO SocialAccount (user_id, provider, provider_user_id) VALUES
    (2, 'kakao', 'kakao_2002'),
    (3, 'google','google_3003');

-- 5) 개인 일정: SingleSchedule (alice,bob)
INSERT INTO SingleSchedule (user_id, title, color, date, day, start_time, end_time) VALUES
    (1, 'Alice Meeting', 1, '2025-07-28', 1, '10:00:00', '11:00:00'),
    (2, 'Bob Workshop',  2, '2025-07-29', 2, '14:00:00', '15:30:00');

-- 6) 반복 일정: RepeatSchedule + RepeatException (charlie)
INSERT INTO RepeatSchedule (user_id, title, color, start_date, end_date, repeat_days, start_time, end_time) VALUES
    (3, 'Daily Standup', 3, '2025-07-01', '2025-07-31', 127, '09:00:00', '09:15:00');
INSERT INTO RepeatException (repeat_id, exception_date) VALUES
    (LAST_INSERT_ID(), '2025-07-15');

-- 7) 친구 요청 & 친구 관계 (alice↔bob, bob↔charlie)
INSERT INTO FriendRequest (sender_id, receiver_id, status) VALUES
    (1, 2, 1),  -- pending
    (2, 3, 2);  -- accepted
INSERT INTO Friend (user_id, friend_id, is_favorite, visibility, nickname) VALUES
    (2, 1, FALSE, 'ALL', 'Alice'),
    (3, 2, TRUE,  'SIMPLE', 'Bob');

-- 8) 친구 일정 요청 (alice→diana)
INSERT INTO FriendScheduleRequest (sender_id, receiver_id, title, date, start_time, end_time, status, request_memo) VALUES
    (1, 4, 'Lunch?', '2025-07-30', '12:00:00', '13:00:00', 1, 'Join me if free');

-- 9) 그룹 생성 (alice master, bob master)
INSERT INTO `Group` (master_id, group_name, group_type, max_member, category, unique_code) VALUES
    (1, 'Study Group', 'NORMAL', 5, 1, 'STUDY123'),
    (2, 'Project Team','NORMAL', 4, 3, 'PROJ456');

-- 10) 그룹 요청 (bob invites evan to group1)
INSERT INTO GroupRequest (group_id, inviter_id, receiver_id, status) VALUES
    (1, 2, 5, 1);

-- 11) 그룹 멤버 (alice,bob,diana in group1; bob,charlie in group2)
INSERT INTO GroupMembers (group_id, user_id, is_favorite) VALUES
    (1, 1, FALSE),
    (1, 2, TRUE),
    (1, 4, FALSE),
    (2, 2, FALSE),
    (2, 3, TRUE);

-- 12) 그룹 일정 (for group1 & group2)
INSERT INTO GroupSchedule (group_id, title, color, date, day, start_time, end_time) VALUES
    (1, 'Group Study', 1, '2025-07-29', 2, '18:00:00', '20:00:00'),
    (2, 'Project Kickoff', 2, '2025-07-31', 4, '10:00:00', '12:00:00');

-- 13) 알림 (Notifications) for user5
INSERT INTO Notifications (sender_id, user_id, type, content, is_read) VALUES
    (1, 5, 1, 'Welcome to the platform!', FALSE);

-- 14) 시스템 공지 (SystemNotices)
INSERT INTO SystemNotices (title, content) VALUES
    ('시스템 점검 안내',  '7/30 02:00 ~ 04:00 시스템 점검이 예정되어 있습니다.'),
    ('새 기능 출시',      '캘린더 공유 기능이 추가되었습니다.');

SET FOREIGN_KEY_CHECKS = 1;
