package com.backend.User.Dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryRequestDto {
	private String title;
	private String content;
}
