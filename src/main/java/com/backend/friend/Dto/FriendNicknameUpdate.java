package com.backend.friend.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendNicknameUpdate {
	@Schema(description = "변경할 닉네임", example = "영훈이", required = true)
	@NotBlank(message = "닉네임을 비워둘 수 없습니다.")
	private String nickname;
}
