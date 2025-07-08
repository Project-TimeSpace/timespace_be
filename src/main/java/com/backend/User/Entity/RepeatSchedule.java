package com.backend.User.Entity;

import com.backend.ConfigEnum.GlobalEnum;
import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "RepeatSchedule")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(description = "반복 일정 정보를 저장하는 엔티티")
public class RepeatSchedule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "반복 일정 고유 ID", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "일정을 등록한 사용자 정보")
    private User user;

    @Column(nullable = false, length = 100)
    @Schema(description = "반복 일정 제목", example = "매주 회의")
    private String title;

    @Schema(description = "일정 표시 색상-정수로 저장하고 Enum으로 매핑", example = "1")
    private int color;

    @Column(name = "start_date", nullable = false)
    @Schema(description = "반복 시작 날짜", example = "2025-06-01")
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    @Schema(description = "반복 종료 날짜", example = "2025-12-31")
    private LocalDate endDate;

    @Column(name = "repeat_days", nullable = false)
    @Schema(description = "반복 요일", example = "1=월요일~7=일요일")
    private Integer repeatDays;

    @Column(name = "start_time", nullable = false)
    @Schema(description = "시작 시간", example = "09:00:00")
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    @Schema(description = "종료 시간", example = "10:00:00")
    private LocalTime endTime;
}