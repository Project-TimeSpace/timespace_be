package com.backend.Friend.Service;

import com.backend.ConfigEnum.GlobalEnum.NotificationType;
import com.backend.ConfigEnum.GlobalEnum.Visibility;
import com.backend.Friend.Dto.FriendRequestReceivedDto;
import com.backend.Friend.Entity.FriendRequest;
import com.backend.Friend.Entity.Friend;
import com.backend.ConfigEnum.GlobalEnum.RequestStatus;
import com.backend.Notification.Service.NotificationService;
import com.backend.User.Entity.User;
import com.backend.User.Repository.UserRepository;
import com.backend.Friend.Repository.FriendRepository;
import com.backend.Friend.Repository.FriendRequestRepository;
import com.backend.Friend.Repository.FriendScheduleRequestRepository;
import com.backend.User.Service.UserSingleScheduleService;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendRequestService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final NotificationService notificationService;


    @Transactional
    public void sendFriendRequest(Long userId, String email) {
        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        User receiver = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("요청 대상 사용자를 찾을 수 없습니다."));

        // 이미 친구 관계인지 검사
        if (friendRepository.findByUserIdAndFriendId(sender.getId(), receiver.getId()).isPresent()) {
            throw new IllegalArgumentException("이미 친구 관계입니다.");
        }

        // 중복 요청 방지 (이미 보낸 요청이 PENDING 상태라면)
        boolean pendingExists = friendRequestRepository
                .existsBySenderIdAndReceiverIdAndStatus(sender.getId(), receiver.getId(), RequestStatus.PENDING);
        if (pendingExists) {
            throw new IllegalArgumentException("이미 요청을 보낸 상태입니다.");
        }

        FriendRequest req = FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status(RequestStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();
        friendRequestRepository.save(req);

        // 6) 알림 전송
        String content = String.format("%s(%s)님이 친구 요청을 보냈습니다.", sender.getUserName(), sender.getEmail());
        notificationService.createNotification(
                userId, receiver.getId(), NotificationType.FRIEND_REQUEST, content, req.getId());
    }

    @Transactional(readOnly = true)
    public List<FriendRequestReceivedDto> getReceivedRequests(Long userId) {
        List<FriendRequest> requests = friendRequestRepository.findAllByReceiverId(userId);

        return requests.stream()
                .map(r -> FriendRequestReceivedDto.builder()
                        .id(r.getId())
                        .name(r.getSender().getUserName())
                        .email(r.getSender().getEmail())
                        .status(r.getStatus())       // enum 그대로
                        .requestedAt(r.getRequestedAt())
                        .build()
                )
                .collect(Collectors.toList());
    }


    @Transactional
    public void acceptFriendRequest(Long userId, Long requestId) {
        FriendRequest req = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 친구 요청입니다."));

        // 권한 확인
        if (!req.getReceiver().getId().equals(userId)) {
            throw new AccessDeniedException("친구 요청을 수락할 권한이 없습니다.");
        }
        if (req.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("이미 처리된 요청입니다.");
        }

        User sender   = req.getSender();
        User receiver = req.getReceiver();

        // 양방향 친구 관계 생성 (기본 SIMPLE, nickname = 상대방 이름)
        Friend rel1 = Friend.builder()
                .user(sender)
                .friend(receiver)
                .isFavorite(false)
                .visibility(Visibility.SIMPLE)
                .nickname(receiver.getUserName())
                .createdAt(LocalDateTime.now())
                .build();
        Friend rel2 = Friend.builder()
                .user(receiver)
                .friend(sender)
                .isFavorite(false)
                .visibility(Visibility.SIMPLE)
                .nickname(sender.getUserName())
                .createdAt(LocalDateTime.now())
                .build();
        friendRepository.save(rel1);
        friendRepository.save(rel2);

        // 요청 삭제
        friendRequestRepository.delete(req);
    }

    @Transactional
    public void rejectFriendRequest(Long userId, Long requestId) {
        FriendRequest req = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 친구 요청입니다."));

        // 권한 확인
        if (!req.getReceiver().getId().equals(userId)) {
            throw new AccessDeniedException("친구 요청을 거절할 권한이 없습니다.");
        }
        if (req.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("이미 처리된 요청입니다.");
        }

        // 거절된 요청도 삭제
        friendRequestRepository.delete(req);
    }

}

