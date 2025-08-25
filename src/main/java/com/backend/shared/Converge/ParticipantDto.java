package com.backend.shared.Converge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ParticipantDto {
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;
    @Schema(description = "사용자 이름", example = "홍길동")
    private String userName;
    @Schema(description = "사용자 이메일", example = "hong@example.com")
    private String email;
}