package com.backend.ConfigEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class GlobalEnum {

    // 1. 사용자 소속 대학교 (예시 5개)
    @Getter
    @AllArgsConstructor
    public enum University {
        UNIV_1(1, "가야대학교"),
        UNIV_2(2, "가천대학교"),
        UNIV_3(3, "가톨릭관동대학교"),
        UNIV_4(4, "가톨릭꽃동네대학교"),
        UNIV_5(5, "가톨릭대학교"),
        UNIV_6(6, "강남대학교"),
        UNIV_7(7, "강서대학교"),
        UNIV_8(8, "강원대학교"),
        UNIV_9(9, "건국대학교"),
        UNIV_10(10, "건국대학교 GLOCAL캠퍼스"),
        UNIV_11(11, "건양대학교"),
        UNIV_12(12, "경기대학교"),
        UNIV_13(13, "경남대학교"),
        UNIV_14(14, "경동대학교"),
        UNIV_15(15, "경북대학교"),
        UNIV_16(16, "경상국립대학교"),
        UNIV_17(17, "경성대학교"),
        UNIV_18(18, "경운대학교"),
        UNIV_19(19, "경인여자대학교"),
        UNIV_20(20, "경일대학교"),
        UNIV_21(21, "경희대학교"),
        UNIV_22(22, "계명대학교"),
        UNIV_23(23, "고려대학교"),
        UNIV_24(24, "고려대학교 세종캠퍼스"),
        UNIV_25(25, "고신대학교"),
        UNIV_26(26, "광운대학교"),
        UNIV_27(27, "광주대학교"),
        UNIV_28(28, "광주여자대학교"),
        UNIV_29(29, "국립강릉원주대학교"),
        UNIV_30(30, "국립경국대학교"),
        UNIV_31(31, "국립공주대학교"),
        UNIV_32(32, "국립군산대학교"),
        UNIV_33(33, "국립금오공과대학교"),
        UNIV_34(34, "국립목포대학교"),
        UNIV_35(35, "국립목포해양대학교"),
        UNIV_36(36, "국립부경대학교"),
        UNIV_37(37, "국립순천대학교"),
        UNIV_38(38, "국립창원대학교"),
        UNIV_39(39, "국립한국교통대학교"),
        UNIV_40(40, "국립한국해양대학교"),
        UNIV_41(41, "국립한밭대학교"),
        UNIV_42(42, "국민대학교"),
        UNIV_43(43, "극동대학교"),
        UNIV_44(44, "금강대학교"),
        UNIV_45(45, "김천대학교"),
        UNIV_46(46, "나사렛대학교"),
        UNIV_47(47, "남부대학교"),
        UNIV_48(48, "남서울대학교"),
        UNIV_49(49, "단국대학교"),
        UNIV_50(50, "대구가톨릭대학교"),
        UNIV_51(51, "대구대학교"),
        UNIV_52(52, "대구예술대학교"),
        UNIV_53(53, "대구한의대학교"),
        UNIV_54(54, "대전대학교"),
        UNIV_55(55, "대진대학교"),
        UNIV_56(56, "덕성여자대학교"),
        UNIV_57(57, "동국대학교"),
        UNIV_58(58, "동국대학교 WISE캠퍼스"),
        UNIV_59(59, "동덕여자대학교"),
        UNIV_60(60, "동명대학교"),
        UNIV_61(61, "동서대학교"),
        UNIV_62(62, "동신대학교"),
        UNIV_63(63, "동아대학교"),
        UNIV_64(64, "동양대학교"),
        UNIV_65(65, "동의대학교"),
        UNIV_66(66, "루터대학교"),
        UNIV_67(67, "명지대학교"),
        UNIV_68(68, "목원대학교"),
        UNIV_69(69, "목포가톨릭대학교"),
        UNIV_70(70, "배재대학교"),
        UNIV_71(71, "배화여자대학교"),
        UNIV_72(72, "백석대학교"),
        UNIV_73(73, "부산가톨릭대학교"),
        UNIV_74(74, "부산대학교"),
        UNIV_75(75, "부산여자대학교"),
        UNIV_76(76, "부산외국어대학교"),
        UNIV_77(77, "삼육대학교"),
        UNIV_78(78, "상명대학교"),
        UNIV_79(79, "상지대학교"),
        UNIV_80(80, "서강대학교"),
        UNIV_81(81, "서경대학교"),
        UNIV_82(82, "서울과학기술대학교"),
        UNIV_83(83, "서울기독대학교"),
        UNIV_84(84, "서울대학교"),
        UNIV_85(85, "서울시립대학교"),
        UNIV_86(86, "서울여자간호대학교"),
        UNIV_87(87, "서울여자대학교"),
        UNIV_88(88, "서원대학교"),
        UNIV_89(89, "선문대학교"),
        UNIV_90(90, "성결대학교"),
        UNIV_91(91, "성공회대학교"),
        UNIV_92(92, "성균관대학교"),
        UNIV_93(93, "성신여자대학교"),
        UNIV_94(94, "세명대학교"),
        UNIV_95(95, "세종대학교"),
        UNIV_96(96, "세한대학교"),
        UNIV_97(97, "송원대학교"),
        UNIV_98(98, "수원대학교"),
        UNIV_99(99, "수원여자대학교"),
        UNIV_100(100, "숙명여자대학교"),
        UNIV_101(101, "순천향대학교"),
        UNIV_102(102, "숭실대학교"),
        UNIV_103(103, "숭의여자대학교"),
        UNIV_104(104, "신경주대학교"),
        UNIV_105(105, "신라대학교"),
        UNIV_106(106, "신한대학교"),
        UNIV_107(107, "아주대학교"),
        UNIV_108(108, "안양대학교"),
        UNIV_109(109, "연세대학교"),
        UNIV_110(110, "연세대학교 미래캠퍼스"),
        UNIV_111(111, "영남대학교"),
        UNIV_112(112, "영산대학교"),
        UNIV_113(113, "예수대학교"),
        UNIV_114(114, "예원예술대학교"),
        UNIV_115(115, "용인대학교"),
        UNIV_116(116, "우석대학교"),
        UNIV_117(117, "우송대학교"),
        UNIV_118(118, "울산대학교"),
        UNIV_119(119, "원광대학교"),
        UNIV_120(120, "위덕대학교"),
        UNIV_121(121, "유원대학교"),
        UNIV_122(122, "을지대학교"),
        UNIV_123(123, "이화여자대학교"),
        UNIV_124(124, "인제대학교"),
        UNIV_125(125, "인천대학교"),
        UNIV_126(126, "인하대학교"),
        UNIV_127(127, "전남대학교"),
        UNIV_128(128, "전북대학교"),
        UNIV_129(129, "전주대학교"),
        UNIV_130(130, "제주국제대학교"),
        UNIV_131(131, "제주대학교"),
        UNIV_132(132, "조선대학교"),
        UNIV_133(133, "중부대학교"),
        UNIV_134(134, "중앙대학교"),
        UNIV_135(135, "중원대학교"),
        UNIV_136(136, "차의과학대학교"),
        UNIV_137(137, "창신대학교"),
        UNIV_138(138, "청주대학교"),
        UNIV_139(139, "초당대학교"),
        UNIV_140(140, "총신대학교"),
        UNIV_141(141, "추계예술대학교"),
        UNIV_142(142, "충남대학교"),
        UNIV_143(143, "충북대학교"),
        UNIV_144(144, "평택대학교"),
        UNIV_145(145, "포항공과대학교"),
        UNIV_146(146, "한경국립대학교"),
        UNIV_147(147, "한국공학대학교"),
        UNIV_148(148, "한국기술교육대학교"),
        UNIV_149(149, "한국성서대학교"),
        UNIV_150(150, "한국외국어대학교"),
        UNIV_151(151, "한국항공대학교"),
        UNIV_152(152, "한남대학교"),
        UNIV_153(153, "한동대학교"),
        UNIV_154(154, "한라대학교"),
        UNIV_155(155, "한림대학교"),
        UNIV_156(156, "한서대학교"),
        UNIV_157(157, "한성대학교"),
        UNIV_158(158, "한세대학교"),
        UNIV_159(159, "한신대학교"),
        UNIV_160(160, "한양대학교"),
        UNIV_161(161, "한양대학교 ERICA캠퍼스"),
        UNIV_162(162, "한양여자대학교"),
        UNIV_163(163, "협성대학교"),
        UNIV_164(164, "호남대학교"),
        UNIV_165(165, "호서대학교"),
        UNIV_166(166, "홍익대학교"),
        UNIV_167(167, "화성의과학대학교");

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
        GROUP_MASTER             (7, "GROUP_MASTER"),
        SYSTEM_NOTICE            (8, "SYSTEM_NOTICE");

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

    public enum ProfileImageType {
        USER,  // 사용자 프로필
        GROUP  // 그룹 프로필
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

