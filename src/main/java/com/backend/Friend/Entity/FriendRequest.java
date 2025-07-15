package com.backend.Friend.Entity;


import com.backend.ConfigEnum.Converter.RequestStatusConverter;
import com.backend.ConfigEnum.GlobalEnum.RequestStatus;
import com.backend.User.Entity.User;
import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Entity
@Table(name = "FriendRequest", uniqueConstraints = @UniqueConstraint(name = "uq_friend_request", columnNames = {"sender_id","receiver_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(description = "친구 요청 정보를 저장하는 엔티티")
public class FriendRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "친구 요청 고유 ID", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    @Schema(description = "요청자 정보")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    @Schema(description = "수신자 정보")
    private User receiver;

    @Convert(converter = RequestStatusConverter.class)
    @Column(nullable = false)
    private RequestStatus status;

    @Column(name = "requested_at")
    @Schema(description = "요청 일시", example = "2025-06-01T12:00:00")
    private LocalDateTime requestedAt;
}
