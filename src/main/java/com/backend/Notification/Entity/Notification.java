package com.backend.Notification.Entity;

import com.backend.User.Entity.User;
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

    @Column(length = 30)
    @Schema(description = "알림 종류", example = "FRIEND_REQUEST")
    private String type;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "알림 내용", example = "홍길동님이 친구 요청을 보냈습니다.")
    private String content;

    @Column(name = "is_read", nullable = false)
    @Schema(description = "읽음 여부", example = "false")
    private Boolean isRead;

    @Column(name = "created_at")
    @Schema(description = "알림 생성 일시", example = "2025-06-01T18:00:00")
    private LocalDateTime createdAt;
}