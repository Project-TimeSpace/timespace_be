package com.backend.Login.Auth;

import com.backend.user.Entity.User;
import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "VisitLog", uniqueConstraints = @UniqueConstraint(name = "uq_visit_date", columnNames = {"user_id","visit_date"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(description = "사용자 방문 로그를 저장하는 엔티티")
public class VisitLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "방문 로그 고유 ID", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "방문한 사용자")
    private User user;

    @Column(name = "visit_date", nullable = false)
    @Schema(description = "방문 날짜", example = "2025-06-14")
    private LocalDate visitDate;

    @Column(name = "count", nullable = false)
    @Schema(description = "하루 방문 횟수")
    private int count;

    @Column(name = "created_at")
    @Schema(description = "로그 생성 일시", example = "2025-06-14T08:00:00")
    private LocalDateTime createdAt;
}
