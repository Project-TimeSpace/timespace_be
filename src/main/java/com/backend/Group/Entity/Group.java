package com.backend.Group.Entity;

import com.backend.User.Entity.User;
import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Entity
@Table(name = "`Group`")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(description = "그룹 정보를 저장하는 엔티티")
public class Group {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "그룹 고유 ID", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id")
    @Schema(description = "그룹 관리자(User)")
    private User master;

    @Column(name = "group_name", nullable = false, length = 100)
    @Schema(description = "그룹 이름", example = "스터디 모임")
    private String groupName;

    @Column(name = "group_type", nullable = false, length = 100)
    @Schema(description = "그룹 유형", example = "STUDY")
    private String groupType;

    @Column(name = "max_member", nullable = false)
    @Schema(description = "최대 그룹 멤버 수", example = "20")
    private Integer maxMember;

    @Column(name = "created_at")
    @Schema(description = "그룹 생성 일시", example = "2025-06-01T15:00:00")
    private LocalDateTime createdAt;
}