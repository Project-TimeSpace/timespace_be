package com.backend.Converge;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.backend.User.Service.UserScheduleService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.backend.Config.GlobalEnum.ScheduleCategory;
import com.backend.User.Dto.UserScheduleDto;
import com.backend.User.Entity.User;
import com.backend.User.Repository.UserRepository;

import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduleConvergeTest {

    @Mock
    private UserScheduleService scheduleService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ScheduleConverge sc;


    @Test
    void 두_사용자_일정이_겹치는_경우_정상병합된다() {
        String START = "2025-06-01";
        String END   = "2025-06-01";
        // 1) 사용자 정보 스텁
        when(userRepository.findById(1L)).thenReturn(Optional.of(
                User.builder()
                        .id(1L)
                        .userName("Alice")
                        .email("alice@x.com")
                        .password("pass")
                        .university("Univ")
                        .major("CS")
                        .phoneNumber("010-0000-0001")
                        .kakaoId("alice_kakao")
                        .maxFriend(100)
                        .maxGroup(50)
                        .selfMemo("")
                        .createdAt(LocalDateTime.now())
                        .build()
        ));
        when(userRepository.findById(2L)).thenReturn(Optional.of(
                User.builder()
                        .id(2L)
                        .userName("Bob")
                        .email("bob@x.com")
                        .password("pass")
                        .university("Univ")
                        .major("CS")
                        .phoneNumber("010-0000-0002")
                        .kakaoId("bob_kakao")
                        .maxFriend(100)
                        .maxGroup(50)
                        .selfMemo("")
                        .createdAt(LocalDateTime.now())
                        .build()
        ));

        // 2) 더미 일정 생성
        UserScheduleDto aliceDto = UserScheduleDto.builder()
                .id(100L)
                .isRepeat(false)
                .title("AliceSchedule")
                .day(1)
                .category(ScheduleCategory.NORMAL)
                .date(LocalDate.of(2025, 6, 1))
                .startTime(LocalTime.of(0, 0))
                .endTime(LocalTime.of(0, 20))
                .build();

        UserScheduleDto bobDto = UserScheduleDto.builder()
                .id(200L)
                .isRepeat(false)
                .title("BobSchedule")
                .day(1)
                .category(ScheduleCategory.NORMAL)
                .date(LocalDate.of(2025, 6, 1))
                .startTime(LocalTime.of(0, 10))
                .endTime(LocalTime.of(0, 30))
                .build();

        // 3) scheduleService 스텁 설정
        when(scheduleService.getSingleSchedulesByPeriod(1L, START, END))
                .thenReturn(List.of(aliceDto));
        when(scheduleService.getRepeatSchedulesByPeriod(1L, START, END))
                .thenReturn(List.of());
        when(scheduleService.getSingleSchedulesByPeriod(2L, START, END))
                .thenReturn(List.of(bobDto));
        when(scheduleService.getRepeatSchedulesByPeriod(2L, START, END))
                .thenReturn(List.of());

        // 4) 알고리즘 실행
        List<ConvergedScheduleDto> merged =
                sc.convergeSchedules(List.of(1L, 2L), START, END);

        // 5) 결과 검증
        assertEquals(3, merged.size(), "병합된 구간의 개수");

        // [00:00~00:10] → Alice
        var seg1 = merged.get(0);
        assertEquals(LocalTime.of(0, 0),  seg1.getStartTime());
        assertEquals(LocalTime.of(0, 10), seg1.getEndTime());
        assertEquals(1, seg1.getUsers().size());
        assertTrue(seg1.getUsers().stream()
                .anyMatch(u -> "Alice".equals(u.getUserName()))
        );

        // [00:10~00:20] → Alice + Bob
        var seg2 = merged.get(1);
        assertEquals(LocalTime.of(0, 10), seg2.getStartTime());
        assertEquals(LocalTime.of(0, 20), seg2.getEndTime());
        assertEquals(2, seg2.getUsers().size());

        // [00:20~00:30] → Bob
        var seg3 = merged.get(2);
        assertEquals(LocalTime.of(0, 20), seg3.getStartTime());
        assertEquals(LocalTime.of(0, 30), seg3.getEndTime());
        assertEquals(1, seg3.getUsers().size());
        assertTrue(seg3.getUsers().stream()
                .anyMatch(u -> "Bob".equals(u.getUserName()))
        );
    }

    @Test
    void 다섯_사용자_각_다섯_개_일정_병합된다() {

        String START = "2025-06-01";
        String END   = "2025-06-01";
        // 1) 사용자 정보 스텁
        for (long userId = 1; userId <= 5; userId++) {
            when(userRepository.findById(userId)).thenReturn(Optional.of(
                    User.builder()
                            .id(userId)
                            .userName("User" + userId)
                            .email("user" + userId + "@x.com")
                            .password("pass")
                            .university("Univ")
                            .major("Major")
                            .phoneNumber("010-0000-000" + userId)
                            .kakaoId("kakao" + userId)
                            .maxFriend(100)
                            .maxGroup(50)
                            .selfMemo("")
                            .createdAt(LocalDateTime.now())
                            .build()
            ));
        }

        // 2) 더미 일정 생성 (각 사용자 동일)
        List<UserScheduleDto> schedules = new ArrayList<>();
        for (int k = 0; k < 5; k++) {
            schedules.add(UserScheduleDto.builder()
                    .id((long)(100 + k))
                    .isRepeat(false)
                    .title("Schedule" + k)
                    .day(1)
                    .category(ScheduleCategory.NORMAL)
                    .date(LocalDate.of(2025, 6, 1))
                    .startTime(LocalTime.of(k * 2, 0))
                    .endTime(LocalTime.of(k * 2 + 1, 0))
                    .build()
            );
        }

        // 3) scheduleService 스텁 설정
        for (long userId = 1; userId <= 5; userId++) {
            when(scheduleService.getSingleSchedulesByPeriod(userId, START, END))
                    .thenReturn(schedules);
            when(scheduleService.getRepeatSchedulesByPeriod(userId, START, END))
                    .thenReturn(List.of());
        }

        // 4) 알고리즘 실행
        List<ConvergedScheduleDto> merged =
                sc.convergeSchedules(List.of(1L, 2L, 3L, 4L, 5L), START, END);

        // 5) 검증: 5개 세그먼트, 각 세그먼트 5명 참여
        assertEquals(5, merged.size(), "총 병합 세그먼트 수");
        for (int i = 0; i < 5; i++) {
            var seg = merged.get(i);
            assertEquals(5, seg.getUsers().size(), "세그먼트 " + i + " 참여자 수");
            assertEquals(5, seg.getCount(),         "세그먼트 " + i + " count 필드");
            assertEquals(LocalTime.of(i * 2, 0), seg.getStartTime());
            assertEquals(LocalTime.of(i * 2 + 1, 0), seg.getEndTime());
        }
    }

    @Test
    void 랜덤_5명_5개씩_일주일_일정_병합된다() {
        String START = "2025-06-01";
        String END   = "2025-06-07";

        // 고정된 시드로 테스트의 일관성 유지
        Random rnd = new Random(12345);

        // 1) 5명의 더미 사용자 세팅
        for (long userId = 1; userId <= 5; userId++) {
            when(userRepository.findById(userId)).thenReturn(Optional.of(
                    User.builder()
                            .id(userId)
                            .userName("User" + userId)
                            .email("user" + userId + "@x.com")
                            .password("pass")
                            .university("Univ")
                            .major("Major")
                            .phoneNumber("010-0000-000" + userId)
                            .kakaoId("kakao" + userId)
                            .maxFriend(100)
                            .maxGroup(50)
                            .selfMemo("")
                            .createdAt(LocalDateTime.now())
                            .build()
            ));
        }

        // 2) 각 사용자별 5개 일정 생성 (2025-06-01 ~ 2025-06-07)
        Map<Long, List<UserScheduleDto>> schedulesByUser = new HashMap<>();
        LocalDate baseDate = LocalDate.of(2025, 6, 1);
        for (long userId = 1; userId <= 5; userId++) {
            List<UserScheduleDto> list = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                // 날짜 랜덤: 0~6일차
                LocalDate date = baseDate.plusDays(rnd.nextInt(7));
                // 시작 시간 랜덤 (0~23시)
                int hour = rnd.nextInt(24);
                // 지속시간 랜덤 (1~3시간)
                int dur = rnd.nextInt(3) + 1;
                LocalTime start = LocalTime.of(hour, 0);
                LocalTime end   = start.plusHours(dur);
                list.add(UserScheduleDto.builder()
                        .id(userId * 100 + i)
                        .isRepeat(false)
                        .title("U" + userId + "_S" + i)
                        .day(date.getDayOfWeek().getValue())
                        .category(ScheduleCategory.NORMAL)
                        .date(date)
                        .startTime(start)
                        .endTime(end)
                        .build()
                );
            }
            schedulesByUser.put(userId, list);
        }

        // 3) scheduleService 에 스텁 등록
        for (long userId = 1; userId <= 5; userId++) {
            when(scheduleService.getSingleSchedulesByPeriod(userId, START, END))
                    .thenReturn(schedulesByUser.get(userId));
            when(scheduleService.getRepeatSchedulesByPeriod(userId, START, END))
                    .thenReturn(List.of());
        }

        // (옵션) 생성된 더미 일정 로그
        schedulesByUser.forEach((uid, list) -> {
            System.out.println("=== User " + uid + " Schedules ===");
            list.forEach(s -> System.out.println(
                    s.getDate() + " " + s.getStartTime() + "~" + s.getEndTime()));
        });

        // 4) 알고리즘 실행 & 결과 검증
        var merged = sc.convergeSchedules(List.of(1L,2L,3L,4L,5L), START, END);
        assertFalse(merged.isEmpty(), "병합 결과가 비어 있으면 안됩니다");
        // 추가 검증 로직은 필요에 따라 더 작성하세요.
    }
}
