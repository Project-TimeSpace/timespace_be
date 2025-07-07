package com.backend.Friend.Service;


import com.backend.ConfigEnum.GlobalEnum.RequestStatus;
import com.backend.ConfigEnum.GlobalEnum.ScheduleCategory;
import com.backend.ConfigEnum.GlobalEnum.Visibility;
import com.backend.Converge.ConvergedScheduleDto;
import com.backend.Converge.ScheduleConverge;
import com.backend.Friend.Dto.FriendScheduleRequestDto;
import com.backend.Friend.Dto.SimpleScheduleDto;
import com.backend.Friend.Entity.Friend;
import com.backend.Friend.Entity.FriendScheduleRequest;
import com.backend.Friend.Repository.FriendRepository;
import com.backend.Friend.Repository.FriendScheduleRequestRepository;
import com.backend.User.Dto.CreateSingleScheduleDto;
import com.backend.User.Dto.UserScheduleDto;
import com.backend.User.Entity.User;
import com.backend.User.Repository.UserRepository;
import com.backend.User.Service.UserRepeatScheduleService;
import com.backend.User.Service.UserSingleScheduleService;
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
    private final UserSingleScheduleService userSingleScheduleService;
    private final FriendRepository friendRepository;
    private final ScheduleConverge scheduleConverge;
    private final UserRepeatScheduleService userRepeatScheduleService;

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

        List<UserScheduleDto> combined = userSingleScheduleService.getSingleSchedulesByPeriod(friendId, startDate, endDate);
        combined.addAll(
                userRepeatScheduleService.getRepeatSchedulesByPeriod(friendId, startDate, endDate));
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
        List<UserScheduleDto> mySingles = userSingleScheduleService.getSingleSchedulesByPeriod(
                userId, dto.getDate().toString(), dto.getDate().toString());
        List<UserScheduleDto> myRepeats = userRepeatScheduleService.getRepeatSchedulesByPeriod(
                userId, dto.getDate().toString(), dto.getDate().toString());
        checkOverlap(mySingles, dto, "이미 일정이 있습니다!");
        checkOverlap(myRepeats, dto, "이미 일정이 있습니다!");

        // 4) 일정 충돌 검사: 친구
        List<UserScheduleDto> frSingles = userSingleScheduleService.getSingleSchedulesByPeriod(
                friendId, dto.getDate().toString(), dto.getDate().toString());
        List<UserScheduleDto> frRepeats = userRepeatScheduleService.getRepeatSchedulesByPeriod(
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

    @Transactional
    public void acceptScheduleRequest(Long userId, Long requestId) {
        // 1) 요청 조회
        FriendScheduleRequest req = scheduleRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요청입니다."));

        // 2) 수신자 검증
        if (!req.getReceiver().getId().equals(userId)) {
            throw new AccessDeniedException("본인이 수신자가 아닙니다.");
        }
        if (!RequestStatus.PENDING.name().equals(req.getStatus())) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }

        // 3) DTO 생성 (색상·카테고리 기본값 사용)
        CreateSingleScheduleDto dto = CreateSingleScheduleDto.builder()
                .title(req.getTitle())
                .color(1)
                .category(ScheduleCategory.FRIEND.getCode())
                .date(req.getDate())
                .day(req.getDate().getDayOfWeek().getValue())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .build();

        Long senderId   = req.getSender().getId();
        Long receiverId = req.getReceiver().getId();

        // 4) 양쪽 일정으로 저장
        userSingleScheduleService.createSingleSchedule(senderId, dto);
        userSingleScheduleService.createSingleSchedule(receiverId, dto);

        // 5) 요청 상태 업데이트
        req.setStatus(RequestStatus.ACCEPTED.name());
        scheduleRequestRepository.save(req);
    }

    @Transactional
    public void rejectScheduleRequest(Long userId, Long requestId) {
        // 1) 요청 조회
        FriendScheduleRequest req = scheduleRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요청입니다."));

        // 2) 수신자 검증
        if (!req.getReceiver().getId().equals(userId)) {
            throw new AccessDeniedException("본인이 수신자가 아닙니다.");
        }
        // 3) 상태 확인
        if (!RequestStatus.PENDING.name().equals(req.getStatus())) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }

        // 4) 상태 REJECTED로 변경 후 저장
        req.setStatus(RequestStatus.REJECTED.name());
        scheduleRequestRepository.save(req);
    }
}
