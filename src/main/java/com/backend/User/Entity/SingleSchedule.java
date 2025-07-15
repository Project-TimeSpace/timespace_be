package com.backend.User.Entity;

import com.backend.ConfigEnum.Converter.ScheduleColorConverter;
import com.backend.ConfigEnum.GlobalEnum;
import com.backend.ConfigEnum.GlobalEnum.ScheduleColor;
import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "SingleSchedule")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(description = "단일 일정 정보를 저장하는 엔티티")
public class SingleSchedule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "단일 일정 고유 ID", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "일정을 등록한 사용자 정보")
    private User user;

    @Column(length = 100)
    @Schema(description = "일정 제목", example = "회의")
    private String title;

    @Convert(converter = ScheduleColorConverter.class)
    @Schema(description = "일정 표시 색상 (hex)", example = "#FF0000")
    private ScheduleColor color;

    @Column(nullable = false)
    @Schema(description = "일정 날짜", example = "2025-06-14")
    private LocalDate date;

    @Column(nullable = false)
    @Schema(description = "요일(1=월요일~7=일요일)", example = "0")
    private Integer day;

    @Column(name = "start_time", nullable = false)
    @Schema(description = "시작 시간", example = "09:00:00")
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    @Schema(description = "종료 시간", example = "10:00:00")
    private LocalTime endTime;
}
