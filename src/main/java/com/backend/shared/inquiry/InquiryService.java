package com.backend.shared.inquiry;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.User.Dto.InquiryRequestDto;
import com.backend.User.Dto.InquiryResponseDto;
import com.backend.User.Error.UserErrorCode;
import com.backend.response.BusinessException;
import com.backend.User.Entity.User;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Service
@AllArgsConstructor
public class InquiryService {

	private final InquiryRepository inquiryRepository;

	/** 진행중(0)인 문의가 있으면 예외, 아니면 신규 생성 */
	@Transactional
	public InquiryResponseDto createInquiry(User user, InquiryRequestDto dto) {
		if (inquiryRepository.existsByUserIdAndStatus(user.getId(), 0)) {
			throw new BusinessException(UserErrorCode.INQUIRY_OPEN_EXISTS);
		}
		Inquiry saved = inquiryRepository.save(
			Inquiry.builder()
				.user(user)
				.title(dto.getTitle())
				.content(dto.getContent())
				.status(0)
				.build()
		);
		return InquiryResponseDto.from(saved);
	}

	/** 내 문의 목록 최신순 조회 */
	@Transactional(readOnly = true)
	public List<InquiryResponseDto> getMyInquiries(Long userId) {
		return inquiryRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
			.stream()
			.map(InquiryResponseDto::from)
			.toList();
	}
}
