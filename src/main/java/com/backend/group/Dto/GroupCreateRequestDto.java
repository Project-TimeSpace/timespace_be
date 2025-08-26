package com.backend.group.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "GroupCreateRequestDto", description = "그룹 생성 요청 DTO")
public class GroupCreateRequestDto {

    @Schema(description = "그룹 이름", example = "밥약1조")
    private String groupName;

    @Schema(description = "그룹 타입-일단 NORMAL로만", example = "NORMAL")
    private String groupType;

    @Schema(description = "카테고리 코드", example = "3")
    private int category;

    @Schema(description = "최대 멤버 수", example = "10", minimum = "1")
    private int maxMember;
}
