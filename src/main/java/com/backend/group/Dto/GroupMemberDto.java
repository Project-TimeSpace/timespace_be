package com.backend.group.Dto;

import com.backend.group.Entity.GroupMembers;
import com.backend.user.Entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupMemberDto {

    @Schema(description = "유저 ID", example = "101")
    private Long userId;

    @Schema(description = "유저 이름", example = "홍길동")
    private String userName;

    @Schema(description = "이메일", example = "hong@example.com")
    private String email;

    @Schema(description = "프로필 이미지 URL", example = "https://kr.object.ncloudstorage.com/your-bucket/profiles/101/abc.png")
    @JsonProperty("profile_image_url")
    private String profileImageUrl;

    @Schema(description = "그룹 마스터 여부", example = "false")
    @JsonProperty("isMaster")
    private boolean isMaster;

    public static GroupMemberDto from(GroupMembers gm, Long masterId) {
        User user = gm.getUser();
        return GroupMemberDto.builder()
            .userId(user.getId())
            .userName(user.getUserName())
            .email(user.getEmail())
            .profileImageUrl(user.getProfileImageUrl())
            .isMaster(masterId.equals(user.getId()))
            .build();
    }
}
