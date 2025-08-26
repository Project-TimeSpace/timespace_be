package com.backend.group.Dto;

import java.util.List;

import com.backend.group.Entity.Group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "사이드바 상단용 그룹 상세 정보 DTO")
public class GroupInfoDto {

    @Schema(description = "그룹 식별자", example = "1")
    private Long groupId;

    @Schema(description = "그룹 이름", example = "스터디 팀")
    private String groupName;

    @Schema(description = "그룹 타입", example = "Normal")
    private String groupType;

    @Schema(description = "그룹 카테고리 code값", example = "1")
    private Integer categoryCode;

    @Schema(description = "그룹 카테고리 이름값(코드와 연관)", example = "일반")
    private String categoryName;

    @Schema(description = "현재 멤버 수", example = "5")
    private int memberCount;

    @Schema(description = "최대 멤버 수", example = "10")
    private int maxMember;

    @Schema(description = "그룹 마스터 유저 ID", example = "42")
    private Long masterId;

    @Schema(description = "그룹 이미지 URL",
        example = "https://kr.object.ncloudstorage.com/your-bucket/groups/1/abc.png")
    private String groupImageUrl;   // ✅ 추가

    @Schema(description = "그룹 멤버 목록")
    private List<GroupMemberDto> members;

    public static GroupInfoDto from(Group group, int memberCount, List<GroupMemberDto> members) {
        return GroupInfoDto.builder()
            .groupId(group.getId())
            .groupName(group.getGroupName())
            .groupType(group.getGroupType())
            .categoryCode(group.getCategory().getCode())
            .categoryName(group.getCategory().getDisplayName())
            .memberCount(memberCount)
            .maxMember(group.getMaxMember())
            .masterId(group.getMaster().getId())
            .groupImageUrl(group.getGroupImageUrl())
            .members(members)
            .build();
    }
}
