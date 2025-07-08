package com.backend.SharedFunction.Converge;

import com.backend.User.Dto.UserScheduleDto;
import com.backend.User.Entity.User;
import com.backend.User.Repository.UserRepository;
import com.backend.User.Service.UserRepeatScheduleService;
import com.backend.User.Service.UserSingleScheduleService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleConverge {

    private final UserSingleScheduleService userSingleScheduleService;
    private final UserRepository userRepository;
    private final UserRepeatScheduleService userRepeatScheduleService;

    //주어진 사용자 멤버들의 일정을 SweepLine 알고리즘으로 병합하여 반환
    public List<ConvergedScheduleDto> convergeSchedules(List<Long> members, String startDate, String endDate) {
        //long teststart = System.nanoTime();
        // 1) 초기 배열 설정
        List<ConvergedScheduleDto> initialSchedules = new ArrayList<>();

        // 2) UserScheduleService를 통해 각 사용자 일정 조회
        for (Long memberId : members) {
            User user = userRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 없습니다: " + memberId));
            ParticipantDto participant = ParticipantDto.builder()
                    .userId(user.getId())
                    .userName(user.getUserName())
                    .email(user.getEmail())
                    .build();

            List<UserScheduleDto> singles = userSingleScheduleService.getSingleSchedulesByPeriod(memberId, startDate, endDate);
            List<UserScheduleDto> repeats = userRepeatScheduleService.getRepeatSchedulesByPeriod(memberId, startDate, endDate);

            // 단일 일정 추가
            for (UserScheduleDto s : singles) {
                initialSchedules.add(ConvergedScheduleDto.builder()
                        .date(s.getDate())
                        .day(s.getDay())
                        .startTime(s.getStartTime())
                        .endTime(s.getEndTime())
                        .users(Collections.singletonList(participant))
                        .count(1)
                        .build());
            }
            // 반복 일정 추가
            for (UserScheduleDto s : repeats) {
                initialSchedules.add(ConvergedScheduleDto.builder()
                        .date(s.getDate())
                        .day(s.getDay())
                        .startTime(s.getStartTime())
                        .endTime(s.getEndTime())
                        .users(Collections.singletonList(participant))
                        .count(1)
                        .build());
            }
        }

        /*
        for (ConvergedScheduleDto sch : initialSchedules) {
            System.out.println(sch);
        }
        */

        // SweepLine 병합 로직
        // 1) 그리드 초기화: [days][144][list of participants]
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end   = LocalDate.parse(endDate);
        int days = (int) ChronoUnit.DAYS.between(start, end) + 1;
        int slotsPerDay = 24 * 6;

        // starts, ends 그리드 생성
        List<List<List<ParticipantDto>>> starts = new ArrayList<>(days);
        List<List<List<ParticipantDto>>> ends   = new ArrayList<>(days);
        for (int d = 0; d < days; d++) {
            List<List<ParticipantDto>> dayStarts = new ArrayList<>(slotsPerDay);
            List<List<ParticipantDto>> dayEnds   = new ArrayList<>(slotsPerDay);
            for (int t = 0; t < slotsPerDay; t++) {
                dayStarts.add(new ArrayList<>());
                dayEnds.add(new ArrayList<>());
            }
            starts.add(dayStarts);
            ends.add(dayEnds);
        }

        // 2) initialSchedules 순회하며 이벤트 분리 마킹
        for (ConvergedScheduleDto sch : initialSchedules) {
            int dayIndex = (int) ChronoUnit.DAYS.between(start, sch.getDate());
            int startIdx = sch.getStartTime().getHour() * 6 + sch.getStartTime().getMinute() / 10;
            int endIdx   = sch.getEndTime().getHour()   * 6 + sch.getEndTime().getMinute()   / 10;
            ParticipantDto member = sch.getUsers().get(0);

            starts.get(dayIndex).get(startIdx).add(member);  // + 이벤트
            ends  .get(dayIndex).get(endIdx)  .add(member);  // − 이벤트
        }

        /*
        System.out.println("========================");
        for (int d = 0; d < days; d++) {
            LocalDate date = start.plusDays(d);
            System.out.println("=== Date: " + date + " ===");
            for (int t = 0; t < 10; t++) {  // 슬롯 0~9 까지만
                List<ParticipantDto> s = starts.get(d).get(t);
                List<ParticipantDto> e = ends  .get(d).get(t);
                System.out.printf("slot %2d: starts=%s, ends=%s%n",
                        t,
                        s.stream().map(ParticipantDto::getUserName).toList(),
                        e.stream().map(ParticipantDto::getUserName).toList()
                );
            }
        }
        System.out.println("========================");
        */

        // 3) 병합 로직
        List<ConvergedScheduleDto> result = new ArrayList<>(); // 결과는 sort 없어도 시간순 정렬

        // 하루 단위로 반복
        for (int d = 0; d < days; d++) {
            LocalDate date = start.plusDays(d);
            Set<ParticipantDto> active = new LinkedHashSet<>();
            int prevIndex = -1;

            // t는 0부터 slotsPerDay까지 순회
            for (int t = 0; t <= slotsPerDay; t++) {
                // 이벤트 확인
                boolean isEvent = (t < slotsPerDay) && (!starts.get(d).get(t).isEmpty() || !ends.get(d).get(t).isEmpty());

                if (isEvent) {
                    // 1) 이전 세그먼트가 열려 있었다면 닫아준다
                    if (prevIndex != -1) {
                        LocalTime segStart = LocalTime.of(prevIndex / 6, (prevIndex % 6) * 10);
                        LocalTime segEnd   = LocalTime.of(t / 6,           (t % 6) * 10);
                        System.out.println("segmentStart=" + segStart + ", segmentEnd=" + segEnd);

                        result.add(ConvergedScheduleDto.builder()
                                .date(date)
                                .day(date.getDayOfWeek().getValue())
                                .startTime(segStart)
                                .endTime(segEnd)
                                .users(new ArrayList<>(active))
                                .count(active.size())
                                .build()
                        );
                    }

                    // 2) 이벤트 적용: 종료(remove) → 시작(add)
                    active.removeAll(ends.get(d).get(t));
                    active.addAll(starts.get(d).get(t));

                    // 3) 활성화 상태 갱신
                    prevIndex = active.isEmpty() ? -1 : t;
                }
            }
        }

        /*
        for (ConvergedScheduleDto sch : result) {
            System.out.println(sch);
        }
        */
        //long testend   = System.nanoTime();
        //System.out.printf("convergeSchedules took %d μs%n", (testend - teststart) / 1_000);
        // 리턴 할 때 날짜, 시간 순서의 data가 전달됨
        return result;
    }

}
