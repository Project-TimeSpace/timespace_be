package com.backend.User.Service;

import java.util.List;
import java.util.stream.Collectors;

import com.backend.ConfigEnum.GlobalEnum;
import com.backend.ConfigEnum.GlobalEnum.University;
import com.backend.User.Dto.InquiryRequestDto;
import com.backend.User.Dto.InquiryResponseDto;
import com.backend.User.Dto.UserInfoDto;
import com.backend.User.Dto.UserUpdateRequestDto;
import com.backend.User.Entity.Inquiry;
import com.backend.User.Entity.User;
import com.backend.User.Entity.UserUpdateRequest;
import com.backend.User.Error.UserErrorCode;
import com.backend.User.Repository.InquiryRepository;
import com.backend.User.Repository.UserRepository;
import com.backend.User.Repository.UserUpdateRequestRepository;
import com.backend.response.BusinessException;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final InquiryRepository inquiryRepository;
    private final UserUpdateRequestRepository requestRepository;

    public User getUserById(Long id) {
        if (id == null)
            throw new BusinessException(UserErrorCode.USER_ID_REQUIRED);
        return userRepository.findById(id)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public UserInfoDto getMyInfo(Long id) {
        User user = getUserById(id);
        return UserInfoDto.from(user);
    }

    @Transactional
    public void submitUpdateRequest(Long userId, UserUpdateRequestDto dto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        boolean hasAny = dto.getUserName()!=null || dto.getUnivCode()!=null
            || dto.getPhoneNumber()!=null || dto.getBirthDate()!=null;
        if (!hasAny) throw new IllegalArgumentException("변경할 항목이 없습니다.");

        requestRepository.findByUser_IdAndStatus(userId, GlobalEnum.RequestStatus.PENDING)
            .ifPresent(r -> { throw new IllegalStateException("대기 중 요청이 이미 존재합니다. (id=" + r.getId() + ")"); });

        UserUpdateRequest req = UserUpdateRequest.builder()
            .user(user)
            .requestedUserName(dto.getUserName())
            .requestedUnivCode(dto.getUnivCode())
            .requestedPhoneNumber(dto.getPhoneNumber())
            .requestedBirthDate(dto.getBirthDate())
            .status(GlobalEnum.RequestStatus.PENDING) // code=1
            .build();

        requestRepository.save(req);
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
            .createdAt(saved.getCreatedAt())

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
                .createdAt(inq.getCreatedAt())

                .replyContent(inq.getReplyContent())
                .answeredAt(inq.getAnsweredAt() != null ? inq.getAnsweredAt() : null)
                .build()
            )
            .collect(Collectors.toList());
    }

}
