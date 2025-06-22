package com.backend.Friend.Dto;

import com.backend.Config.GlobalEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "친구 요청 생성 및 조회에 사용하는 DTO")
public class FriendRequestReceivedDto {

    @Schema(description = "요청 ID (조회 시)", example = "10")
    private Long id;

    @Schema(description = "요청자 이름 (조회 시)", example = "김철수")
    private String name;

    @Schema(description = "상대방 이메일", example = "chulsoo@example.com")
    private String email;

    @Schema(description = "요청 상태", example = "PENDING")
    private GlobalEnum.RequestStatus status;

    @Schema(description = "요청 일시 (조회 시)", example = "2025-06-10T14:30:00")
    private LocalDateTime requestedAt;
}
