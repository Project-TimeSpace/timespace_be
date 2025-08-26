package com.backend.group.Dto;

import com.backend.group.Entity.Group;

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

    @Schema(description = "그룹 카테고리 code값", example = "1")
    private Integer categoryCode;

    @Schema(description = "그룹 카테고리 이름값(코드랑 연관 데이터임)", example = "일반")
    private String categoryName;

    @Schema(description = "현재 멤버 수", example = "5")
    private Long memberCount;

    @Schema(description = "최대 멤버 수", example = "10")
    private Integer maxMemberCount;

    @Schema(description = "그룹 이미지 URL", example = "https://kr.object.ncloudstorage.com/your-bucket/groups/1/abc.png")
    private String groupImageUrl;

    public static GroupSummaryDto from(Group group, long memberCount) {
        return GroupSummaryDto.builder()
            .groupId(group.getId())
            .groupName(group.getGroupName())
            .groupType(group.getGroupType())
            .categoryCode(group.getCategory().getCode())
            .categoryName(group.getCategory().getDisplayName())
            .memberCount(memberCount)
            .maxMemberCount(group.getMaxMember())
            .groupImageUrl(group.getGroupImageUrl())
            .build();
    }
}

