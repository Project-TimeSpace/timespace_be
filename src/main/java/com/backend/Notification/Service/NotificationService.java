package com.backend.Notification.Service;

import com.backend.configenum.GlobalEnum;
import com.backend.Notification.Dto.NotificationDto;
import com.backend.Notification.Entity.Notification;
import com.backend.Notification.Repository.NotificationRepository;
import com.backend.configenum.GlobalEnum.NotificationType;
import com.backend.user.Entity.User;
import com.backend.user.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // 한명에게 알림 보내기
    public void createNotification(Long senderId, Long receiverId, NotificationType type, String content, Long targetId) {
        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new NoSuchElementException("Sender not found with id: " + senderId));
        User receiver = userRepository.findById(receiverId)
            .orElseThrow(() -> new NoSuchElementException("Receiver not found with id: " + receiverId));

        Notification notification = Notification.builder()
            .sender(sender)
            .user(receiver)
            .type(type)
            .targetId(targetId)
            .content(content)
            .isRead(false)
            .createdAt(LocalDateTime.now())
            .build();

        notificationRepository.save(notification);
    }

    // 다수에게 알림을 보내야 할 때
    @Transactional
    public void createNotifications(Long senderId, List<Long> receivers, GlobalEnum.NotificationType type, String content, Long targetId) {

        User senderRef = userRepository.getReferenceById(senderId);
        List<Notification> list = new ArrayList<>(receivers.size());
        for (Long rid : receivers) {
            list.add(Notification.builder()
                .sender(senderRef)
                .user(userRepository.getReferenceById(rid))
                .type(type)
                .content(content)
                .targetId(targetId)
                .build());
        }
        notificationRepository.saveAll(list);
    }

    /** 읽지 않은 알림을 최신순으로 조회하고, 그 목록만 읽음 처리 */
    @Transactional
    public List<NotificationDto> pullUnreadAndMarkRead(Long userId) {

        // 1) 읽지 않은 알림 조회(최신순)
        List<Notification> entities =
            notificationRepository.findByUser_IdAndIsReadFalseOrderByCreatedAtDesc(userId);

        // 2) DTO로 변환
        List<NotificationDto> dtos = entities.stream()
            .map(n -> NotificationDto.builder()
                .id(n.getId())
                .senderId(n.getSender() != null ? n.getSender().getId() : null)
                .senderName(n.getSender() != null ? n.getSender().getUserName() : null)
                .senderEmail(n.getSender() != null ? n.getSender().getEmail() : null)
                .content(n.getContent())
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .build())
            .toList();

        // 3) 방금 조회한 것만 읽음 처리 (신규 알림 레이스 방지)
        if (!entities.isEmpty()) {
            List<Integer> ids = entities.stream().map(Notification::getId).toList();
            notificationRepository.markAsReadByIds(ids);
        }

        return dtos;
    }


    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationsByTypes(Long userId,
        List<GlobalEnum.NotificationType> types) {
        List<Notification> notifications =
            notificationRepository.findByUser_IdAndDeletedAtIsNullAndTypeInOrderByCreatedAtDesc(userId, types);

        return notifications.stream().map(this::toDto).collect(Collectors.toList());
    }

    private NotificationDto toDto(Notification notification) {
        return NotificationDto.builder()
            .id(notification.getId())
            .senderId(notification.getSender().getId())
            .senderName(notification.getSender().getUserName())
            .senderEmail(notification.getSender().getEmail())
            .content(notification.getContent())
            .isRead(notification.getIsRead())
            .createdAt(notification.getCreatedAt())
            .build();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        long number = notificationRepository.countByUserIdAndIsReadFalse(userId);
        if(number>100){number=99;}
        return number;
    }

    @Transactional
    public void markAsRead(Long userId, Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new NoSuchElementException("Notification not found with id: " + notificationId));

        if (!notification.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("이 알림을 읽을 권한이 없습니다.");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }


}