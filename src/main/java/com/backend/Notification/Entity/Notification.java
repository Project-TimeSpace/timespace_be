package com.backend.Notification.Entity;

import com.backend.configenum.Converter.NotificationTypeConverter;
import com.backend.configenum.GlobalEnum.NotificationType;
import com.backend.user.Entity.User;
import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Entity
@Table(name = "Notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(description = "알림 정보를 저장하는 엔티티")
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "알림 고유 ID", example = "1")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    @Schema(description = "알림 발신자")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "알림 수신자")
    private User user;

    @Convert(converter = NotificationTypeConverter.class)
    @Column(name = "type", nullable = false)
    @Schema(description = "알림 종류 코드 (GlobalEnum.NotificationType)", example = "1")
    private NotificationType type;

    @Column(name = "target_id")
    @Schema(description = "친구가 약속 신청한거 | 그룹 스케쥴 추가 관련 ID정보")
    private Long targetId;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "알림 내용", example = "홍길동님이 친구 요청을 보냈습니다.")
    private String content;

    @Column(name = "is_read", nullable = false)
    @Schema(description = "읽음 여부", example = "false")
    private Boolean isRead;

    @Column(name = "created_at")
    @Schema(description = "알림 생성 일시", example = "2025-06-01T18:00:00")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}