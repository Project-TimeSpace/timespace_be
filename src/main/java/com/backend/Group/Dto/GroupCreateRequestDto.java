package com.backend.Group.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupCreateRequestDto {
    private String groupName;
    private String groupType;
    private int maxMember;
}
