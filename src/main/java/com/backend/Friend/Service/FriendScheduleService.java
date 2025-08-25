package com.backend.Friend.Service;


import com.backend.ConfigEnum.GlobalEnum.NotificationType;
import com.backend.ConfigEnum.GlobalEnum.RequestStatus;
import com.backend.ConfigEnum.GlobalEnum.ScheduleColor;
import com.backend.ConfigEnum.GlobalEnum.Visibility;
import com.backend.Friend.Dto.ScheduleRequestDto;
import com.backend.shared.Converge.ConvergedScheduleDto;
import com.backend.shared.Converge.ScheduleConverge;
import com.backend.Friend.Dto.FriendScheduleRequestDto;
import com.backend.Friend.Entity.Friend;
import com.backend.Friend.Entity.FriendScheduleRequest;
import com.backend.Friend.Repository.FriendRepository;
import com.backend.Friend.Repository.FriendScheduleRequestRepository;
import com.backend.Notification.Service.NotificationService;
import com.backend.shared.SharedFunction;
import com.backend.User.Dto.CreateSingleScheduleDto;
import com.backend.User.Dto.UserScheduleDto;
import com.backend.User.Entity.User;
import com.backend.User.Repository.UserRepository;
import com.backend.User.Service.UserRepeatScheduleService;
import com.backend.User.Service.UserSingleScheduleService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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
    private final NotificationService notificationService;
    private final SharedFunction sharedFunction;

    public Friend getFriendRelationOrThrow(Long userId, Long friendId) {
        return friendRepository.findByUserIdAndFriendId(userId, friendId)
                .orElseThrow(() -> new IllegalArgumentException("친구 관계가 아닙니다."));
    }

    // 4. 친구 캘린더 조회
    @Transactional(readOnly = true)
    public Object getFriendCalendar(Long userId, Long friendId, String startDate, String endDate) {
        Friend relation = getFriendRelationOrThrow(friendId,userId);
        Visibility visibility = relation.getVisibility();

        if(visibility.equals(Visibility.SECRET)){
            throw new AccessDeniedException("이 사람의 캘린더는 비공개 설정입니다.");
        }

        // All, SIMPLE은 데이터 조회
        // DTO 한가지로 만들고, 대신 불필요 데이터 null처리
        List<UserScheduleDto> combined = userSingleScheduleService.getSingleSchedulesByPeriod(friendId, startDate, endDate);
        combined.addAll(userRepeatScheduleService.getRepeatSchedulesByPeriod(friendId, startDate, endDate));

        if (visibility == Visibility.SIMPLE) {
            return combined.stream()
                    .map(s -> UserScheduleDto.builder()
                            .color(ScheduleColor.GRAY.getCode())
                            .date(s.getDate())
                            .day(s.getDay())
                            .startTime(s.getStartTime())
                            .endTime(s.getEndTime())
                            .build()
                    )
                    .collect(Collectors.toList());
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

        // 3) 일정 충돌 검사: 본인, 친구 -> SharedFunction으로 해결하게 리팩토링
        // validateSingleScheduleOverlap 내부에서 단일&반복 일정 충돌을 모두 처리
        sharedFunction.validateSingleScheduleOverlap(userId, dto.getDate(), dto.getStartTime(), dto.getEndTime(), 0L);
        sharedFunction.validateSingleScheduleOverlap(friendId, dto.getDate(), dto.getStartTime(), dto.getEndTime(), 0L);

        // 4) 엔티티 생성 및 저장
        FriendScheduleRequest req = FriendScheduleRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .title(dto.getTitle())
                .requestMemo(dto.getRequestMemo())
                .date(dto.getDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .status(RequestStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();

        scheduleRequestRepository.save(req);

        // 5) 알림 생성
        String content = String.format("%s님이 %s에 \"%s\" 약속을 신청했습니다.",
                sender.getUserName(), dto.getDate().toString(), dto.getTitle());
        notificationService.createNotification(
                userId, friendId, NotificationType.FRIEND_SCHEDULE_REQUEST, content, req.getId());
    }

    @Transactional(readOnly = true)
    public List<ScheduleRequestDto> getReceivedScheduleRequests(Long recipientId) {
        return scheduleRequestRepository.findByReceiver_Id(recipientId).stream()
                .map(req -> ScheduleRequestDto.builder()
                        .requestId(req.getId())
                        .senderId(req.getSender().getId())
                        .senderName(req.getSender().getUserName())
                        .title(req.getTitle())
                        .requestMemo(req.getRequestMemo())
                        .scheduleDate(req.getDate())
                        .startTime(req.getStartTime())
                        .endTime(req.getEndTime())
                        .status(String.valueOf(req.getStatus()))
                        .requestedAt(req.getRequestedAt())
                        .build()
                )
                .collect(Collectors.toList());
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
        if (!RequestStatus.PENDING.equals(req.getStatus())) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }

        // 3) DTO 생성 (색상·카테고리 기본값 사용)
        CreateSingleScheduleDto dto = CreateSingleScheduleDto.builder()
                .title(req.getTitle())
                .color(1)
                .date(req.getDate())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .build();

        Long senderId   = req.getSender().getId();
        Long receiverId = req.getReceiver().getId();

        // 4) 양쪽 일정으로 저장
        userSingleScheduleService.createSingleSchedule(senderId, dto);
        userSingleScheduleService.createSingleSchedule(receiverId, dto);

        // 5) 요청 상태 업데이트
        //req.setStatus(RequestStatus.ACCEPTED);
        //scheduleRequestRepository.save(req);

        // 6) 알림 생성 (요청자에게 수락 알림)
        String content = String.format("%s님이 %s에 \"%s\" 약속 요청을 수락했습니다.",
                req.getReceiver().getUserName(), req.getDate(), req.getTitle());
        notificationService.createNotification(receiverId, senderId,
                NotificationType.FRIEND_SCHEDULE_ACCEPTED, content, (long)-1);

        scheduleRequestRepository.delete(req);
    }

    @Transactional
    public void rejectScheduleRequest(Long userId, Long requestId) {
        // 1) 요청 조회
        FriendScheduleRequest req = scheduleRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 요청입니다."));

        // 2) 수신자 검증
        if (!req.getReceiver().getId().equals(userId)) {
            throw new AccessDeniedException("본인이 수신자가 아닙니다.");
        }
        // 3) 상태 확인
        if (!RequestStatus.PENDING.equals(req.getStatus())) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }

        Long senderId   = req.getSender().getId();
        Long receiverId = req.getReceiver().getId();

        String content = String.format(
                "%s님이 %s에 \"%s\" 약속 요청을 거절했습니다.",
                req.getReceiver().getUserName(),
                req.getDate(),
                req.getTitle()
        );
        notificationService.createNotification(receiverId, senderId,
                NotificationType.FRIEND_SCHEDULE_REJECTED, content, (long)-1);

        scheduleRequestRepository.delete(req);
    }
}
