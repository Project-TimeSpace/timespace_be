package com.backend.friend.Service;

import com.backend.configenum.GlobalEnum.NotificationType;
import com.backend.configenum.GlobalEnum.Visibility;
import com.backend.friend.Dto.FriendRequestReceivedDto;
import com.backend.friend.Entity.FriendRequest;
import com.backend.friend.Entity.Friend;
import com.backend.Notification.Service.NotificationService;
import com.backend.response.BusinessException;
import com.backend.user.Entity.User;
import com.backend.friend.error.FriendErrorCode;
import com.backend.friend.Repository.FriendRepository;
import com.backend.friend.Repository.FriendRequestRepository;
import com.backend.user.Service.UserService;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendRequestService {
    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;

    private final NotificationService notificationService;
    private final UserService userService;

    private FriendRequest getRequestOrThrow(Long requestId) {
        return friendRequestRepository.findById(requestId)
            .orElseThrow(() -> new BusinessException(FriendErrorCode.REQUEST_NOT_FOUND));
    }
    private void ensureReceiverAuthorized(Long userId, FriendRequest req) {
        if (!req.getReceiver().getId().equals(userId)) {
            throw new BusinessException(FriendErrorCode.NO_PERMISSION);
        }
    }

    private Friend buildRelation(User owner, User other, String nickname) {
        return Friend.builder()
            .user(owner)
            .friend(other)
            .isFavorite(false)
            .visibility(Visibility.SIMPLE)
            .nickname(nickname)
            .createdAt(LocalDateTime.now())
            .build();
    }

    @Transactional
    public void sendFriendRequest(Long userId, String email) {
        User sender = userService.getUserById(userId);
        User receiver = userService.getUserByEmail(email);

        // 이미 친구 관계인지 검사
        if (friendRepository.findByUser_IdAndFriend_Id(sender.getId(), receiver.getId()).isPresent()) {
            throw new BusinessException(FriendErrorCode.ALREADY_FRIENDS);
        }

        // 중복 요청 방지 (이미 보낸 요청이 PENDING 상태라면)
        boolean pendingExists = friendRequestRepository.existsBySenderIdAndReceiverId(sender.getId(), receiver.getId());
        if (pendingExists) {
            throw new BusinessException(FriendErrorCode.REQUEST_ALREADY_SENT);
        }

        FriendRequest req = FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
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

        return requests.stream().map(FriendRequestReceivedDto::from).toList();
    }


    @Transactional
    public void acceptFriendRequest(Long userId, Long requestId) {
        FriendRequest req = getRequestOrThrow(requestId);
        ensureReceiverAuthorized(userId, req);

        User sender   = req.getSender();
        User receiver = req.getReceiver();

        Friend rel1 = buildRelation(sender, receiver, receiver.getUserName());
        Friend rel2 = buildRelation(receiver, sender, sender.getUserName());
        friendRepository.save(rel1);
        friendRepository.save(rel2);

        friendRequestRepository.delete(req);
    }

    @Transactional
    public void rejectFriendRequest(Long userId, Long requestId) {
        FriendRequest req = getRequestOrThrow(requestId);
        ensureReceiverAuthorized(userId, req);

        friendRequestRepository.delete(req);
    }

}

