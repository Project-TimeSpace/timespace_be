package com.backend.Notification.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "NotificationDto", description = "유저에게 전달되는 알림 정보를 담은 DTO")
public class NotificationDto {

    @Schema(description = "알림 ID", example = "1")
    private Integer id;

    @Schema(description = "발신자 유저 ID", example = "42")
    private Long senderId;

    @Schema(description = "발신자 이름", example = "홍길동")
    private String senderName;

    @Schema(description = "발신자 이메일 주소", example = "hong@example.com")
    private String senderEmail;

    @Schema(description = "알림 내용", example = "홍길동님이 친구 요청을 보냈습니다.")
    private String content;

    @Schema(description = "읽음 여부", example = "false")
    private Boolean isRead;

    @Schema(description = "알림 생성 시각 (ISO-8601)", example = "2025-07-08T15:30:00")
    private LocalDateTime createdAt;
}
