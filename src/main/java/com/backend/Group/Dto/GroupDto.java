package com.backend.Group.Dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupDto {
    private Long groupId;
    private String groupName;
    private String groupType;
    private int maxMember;
    private LocalDateTime createdAt;
}

