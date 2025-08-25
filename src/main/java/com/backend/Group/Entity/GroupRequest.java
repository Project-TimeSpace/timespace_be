package com.backend.Group.Entity;

import com.backend.ConfigEnum.Converter.RequestStatusConverter;
import com.backend.ConfigEnum.GlobalEnum.RequestStatus;
import com.backend.User.Entity.User;
import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Entity
@Table(name = "GroupRequest", uniqueConstraints = @UniqueConstraint(name = "uq_group_receiver", columnNames = {"group_id","receiver_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(description = "그룹 가입 요청 정보를 저장하는 엔티티")
public class GroupRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "그룹 요청 고유 ID", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @Schema(description = "대상 그룹 정보")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id", nullable = false)
    @Schema(description = "초대한 사용자")
    private User inviter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    @Schema(description = "초대받은 사용자")
    private User receiver;

    @Column(name = "requested_at")
    @Schema(description = "요청 일시", example = "2025-06-01T16:00:00")
    private LocalDateTime requestedAt;

    @Column(name = "responded_at")
    @Schema(description = "응답 일시", example = "2025-06-02T10:00:00")
    private LocalDateTime respondedAt;
}
