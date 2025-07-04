package com.backend.Group.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
public class GroupSummaryDto {
    private Long groupId;
    private String groupName;
    private String groupType;         // 카테고리
    private int memberCount;
    private int maxMemberCount;
    //private String groupImageUrl;     // 사진
}

