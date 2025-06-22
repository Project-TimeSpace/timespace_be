package com.backend.Friend.Dto;

import com.backend.Config.GlobalEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "친구 목록 조회 시 반환할 DTO")
public class FriendDto {

    @Schema(description = "친구 관계 고유 ID", example = "1")
    private Long id;

    @Schema(description = "친구 사용자 ID", example = "2")
    private Long friendId;

    @Schema(description = "친구 이름", example = "홍길동")
    private String name;

    @Schema(description = "친구 이메일", example = "hong@example.com")
    private String email;

    @Schema(description = "즐겨찾기 여부", example = "true")
    private Boolean isFavorite;

    @Schema(description = "친구 표시 여부", example = "true")
    private GlobalEnum.Visibility visibility;

    @Schema(description = "친구 별칭", example = "길동이")
    private String nickname;

    @Schema(description = "친구 추가 일시", example = "2025-06-01T13:00:00")
    private LocalDateTime createdAt;
}

