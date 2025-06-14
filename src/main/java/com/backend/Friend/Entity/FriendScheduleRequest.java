package com.backend.Friend.Entity;

import com.backend.User.Entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "FriendScheduleRequest")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(description = "친구 일정 요청을 저장하는 엔티티")
public class FriendScheduleRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "일정 요청 고유 ID", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    @Schema(description = "요청자 정보")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    @Schema(description = "수신자 정보")
    private User receiver;

    @Column(length = 100)
    @Schema(description = "일정 제목", example = "함께 점심")
    private String title;

    @Column(nullable = false)
    @Schema(description = "일정 날짜", example = "2025-06-15")
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    @Schema(description = "시작 시간", example = "12:00:00")
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    @Schema(description = "종료 시간", example = "13:00:00")
    private LocalTime endTime;

    @Column(nullable = false, length = 30)
    @Schema(description = "요청 상태", example = "PENDING")
    private String status;

    @Column(name = "requested_at")
    @Schema(description = "요청 일시", example = "2025-06-01T14:00:00")
    private LocalDateTime requestedAt;
}
