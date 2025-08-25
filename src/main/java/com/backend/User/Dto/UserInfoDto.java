package com.backend.User.Dto;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

import com.backend.ConfigEnum.GlobalEnum;
import com.backend.User.Entity.User;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {

    @Schema(description = "유저 고유 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String userName;

    @Schema(description = "이메일 주소", example = "hong@example.com")
    private String email;

    @Schema(description = "대학교 코드", example = "1")
    private Integer univCode;
    @Schema(description = "대학교 이름", example = "한양대학교")
    private String univName;

    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phoneNumber;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;

    @Schema(description = "생년월일", example = "1990-01-01")
    private LocalDate birthDate;

    public static UserInfoDto from(User user) {
        GlobalEnum.University univ = user.getUniversity();
        return UserInfoDto.builder()
            .id(user.getId())
            .userName(user.getUserName())
            .email(user.getEmail())
            .univCode(univ != null ? univ.getCode() : null)
            .univName(univ != null ? univ.getDisplayName() : null)
            .phoneNumber(user.getPhoneNumber())
            .birthDate(user.getBirthDate())
            .profileImageUrl(user.getProfileImageUrl())
            .build();
    }
}
