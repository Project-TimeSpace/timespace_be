package com.backend.Friend.Service;


import com.backend.Config.GlobalEnum.RequestStatus;
import com.backend.Config.GlobalEnum.Visibility;
import com.backend.Converge.ConvergedScheduleDto;
import com.backend.Converge.ScheduleConverge;
import com.backend.Friend.Dto.FriendScheduleRequestDto;
import com.backend.Friend.Dto.SimpleScheduleDto;
import com.backend.Friend.Entity.Friend;
import com.backend.Friend.Entity.FriendScheduleRequest;
import com.backend.Friend.Repository.FriendRepository;
import com.backend.Friend.Repository.FriendScheduleRequestRepository;
import com.backend.User.Dto.UserScheduleDto;
import com.backend.User.Entity.User;
import com.backend.User.Repository.UserRepository;
import com.backend.User.Service.UserScheduleService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FriendScheduleService {

    private final UserRepository userRepository;
    private final FriendScheduleRequestRepository scheduleRequestRepository;
    private final UserScheduleService userScheduleService;
    private final FriendRepository friendRepository;
    private final ScheduleConverge scheduleConverge;

    public Friend getFriendRelationOrThrow(Long userId, Long friendId) {
        return friendRepository.findByUserIdAndFriendId(userId, friendId)
                .orElseThrow(() -> new IllegalArgumentException("친구 관계가 아닙니다."));
    }

    // 4. 친구 캘린더 조회
    @Transactional(readOnly = true)
    public Object getFriendCalendar(Long userId, Long friendId, String startDate, String endDate) {
        Friend relation = getFriendRelationOrThrow(userId, friendId);
        Visibility visibility = relation.getVisibility();

        if(visibility.equals(Visibility.SECRET)){
            throw new AccessDeniedException("이 사람의 캘린더는 비공개 설정입니다.");
        }

        List<UserScheduleDto> combined = userScheduleService.getSingleSchedulesByPeriod(friendId, startDate, endDate);
        combined.addAll(userScheduleService.getRepeatSchedulesByPeriod(friendId, startDate, endDate));
        if (visibility.equals(Visibility.SECRET)) {
            return combined.stream()
                    .map(s -> SimpleScheduleDto.builder()
                            .date(s.getDate())
                            .day(s.getDay())
                            .startTime(s.getStartTime())
                            .endTime(s.getEndTime())
                            .build()
                    ).collect(Collectors.toList());
        }
        return combined;
    }

    // 친구 캘린더와 내 캘린더 병합해서 보여주기.
    @Transactional(readOnly = true)
    public List<ConvergedScheduleDto> getMergedCalendar(
            Long userId, Long friendId, String startDate, String endDate) {

        // 1) 친구 관계 확인
        friendRepository.findByUserIdAndFriendId(userId, friendId)
                .orElseThrow(() -> new IllegalArgumentException("친구 관계가 아닙니다: "));

        List<Long> friendIds = new ArrayList<>();
        friendIds.add(userId); friendIds.add(friendId);
        // 2) Sweep-Line 병합 알고리즘 호출
        return scheduleConverge.convergeSchedules(friendIds, startDate, endDate);
    }

    // 5. 약속 신청하기
    @Transactional
    public void sendScheduleRequest(Long userId, Long friendId, FriendScheduleRequestDto dto) {
        // (1) 요청자/수신자 검증
        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        User receiver = userRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("요청 대상 친구를 찾을 수 없습니다."));

        // 2) 친구 관계 검증
        friendRepository.findByUserIdAndFriendId(userId, friendId)
                .orElseThrow(() -> new IllegalArgumentException("친구 관계가 아닙니다."));

        // 3) 일정 충돌 검사: 본인
        List<UserScheduleDto> mySingles = userScheduleService.getSingleSchedulesByPeriod(
                userId, dto.getDate().toString(), dto.getDate().toString());
        List<UserScheduleDto> myRepeats = userScheduleService.getRepeatSchedulesByPeriod(
                userId, dto.getDate().toString(), dto.getDate().toString());
        checkOverlap(mySingles, dto, "이미 일정이 있습니다!");
        checkOverlap(myRepeats, dto, "이미 일정이 있습니다!");

        // 4) 일정 충돌 검사: 친구
        List<UserScheduleDto> frSingles = userScheduleService.getSingleSchedulesByPeriod(
                friendId, dto.getDate().toString(), dto.getDate().toString());
        List<UserScheduleDto> frRepeats = userScheduleService.getRepeatSchedulesByPeriod(
                friendId, dto.getDate().toString(), dto.getDate().toString());
        checkOverlap(frSingles, dto, "친구는 해당 시간대에 일정이 있습니다.");
        checkOverlap(frRepeats, dto, "친구는 해당 시간대에 일정이 있습니다.");


        // 5) 엔티티 생성 및 저장
        FriendScheduleRequest req = FriendScheduleRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .title(dto.getTitle())
                .date(dto.getDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .status(RequestStatus.PENDING.name())   // 초기 상태는 PENDING
                .build();

        scheduleRequestRepository.save(req);
    }

    // 공통 충돌 검사 헬퍼
    private void checkOverlap(
            List<UserScheduleDto> schedules,
            FriendScheduleRequestDto req,
            String errMsg) {

        for (UserScheduleDto s : schedules) {
            // 두 구간이 겹치는 조건: req.start < s.end && s.start < req.end
            if (req.getStartTime().isBefore(s.getEndTime()) &&
                    s.getStartTime().isBefore(req.getEndTime())) {
                throw new IllegalArgumentException(errMsg);
            }
        }
    }
}
