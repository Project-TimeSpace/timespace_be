CREATE DATABASE IF NOT EXISTS timespace DEFAULT CHARACTER SET  utf8mb4 COLLATE utf8mb4_unicode_ci;
USE timespace;

CREATE TABLE `User` (
    id            BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_name     VARCHAR(50)  NOT NULL,
    email         VARCHAR(50)  NOT NULL UNIQUE,
    password      VARCHAR(100) NOT NULL,
    university    INT NOT NULL,
    phone_number  VARCHAR(20),
    max_friend    INT          NOT NULL DEFAULT 50,
    max_group     INT          NOT NULL DEFAULT 10,
    self_memo     VARCHAR(100),
    birth_date    DATE,
    profile_image_url   VARCHAR(255),
    created_at    DATETIME     DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE RefreshToken (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    token VARCHAR(500) NOT NULL,
    expiry_date DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES `User`(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE SocialAccount (
    id                 BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id            BIGINT       NOT NULL,
    provider           VARCHAR(30)  NOT NULL,  -- 'kakao', 'google', 'naver' 등
    provider_user_id   VARCHAR(100) NOT NULL,  -- 해당 소셜 공급자의 고유 사용자 ID
    connected_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX uq_social_account (provider, provider_user_id),
    FOREIGN KEY (user_id) REFERENCES `User`(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE SingleSchedule (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    title       VARCHAR(100),
    color       INT          NOT NULL,
    date        DATE         NOT NULL,
    day         INT          NOT NULL,
    start_time  TIME         NOT NULL,
    end_time    TIME         NOT NULL,
    FOREIGN KEY (user_id) REFERENCES `User`(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE RepeatSchedule (
    id             BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id        BIGINT       NOT NULL,
    title          VARCHAR(100) NOT NULL,
    color          INT          NOT NULL,
    start_date     DATE         NOT NULL,
    end_date       DATE         NOT NULL,
    repeat_days    INT          NOT NULL,
    start_time     TIME         NOT NULL,
    end_time       TIME         NOT NULL,
    FOREIGN KEY (user_id) REFERENCES `User`(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE RepeatException (
    id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
    repeat_id       BIGINT       NOT NULL,
    exception_date  DATE         NOT NULL,
    FOREIGN KEY (repeat_id) REFERENCES RepeatSchedule(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE FriendRequest (
    id            BIGINT       AUTO_INCREMENT PRIMARY KEY,
    sender_id     BIGINT       NOT NULL,
    receiver_id   BIGINT       NOT NULL,
    status        INT            NOT NULL,
    requested_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_friend_request (sender_id, receiver_id),
    FOREIGN KEY (sender_id) REFERENCES `User`(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES `User`(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE Friend (
    id           BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT       NOT NULL,
    friend_id    BIGINT       NOT NULL,
    is_favorite  BOOLEAN      NOT NULL DEFAULT FALSE,
    visibility   VARCHAR(10)  NOT NULL,
    nickname     VARCHAR(50),
    created_at   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    UNIQUE  KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES `User`(id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES `User`(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE FriendScheduleRequest (
    id            BIGINT       AUTO_INCREMENT PRIMARY KEY,
    sender_id     BIGINT       NOT NULL,
    receiver_id   BIGINT       NOT NULL,
    title         VARCHAR(100),
    date          DATE         NOT NULL,
    start_time    TIME         NOT NULL,
    end_time      TIME         NOT NULL,
    status        INT          NOT NULL,
    request_memo  VARCHAR(300),
    requested_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES `User`(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES `User`(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE `Group` (
    id           BIGINT       AUTO_INCREMENT PRIMARY KEY,
    master_id    BIGINT,
    group_name   VARCHAR(100) NOT NULL,
    group_type   VARCHAR(100) NOT NULL,
    max_member   INT          NOT NULL DEFAULT 7,
    created_at   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    category     INT NOT NULL,
    unique_code  VARCHAR(100),
    FOREIGN KEY (master_id) REFERENCES `User`(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE GroupRequest (
    id            BIGINT       AUTO_INCREMENT PRIMARY KEY,
    group_id      BIGINT       NOT NULL,
    inviter_id    BIGINT       NOT NULL,
    receiver_id   BIGINT       NOT NULL,
    status        INT          NOT NULL,
    requested_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    responded_at  DATETIME,
    UNIQUE KEY uq_group_receiver (group_id, receiver_id),
    FOREIGN KEY (group_id) REFERENCES `Group`(id) ON DELETE CASCADE,
    FOREIGN KEY (inviter_id) REFERENCES `User`(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES `User`(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE GroupMembers (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    group_id    BIGINT       NOT NULL,
    user_id     BIGINT       NOT NULL,
    is_favorite BOOLEAN      NOT NULL DEFAULT FALSE,
    UNIQUE  KEY (group_id, user_id),
    FOREIGN KEY (group_id) REFERENCES `Group`(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES `User`(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE GroupSchedule (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    group_id    BIGINT       NOT NULL,
    title       VARCHAR(100) NOT NULL,
    color       INT          NOT NULL,
    date        DATE         NOT NULL,
    day         INT          NOT NULL,
    start_time  TIME         NOT NULL,
    end_time    TIME         NOT NULL,
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES `Group`(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE Notifications (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    sender_id  BIGINT   NOT NULL,
    user_id    BIGINT   NOT NULL,
    type       INT NOT NULL,
    content    TEXT,
    is_read    BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES `User`(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES `User`(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE Admin (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    email        VARCHAR(50)  NOT NULL,
    password     VARCHAR(100) NOT NULL,
    admin_name   VARCHAR(30)
) ENGINE=InnoDB;

CREATE TABLE SystemNotices (
    id         INT       AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(100) NOT NULL,
    content    TEXT         NOT NULL,
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE VisitLog (
    id         BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    visit_date DATE         NOT NULL,
    count INT         NOT NULL DEFAULT 1,
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES `User`(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Index Definitions
CREATE INDEX idx_ss_user_date     ON SingleSchedule (user_id, date);
CREATE INDEX idx_ss_user_day      ON SingleSchedule (user_id, day);
CREATE INDEX idx_rs_user          ON RepeatSchedule (user_id);
CREATE INDEX idx_re_repeat        ON RepeatException (repeat_id);
CREATE UNIQUE INDEX uq_re_date    ON RepeatException (repeat_id, exception_date);
CREATE INDEX idx_fr_receiver      ON FriendRequest (receiver_id);
CREATE INDEX idx_pg_master        ON `Group` (master_id);
CREATE INDEX idx_gr_receiver      ON GroupRequest (receiver_id);
CREATE INDEX idx_gm_user          ON GroupMembers (user_id);
CREATE INDEX idx_gs_group_date    ON GroupSchedule (group_id, date);
CREATE INDEX idx_gs_group_day     ON GroupSchedule (group_id, day);
CREATE INDEX idx_notif_user       ON Notifications (user_id);
CREATE INDEX idx_notif_is_read    ON Notifications (is_read);
CREATE INDEX idx_fsr_receiver     ON FriendScheduleRequest (receiver_id);
CREATE UNIQUE INDEX uq_visit_date ON VisitLog (user_id, visit_date);
