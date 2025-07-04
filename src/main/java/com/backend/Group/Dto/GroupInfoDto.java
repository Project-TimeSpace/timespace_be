package com.backend.Group.Dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupInfoDto {
    private Long groupId;
    private String groupName;
    private String groupType;
    private int memberCount;
    private int maxMember;
    private Long masterId;
    private List<GroupMemberDto> members;
}

