package com.backend.Group.Entity;

import com.backend.User.Entity.User;
import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "GroupMembers", uniqueConstraints = @UniqueConstraint(columnNames = {"group_id","user_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(description = "그룹 멤버 정보를 저장하는 엔티티")
public class GroupMembers {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "그룹 멤버 고유 ID", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @Schema(description = "소속된 그룹")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "그룹 멤버 사용자")
    private User user;

    @Column(name = "is_favorite", nullable = false)
    @Schema(description = "즐겨찾기 여부", example = "false")
    private Boolean isFavorite;
}