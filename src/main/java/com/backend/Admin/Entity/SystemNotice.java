package com.backend.Admin.Entity;

import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Entity
@Table(name = "SystemNotices")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(description = "시스템 공지사항 정보를 저장하는 엔티티")
public class SystemNotice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "공지사항 고유 ID", example = "1")
    private Integer id;

    @Column(nullable = false, length = 100)
    @Schema(description = "공지사항 제목", example = "점검 안내")
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Schema(description = "공지사항 내용")
    private String content;

    @Column(name = "created_at")
    @Schema(description = "공지 생성 일시", example = "2025-06-01T19:00:00")
    private LocalDateTime createdAt;
}