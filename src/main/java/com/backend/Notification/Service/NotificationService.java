package com.backend.Notification.Service;

import com.backend.Notification.Dto.NotificationDto;
import com.backend.Notification.Entity.Notification;
import com.backend.Notification.Repository.NotificationRepository;
import com.backend.ConfigEnum.GlobalEnum.NotificationType;
import com.backend.User.Entity.User;
import com.backend.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public void createNotification(Long senderId, Long receiverId, NotificationType type, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NoSuchElementException("Sender not found with id: " + senderId));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new NoSuchElementException("Receiver not found with id: " + receiverId));

        Notification notification = Notification.builder()
                .sender(sender)
                .user(receiver)
                .type(type)
                .content(content)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getNotifications(Long userId) {
        List<Notification> entities = notificationRepository.findByUserIdAndIsReadFalse(userId);

        return entities.stream().map(this::toDto)
                .collect(Collectors.toList());
    }
    private NotificationDto toDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .senderId(notification.getSender().getId())
                .senderName(notification.getSender().getUserName())
                .senderEmail(notification.getSender().getEmail())
                .type(notification.getType())
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
