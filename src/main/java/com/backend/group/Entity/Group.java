package com.backend.group.Entity;

import com.backend.configenum.Converter.GroupCategoryConverter;
import com.backend.configenum.GlobalEnum.GroupCategory;
import com.backend.user.Entity.User;
import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "`Group`")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(description = "그룹 정보를 저장하는 엔티티")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "그룹 고유 ID", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id")
    @Schema(description = "그룹 관리자(user)")
    private User master;

    @Column(name = "group_name", nullable = false, length = 100)
    @Schema(description = "그룹 이름", example = "스터디 모임")
    private String groupName;

    @Column(name = "group_type", nullable = false, length = 100)
    @Schema(description = "그룹 타입", example = "NORMAL")
    private String groupType;

    @Column(name = "max_member", nullable = false)
    @Schema(description = "최대 그룹 멤버 수", example = "20")
    private Integer maxMember;

    @Convert(converter = GroupCategoryConverter.class)
    @Column(name = "category", nullable = false)
    @Schema(description = "그룹 카테고리 코드", example = "1")
    private GroupCategory category;

    @Column(name = "unique_code", length = 100, unique = true)
    @Schema(description = "그룹 참여용 고유 코드", example = "abc123xyz")
    private String uniqueCode;

    @Column(name = "group_image_url", length = 255)
    @Schema(description = "그룹 이미지 URL", example = "https://example.com/groupImg.jpg")
    private String groupImageUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    @Schema(description = "그룹 생성 일시", example = "2025-06-01T15:00:00")
    private LocalDateTime createdAt;
}
