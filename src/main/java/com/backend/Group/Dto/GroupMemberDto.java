package com.backend.Group.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupMemberDto {
    private Long userId;
    private String userName;
    private String email;
    private boolean isMaster;
}
