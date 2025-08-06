package com.backend.User.Service;

import java.util.List;
import java.util.stream.Collectors;

import com.backend.ConfigEnum.GlobalEnum.University;
import com.backend.User.Dto.InquiryRequestDto;
import com.backend.User.Dto.InquiryResponseDto;
import com.backend.User.Dto.UserInfoDto;
import com.backend.User.Dto.UserUpdateRequestDto;
import com.backend.User.Entity.Inquiry;
import com.backend.User.Entity.User;
import com.backend.User.Repository.InquiryRepository;
import com.backend.User.Repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final InquiryRepository inquiryRepository;

    public UserInfoDto getMyInfo(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return UserInfoDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .univCode(user.getUniversity().getCode())
                .univName(user.getUniversity().getDisplayName())
                .phoneNumber(user.getPhoneNumber())
                .birthDate(user.getBirthDate())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    @Transactional
    public void updateMyInfo(Long userId, UserUpdateRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (dto.getUserName() != null) {
            user.setUserName(dto.getUserName());
        }
        if (dto.getUnivCode() != null) {
            user.setUniversity(University.fromCode(dto.getUnivCode()));
        }
        if (dto.getPhoneNumber() != null) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getBirthDate() != null) {
            user.setBirthDate(dto.getBirthDate());
        }

        userRepository.save(user);
    }

    @Transactional
    public InquiryResponseDto createInquiry(Long userId, InquiryRequestDto dto) {
        if (inquiryRepository.existsByUserIdAndStatus(userId, 0)) {
            throw new IllegalStateException("진행 중인 문의가 있어 새 문의를 등록할 수 없습니다.");
        }
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id=" + userId));

        Inquiry saved = inquiryRepository.save(
            Inquiry.builder()
                .user(user)
                .title(dto.getTitle())
                .content(dto.getContent())
                .status(0)
                .build()
        );

        return InquiryResponseDto.builder()
            .inquiryId(saved.getId())
            .title(saved.getTitle())
            .content(saved.getContent())
            .status(saved.getStatus())
            .createdAt(saved.getCreatedAt().toString())
            .updatedAt(saved.getUpdatedAt().toString())
            .replyContent(null)
            .answeredAt(null)
            .build();
    }

    /** 6. 내 문의 내역 조회 */
    @Transactional(readOnly = true)
    public List<InquiryResponseDto> getMyInquiries(Long userId) {
        return inquiryRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(inq -> InquiryResponseDto.builder()
                .inquiryId(inq.getId())
                .title(inq.getTitle())
                .content(inq.getContent())
                .status(inq.getStatus())
                .createdAt(inq.getCreatedAt().toString())
                .updatedAt(inq.getUpdatedAt().toString())
                .replyContent(inq.getReplyContent())
                .answeredAt(inq.getAnsweredAt() != null ? inq.getAnsweredAt().toString() : null)
                .build()
            )
            .collect(Collectors.toList());
    }

}
