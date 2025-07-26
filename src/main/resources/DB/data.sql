USE timespace;

-- Users
INSERT INTO `User` (user_name, email, password, university, phone_number, max_friend, max_group, birth_date, profile_image_url)
VALUES
    ('홍길동', 'hong@example.com', '$2a$10$hashedPwd1', 1, '010-1234-5678', 50, 10, '1990-01-01', 'https://example.com/profile1.jpg'),
    ('김철수', 'kim@example.com', '$2a$10$hashedPwd2', 2, '010-2345-6789', 50, 10, '1991-02-02', 'https://example.com/profile2.jpg');

-- SingleSchedule
INSERT INTO SingleSchedule (user_id, title, color, date, day, start_time, end_time)
VALUES
    (1, '팀 미팅', 5, '2025-07-20', 1, '09:00:00', '10:00:00');

-- RepeatSchedule
INSERT INTO RepeatSchedule (user_id, title, color, start_date, end_date, repeat_days, start_time, end_time)
VALUES
    (1, '주간 회의', 3, '2025-07-01', '2025-12-31', 5, '14:00:00', '15:00:00');

-- FriendScheduleRequest
INSERT INTO FriendScheduleRequest (sender_id, receiver_id, title, date, start_time, end_time, status, request_memo)
VALUES
    (1, 2, '같이 점심', '2025-07-21', '12:00:00', '13:00:00', 1, '점심 먹고 쇼핑해요');

-- Group & Members & Requests
INSERT INTO `Group` (master_id, group_name, group_type, max_member, category, unique_code)
VALUES
    (1, '스터디 모임', 'NORMAL', 10, 1, 'abc123xyz');

INSERT INTO GroupMembers (group_id, user_id)
VALUES
    (1, 1),
    (1, 2);

INSERT INTO GroupRequest (group_id, inviter_id, receiver_id, status)
VALUES
    (1, 1, 2, 1);

-- Notifications
INSERT INTO Notifications (sender_id, user_id, type, content, is_read)
VALUES
    (1, 2, 1, '친구 요청이 도착했습니다.', FALSE);
