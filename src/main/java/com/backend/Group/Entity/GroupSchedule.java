package com.backend.Group.Entity;

import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "GroupSchedule")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(description = "그룹 일정 정보를 저장하는 엔티티")
public class GroupSchedule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "그룹 일정 고유 ID", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @Schema(description = "소속 그룹")
    private Group group;

    @Column(length = 100)
    @Schema(description = "일정 제목", example = "그룹 회의")
    private String title;

    @Column(length = 7)
    @Schema(description = "일정 색상(HEX)", example = "#0000ff")
    private String color;

    @Column(nullable = false)
    @Schema(description = "일정 날짜", example = "2025-06-20")
    private LocalDate date;

    @Column(nullable = false)
    @Schema(description = "요일(0=일요일~6=토요일)", example = "5")
    private Byte day;

    @Column(name = "start_time", nullable = false)
    @Schema(description = "시작 시간", example = "14:00:00")
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    @Schema(description = "종료 시간", example = "15:00:00")
    private LocalTime endTime;

    @Column(name = "created_at")
    @Schema(description = "일정 생성 일시", example = "2025-06-01T17:00:00")
    private LocalDateTime createdAt;
}
