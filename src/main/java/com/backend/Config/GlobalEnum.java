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
}

