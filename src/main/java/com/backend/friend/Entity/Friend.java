package com.backend.friend.Entity;

import com.backend.configenum.GlobalEnum.Visibility;
import com.backend.user.Entity.User;
import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Entity
@Table(name = "Friend", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","friend_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(description = "친구 관계를 저장하는 엔티티")
public class Friend {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "친구 관계 고유 ID", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "사용자 정보")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    @Schema(description = "친구 사용자 정보")
    private User friend;

    @Column(name = "is_favorite", nullable = false)
    @Schema(description = "즐겨찾기 여부", example = "false")
    private Boolean isFavorite;

    @Enumerated(EnumType.STRING)
    @Schema(description = "친구 표시 여부", example = "true")
    @Column(name = "visibility", nullable = false, length = 10)
    private Visibility visibility;

    @Column(length = 50)
    @Schema(description = "친구 별칭", example = "길동이")
    private String nickname;

    @Column(name = "created_at")
    @Schema(description = "친구 추가 일시", example = "2025-06-01T13:00:00")
    private LocalDateTime createdAt;
}
