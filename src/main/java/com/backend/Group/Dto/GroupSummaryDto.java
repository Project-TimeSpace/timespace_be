package com.backend.Group.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "그룹 요약 정보 DTO")
public class GroupSummaryDto {

    @Schema(description = "그룹 식별자", example = "1")
    private Long groupId;

    @Schema(description = "그룹 이름", example = "스터디 팀")
    private String groupName;

    @Schema(description = "그룹 타입", example = "Normal")
    private String groupType;

    @Schema(description = "현재 멤버 수", example = "5")
    private Long memberCount;

    @Schema(description = "최대 멤버 수", example = "10")
    private Integer maxMemberCount;

    //@Schema(description = "그룹 이미지 URL", example = "https://example.com/image.png")
    //private String groupImageUrl;
}

