package com.backend.Config;

public class GlobalEnum {

    // 1. 사용자 소속 대학교 (예시 5개)
    public enum University {
        SEOUL_NATIONAL,
        KOREA_UNIVERSITY,
        YONSEI_UNIVERSITY,
        HANYANG_UNIVERSITY,
        POSTECH
    }

    // 2. 소셜 로그인 공급자
    public enum SocialProvider {
        KAKAO,
        GOOGLE,
        NAVER
    }

    // 3-1. 색상 (일정에서 사용하는 기본 색상)
    public enum ScheduleColor {
        RED("#FF0000"),
        ORANGE("#FFA500"),
        YELLOW("#FFFF00"),
        GREEN("#008000"),
        BLUE("#0000FF"),
        INDIGO("#4B0082"),
        PURPLE("#800080");

        private final String hex;

        ScheduleColor(String hex) {
            this.hex = hex;
        }

        public String getHex() {
            return hex;
        }

        /**
         * 정수 코드를 받아서 대응되는 ScheduleColor를 반환합니다.
         * @param code 0=RED, 1=ORANGE, …, 6=PURPLE
         * @return ScheduleColor
         * @throws IllegalArgumentException 코드가 0~6 범위를 벗어나면 예외 발생
         */
        public static ScheduleColor fromCode(int code) {
            ScheduleColor[] values = ScheduleColor.values();
            if (code < 0 || code >= values.length) {
                throw new IllegalArgumentException("유효하지 않은 color 코드: " + code);
            }
            return values[code];
        }
    }

    // 3-2. 요일 (DB에서는 TINYINT로 저장, 1=Monday ~ 7=Sunday)
    public enum DayOfWeek {
        MONDAY(1),
        TUESDAY(2),
        WEDNESDAY(3),
        THURSDAY(4),
        FRIDAY(5),
        SATURDAY(6),
        SUNDAY(7);

        private final int value;

        DayOfWeek(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        // 예: 1 -> MONDAY
        public static DayOfWeek fromValue(int value) {
            for (DayOfWeek day : values()) {
                if (day.value == value) return day;
            }
            throw new IllegalArgumentException("Invalid day value: " + value);
        }
    }

    // 4. 요청 상태 (Friend, Group, Schedule 모두 사용)
    public enum RequestStatus {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    // 5. 알림 유형
    public enum NotificationType {
        FRIEND_REQUEST,
        FRIEND_SCHEDULE,
        GROUP_INVITE,
        GROUP_SCHEDULE,
        SYSTEM_NOTICE
    }

    public enum ScheduleCategory {
        NORMAL(1),
        FRIEND(2),
        TEAMPLAY(3),
        CLUB(4),
        SCHOOL(5);

        private final int code;

        ScheduleCategory(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        /** 코드 → enum 매핑 */
        public static ScheduleCategory fromCode(int code) {
            for (ScheduleCategory c : values()) {
                if (c.code == code) return c;
            }
            throw new IllegalArgumentException("유효하지 않은 ScheduleCategory 코드: " + code);
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

