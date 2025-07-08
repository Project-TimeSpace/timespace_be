package com.backend.Notification.Controller;

import com.backend.Notification.Dto.NotificationDto;
import com.backend.Notification.Service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('User')")
@Tag(name = "6. 알림관련 api")
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "알림 목록 조회", description = "로그인한 사용자가 받은 모든 알림을 생성일자 기준 최신순으로 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<List<NotificationDto>> listNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());

        List<NotificationDto> notifications = notificationService.getNotifications(userId);

        notifications.sort(Comparator.comparing(NotificationDto::getCreatedAt).reversed());
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "읽지 않은 알림 개수 조회", description = "읽지않은게 100개 이상인 경우 99로 리턴함.")
    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "알림 읽음 처리", description = "지정된 알림 ID의 상태를 읽음(isRead=true)으로 변경합니다.")
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer id) {
        Long userId = Long.parseLong(userDetails.getUsername());
        notificationService.markAsRead(userId, id);
        return ResponseEntity.ok().build();
    }
}
