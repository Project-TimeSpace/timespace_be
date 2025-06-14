package com.backend.UserSchedule.Entity;

import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Entity
@Table(name = "RepeatException")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(description = "반복 일정 예외일자를 저장하는 엔티티")
public class RepeatException {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "예외 고유 ID", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repeat_id", nullable = false)
    @Schema(description = "반복 일정 정보")
    private RepeatSchedule repeatSchedule;

    @Column(name = "exception_date", nullable = false)
    @Schema(description = "예외 발생 일자", example = "2025-06-07")
    private LocalDate exceptionDate;
}