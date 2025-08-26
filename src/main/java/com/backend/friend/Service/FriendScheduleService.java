package com.backend.friend.Service;


import com.backend.configenum.GlobalEnum;
import com.backend.configenum.GlobalEnum.NotificationType;
import com.backend.configenum.GlobalEnum.RequestStatus;
import com.backend.configenum.GlobalEnum.ScheduleColor;
import com.backend.configenum.GlobalEnum.Visibility;
import com.backend.friend.Dto.ScheduleRequestDto;
import com.backend.friend.error.FriendErrorCode;
import com.backend.response.BusinessException;
import com.backend.shared.Converge.ConvergedScheduleDto;
import com.backend.shared.Converge.ScheduleConverge;
import com.backend.friend.Dto.FriendScheduleRequestDto;
import com.backend.friend.Entity.Friend;
import com.backend.friend.Entity.FriendScheduleRequest;
import com.backend.friend.Repository.FriendRepository;
import com.backend.friend.Repository.FriendScheduleRequestRepository;
import com.backend.Notification.Service.NotificationService;
import com.backend.shared.SharedFunction;
import com.backend.user.Dto.CreateSingleScheduleDto;
import com.backend.user.Dto.UserScheduleDto;
import com.backend.user.Entity.User;
import com.backend.user.Repository.UserRepository;
import com.backend.user.Service.UserRepeatScheduleService;
import com.backend.user.Service.UserService;
import com.backend.user.Service.UserSingleScheduleService;

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

    private final FriendScheduleRequestRepository scheduleRequestRepository;
    private final FriendRepository friendRepository;
    private final FriendService friendService;

    private final UserService userService;
    private final UserRepository userRepository;
    private final ScheduleConverge scheduleConverge;
    private final UserSingleScheduleService userSingleScheduleService;
    private final UserRepeatScheduleService userRepeatScheduleService;
    private final NotificationService notificationService;
    private final SharedFunction sharedFunction;

    private Friend getFriendRelationOrThrow(Long userId, Long friendId) {
        return friendRepository.findByUser_IdAndFriend_Id(userId, friendId)
                .orElseThrow(() -> new BusinessException(FriendErrorCode.RELATION_NOT_FOUND));
    }
    private FriendScheduleRequest getScheduleRequestOrThrow(Long requestId) {
        return scheduleRequestRepository.findById(requestId)
            .orElseThrow(() -> new BusinessException(FriendErrorCode.REQUEST_NOT_FOUND));
    }

    // 4. 친구 캘린더 조회
    @Transactional(readOnly = true)
    public Object getFriendCalendar(Long userId, Long friendId, String startDate, String endDate) {
        Friend relation = getFriendRelationOrThrow(friendId,userId);
        Visibility visibility = relation.getVisibility();

        if (visibility.equals(Visibility.SECRET)) {
            throw new BusinessException(FriendErrorCode.CALENDAR_PRIVATE);
        }

        List<UserScheduleDto> combined = userSingleScheduleService.getSingleSchedulesByPeriod(friendId, startDate, endDate);
        combined.addAll(userRepeatScheduleService.getRepeatSchedulesByPeriod(friendId, startDate, endDate));

        if (visibility == Visibility.SIMPLE) {
            return combined.stream()
                .map(UserScheduleDto::toSimpleVisibility)
                .toList();
        }
        return combined;
    }

    @Transactional(readOnly = true)
    public List<ConvergedScheduleDto> getMergedCalendar(Long userId, Long friendId, String startDate, String endDate) {
        getFriendRelationOrThrow(friendId,userId);

        List<Long> friendIds = new ArrayList<>();
        friendIds.add(userId); friendIds.add(friendId);

        return scheduleConverge.convergeSchedules(friendIds, startDate, endDate);
    }

    @Transactional
    public void sendScheduleRequest(Long userId, Long friendId, FriendScheduleRequestDto dto) {
        User sender = userService.getUserById(userId);
        User receiver = userService.getUserById(friendId);

        getFriendRelationOrThrow(friendId,userId);

        sharedFunction.validateSingleScheduleOverlap(userId, dto.getDate(), dto.getStartTime(), dto.getEndTime(), 0L);
        sharedFunction.validateSingleScheduleOverlap(friendId, dto.getDate(), dto.getStartTime(), dto.getEndTime(), 0L);

        FriendScheduleRequest req = FriendScheduleRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .title(dto.getTitle())
                .requestMemo(dto.getRequestMemo())
                .color(ScheduleColor.fromCode(dto.getColor()))
                .date(dto.getDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .requestedAt(LocalDateTime.now())
                .build();
        scheduleRequestRepository.save(req);

        String content = String.format("%s님이 %s에 \"%s\" 약속을 신청했습니다.",
                sender.getUserName(), dto.getDate().toString(), dto.getTitle());
        notificationService.createNotification(
                userId, friendId, NotificationType.FRIEND_SCHEDULE_REQUEST, content, req.getId());
    }

    @Transactional(readOnly = true)
    public List<ScheduleRequestDto> getReceivedScheduleRequests(Long recipientId) {
        return scheduleRequestRepository.findByReceiver_Id(recipientId).stream()
            .map(ScheduleRequestDto::from)
            .toList();
    }

    @Transactional
    public void acceptScheduleRequest(Long userId, Long requestId) {
        FriendScheduleRequest req = getScheduleRequestOrThrow(requestId);
        if (!req.getReceiver().getId().equals(userId)) {
            throw new BusinessException(FriendErrorCode.NO_PERMISSION);
        }
        CreateSingleScheduleDto dto = CreateSingleScheduleDto.from(req);

        Long senderId   = req.getSender().getId();
        Long receiverId = req.getReceiver().getId();
        userSingleScheduleService.createSingleSchedule(senderId, dto);
        userSingleScheduleService.createSingleSchedule(receiverId, dto);

        String content = String.format("%s님이 %s에 \"%s\" 약속 요청을 수락했습니다.",
                req.getReceiver().getUserName(), req.getDate(), req.getTitle());
        notificationService.createNotification(receiverId, senderId,
                NotificationType.FRIEND_SCHEDULE_ACCEPTED, content, (long)-1);

        scheduleRequestRepository.delete(req);
    }

    @Transactional
    public void rejectScheduleRequest(Long userId, Long requestId) {
        FriendScheduleRequest req = getScheduleRequestOrThrow(requestId);
        if (!req.getReceiver().getId().equals(userId)) {
            throw new BusinessException(FriendErrorCode.NO_PERMISSION);
        }

        Long senderId   = req.getSender().getId();
        Long receiverId = req.getReceiver().getId();

        String content = String.format("%s님이 %s에 \"%s\" 약속 요청을 거절했습니다.",
                req.getReceiver().getUserName(), req.getDate(), req.getTitle());
        notificationService.createNotification(receiverId, senderId,
                NotificationType.FRIEND_SCHEDULE_REJECTED, content, (long)-1);

        scheduleRequestRepository.delete(req);
    }
}
