package com.backend.ConfigEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class GlobalEnum {

    // 1. 사용자 소속 대학교 (예시 5개)
    @Getter
    @AllArgsConstructor
    public enum University {
        SEOUL_NATIONAL(1, "서울대학교"),
        YONSEI(2, "연세대학교"),
        KOREA(3, "고려대학교"),
        HANYANG(4, "한양대학교"),
        SUNGKYUNKWAN(5, "성균관대학교");

        private final int code;
        private final String displayName;

        // Code로 University 조회
        public static University fromCode(int code) {
            for (University university : University.values()) {
                if (university.getCode() == code) {
                    return university;
                }
            }
            throw new IllegalArgumentException("유효하지 않은 대학 코드: " + code);
        }
    }

    // 2. 색상 (일정에서 사용하는 기본 색상)
    @Getter
    @AllArgsConstructor
    public enum ScheduleColor {
        BLUE    (1,  "#4286F5"),
        RED     (2,  "#D44245"),
        PINK    (3,  "#F27198"),
        ORANGE  (4,  "#EB9E5A"),
        YELLOW  (5,  "#FCCB05"),
        GREEN   (6,  "#69B054"),
        SEIGE     (7,  "#5FC59D"),
        SKYBLUE  (8, "#63D1D2"),
        INDIGO    (9,  "#80AAE8"),
        DARKBLUE (10, "#4D7ADF"),
        PURPLE    (11,  "#800080"),
        GRAY     (12, "#A9A9A9");

        private final int code;
        private final String hex;

        public static ScheduleColor fromCode(int code) {
            for (ScheduleColor c : values()) {
                if (c.getCode() == code) {
                    return c;
                }
            }
            throw new IllegalArgumentException("유효하지 않은 color 코드: " + code);
        }
    }

    // 3 요일 (DB에서는 TINYINT로 저장, 1=Monday ~ 7=Sunday)
    @Getter
    @AllArgsConstructor
    public enum DayOfWeek {
        MONDAY(1),
        TUESDAY(2),
        WEDNESDAY(3),
        THURSDAY(4),
        FRIDAY(5),
        SATURDAY(6),
        SUNDAY(7);

        private final int value;

        // 예: 1 -> MONDAY
        public static DayOfWeek fromValue(int value) {
            for (DayOfWeek day : values()) {
                if (day.value == value) return day;
            }
            throw new IllegalArgumentException("Invalid day value: " + value);
        }
    }

    // 4. 그룹 타입 (String 매핑)
    @Getter
    @AllArgsConstructor
    public enum GroupCategory {
        NORMAL (1, "일반"),
        FRIEND (2, "친구"),
        PROJECT(3, "프로젝트"),
        CLUB   (4, "동아리"),
        MEETING(5, "회의");

        private final int code;
        private final String displayName;

        public static GroupCategory fromCode(int code) {
            for (GroupCategory c : values()) {
                if (c.getCode() == code) {
                    return c;
                }
            }
            throw new IllegalArgumentException("유효하지 않은 GroupCategory 코드: " + code);
        }
    }

    // 2. 소셜 로그인 공급자
    public enum SocialProvider {
        KAKAO,
        GOOGLE,
        NAVER
    }
    // 4. 요청 상태 (Friend, Group, Schedule 모두 사용)
    @Getter
    @AllArgsConstructor
    public enum RequestStatus {
        PENDING (1, "대기"),
        ACCEPTED(2, "수락"),
        REJECTED(3, "거절");

        private final int code;
        private final String displayName;

        /** DB의 int 코드를 받아서 대응되는 RequestStatus를 반환 */
        public static RequestStatus fromCode(int code) {
            for (RequestStatus s : values()) {
                if (s.getCode() == code) return s;
            }
            throw new IllegalArgumentException("유효하지 않은 RequestStatus 코드: " + code);
        }
    }

    // 5. 알림 유형
    @Getter
    @AllArgsConstructor
    public enum NotificationType {
        FRIEND_REQUEST           (1, "FRIEND_REQUEST"),
        FRIEND_SCHEDULE_REQUEST  (2, "FRIEND_SCHEDULE_REQUEST"),
        FRIEND_SCHEDULE_ACCEPTED (3, "FRIEND_SCHEDULE_ACCEPTED"),
        FRIEND_SCHEDULE_REJECTED (4, "FRIEND_SCHEDULE_REJECTED"),
        GROUP_INVITE             (5, "GROUP_INVITE"),
        GROUP_SCHEDULE           (6, "GROUP_SCHEDULE"),
        SYSTEM_NOTICE            (7, "SYSTEM_NOTICE"),
        GROUP_MASTER             (8, "GRUOP_MASTER");

        private final int code;
        private final String name;

        /** DB int → enum */
        public static NotificationType fromCode(int code) {
            for (NotificationType t : values()) {
                if (t.getCode() == code) return t;
            }
            throw new IllegalArgumentException("유효하지 않은 NotificationType 코드: " + code);
        }

        /** 문자열 name → enum */
        public static NotificationType fromName(String name) {
            for (NotificationType t : values()) {
                if (t.getName().equalsIgnoreCase(name)) return t;
            }
            throw new IllegalArgumentException("유효하지 않은 NotificationType name: " + name);
        }
    }


    public enum Visibility {
        ALL,     // 전체 일정 공개
        SIMPLE,  // 날짜와 시간만 간략 공개
        SECRET   // 비공개
    }

    public enum SortOption {
        NAME_ASC,       // 닉네임 가나다순
        NAME_DESC,      // 닉네임 역순
        CREATED_ASC,    // 친구 맺은 순서: 오래된 순서
        CREATED_DESC    // 친구 맺은 순서: 최신 순서
    }
}

