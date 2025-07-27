package com.backend.User.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequestDto {

    @Schema(description = "사용자 이름", example = "홍길동")
    private String userName;

    @Schema(description = "대학교 코드", example = "1")
    private Integer univCode;

    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phoneNumber;

    @Schema(description = "생년월일", example = "1990-01-01")
    private LocalDate birthDate;
}

