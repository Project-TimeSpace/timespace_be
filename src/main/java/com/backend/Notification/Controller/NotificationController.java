package com.backend.Notification.Controller;

import com.backend.Admin.Dto.SystemNoticeResponseDto;
import com.backend.Admin.Service.SystemNoticeService;
import com.backend.Notification.Dto.NotificationDto;
import com.backend.Notification.Service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "5. User의 알림관련 api")
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final SystemNoticeService systemNoticeService;

    @Operation(summary = "1. 알림 목록 조회(읽지 않은 것만)",
        description = "로그인 사용자의 읽지 않은 알림을 최신순으로 반환하고, **조회 직후 해당 알림들을 읽음 처리**합니다.")
    @GetMapping("/list")
    public ResponseEntity<List<NotificationDto>> listUnreadAndMarkRead(
        @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = Long.parseLong(userDetails.getUsername());
        List<NotificationDto> notifications = notificationService.pullUnreadAndMarkRead(userId);
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "2. 유저가 시스템 공지 목록 조회",
        description = "최신 ID 내림차순으로 전체 목록을 조회합니다.")
    @GetMapping("/system/list")
    public ResponseEntity<List<SystemNoticeResponseDto>> list() {
        return ResponseEntity.ok(systemNoticeService.list());
    }

    @Operation(summary = "3. 유저가 시스템 공지 단건 조회",
        description = "공지 ID로 단건 조회합니다.")
    @GetMapping("/system/{id}")
    public ResponseEntity<SystemNoticeResponseDto> get(@PathVariable Integer id) {
        return ResponseEntity.ok(systemNoticeService.get(id));
    }

    /*
    @Operation(summary = "2. 친구 일정 알림 조회",
        description = "FRIEND_SCHEDULE_REQUEST(2) 및 FRIEND_SCHEDULE_ACCEPTED(3) 타입 알림을 생성일자 최신순으로 조회합니다.")
    @GetMapping("/friend-schedule")
    public ResponseEntity<List<NotificationDto>> getFriendScheduleNotifications(
        @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = Long.parseLong(userDetails.getUsername());
        List<NotificationDto> list = notificationService.getNotificationsByTypes(userId,
            List.of(GlobalEnum.NotificationType.FRIEND_SCHEDULE_REQUEST,
                GlobalEnum.NotificationType.FRIEND_SCHEDULE_ACCEPTED,
                GlobalEnum.NotificationType.FRIEND_SCHEDULE_REJECTED)
        );
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "3 그룹 알림 조회",
        description = "GROUP_INVITE(5), GROUP_SCHEDULE(6), GROUP_MASTER(7) 타입 알림을 생성일자 최신순으로 조회합니다.")
    @GetMapping("/group")
    public ResponseEntity<List<NotificationDto>> getGroupNotifications(
        @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = Long.parseLong(userDetails.getUsername());
        List<NotificationDto> list = notificationService.getNotificationsByTypes(
            userId,
            List.of(
                GlobalEnum.NotificationType.GROUP_INVITE,
                GlobalEnum.NotificationType.GROUP_SCHEDULE,
                GlobalEnum.NotificationType.GROUP_MASTER
            )
        );
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "시스템 공지 조회",
        description = "SYSTEM_NOTICE(8) 타입 알림을 생성일자 최신순으로 조회합니다.")
    @GetMapping("/system")
    public ResponseEntity<List<NotificationDto>> getSystemNotifications(
        @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = Long.parseLong(userDetails.getUsername());
        List<NotificationDto> list = notificationService.getNotificationsByTypes(
            userId,
            List.of(GlobalEnum.NotificationType.SYSTEM_NOTICE)
        );
        list.sort(Comparator.comparing(NotificationDto::getCreatedAt).reversed());
        return ResponseEntity.ok(list);
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
    */
}