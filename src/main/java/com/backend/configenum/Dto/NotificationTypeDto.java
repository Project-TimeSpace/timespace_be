package com.backend.configenum.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationTypeDto {
    @Schema(description = "알림 타입 코드", example = "1")
    private int code;

    @Schema(description = "알림 타입 이름", example = "FRIEND_REQUEST")
    private String name;
}
