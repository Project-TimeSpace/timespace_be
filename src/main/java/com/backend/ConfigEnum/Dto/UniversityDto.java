package com.backend.ConfigEnum.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor  // 파라미터 없는 기본 생성자를 추가
@AllArgsConstructor // 모든 필드를 파라미터로 받는 생성자를 추가
public class UniversityDto {
    private int code;
    private String name;
}