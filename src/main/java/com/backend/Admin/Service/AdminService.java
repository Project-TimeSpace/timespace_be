package com.backend.Admin.Service;

import com.backend.Admin.Dto.AdminInquiryDetailDto;
import com.backend.Admin.Dto.AdminInquirySummaryDto;
import com.backend.Admin.Dto.UserUpdateRequestAdminDto;
import com.backend.Admin.Entity.Admin;
import com.backend.Admin.Repository.AdminRepository;
import com.backend.ConfigEnum.GlobalEnum;
import com.backend.ConfigEnum.GlobalEnum.GroupCategory;
import com.backend.Group.Repository.GroupRepository;
import com.backend.Notification.Entity.Notification;
import com.backend.Notification.Repository.NotificationRepository;
import com.backend.shared.inquiry.Inquiry;
import com.backend.User.Entity.User;
import com.backend.User.Entity.UserUpdateRequest;
import com.backend.shared.inquiry.InquiryRepository;
import com.backend.User.Repository.UserRepository;
import com.backend.Login.Auth.VisitLogRepository;
import com.backend.User.Repository.UserUpdateRequestRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final VisitLogRepository visitLogRepository;
    private final GroupRepository groupRepository;
    private final InquiryRepository inquiryRepository;
    private final AdminRepository adminRepository;
    private final UserUpdateRequestRepository requestRepository;
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public long getTotalUserCount() {
        return userRepository.count();
    }

    @Transactional(readOnly = true)
    public long getVisitUserCount(LocalDate start, LocalDate end) {
        return visitLogRepository
                .countDistinctUserIdByVisitDateBetween(start, end);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getGroupCountByCategory() {
        List<Object[]> results = groupRepository.countGroupByCategory();
        Map<String, Long> counts = new LinkedHashMap<>();
        long total = 0L;

        for (Object[] row : results) {
            Integer categoryCode = (Integer) row[0];
            Long    cnt          = (Long)    row[1];
            total += cnt;
            String categoryName = GroupCategory.fromCode(categoryCode).getDisplayName();
            counts.put(categoryName, cnt);
        }
        counts.put("전체", total);
        return counts;
    }

    /**
     * @param type    "all" | "pending" | "mine"
     * @param start   조회 시작일시 (inclusive), null 허용
     * @param end     조회 종료일시 (inclusive), null 허용
     */
    @Transactional(readOnly = true)
    public List<AdminInquirySummaryDto> listInquiries(
        Long adminId,
        String type,
        LocalDateTime start,
        LocalDateTime end
    ) {
        List<Inquiry> raw;
        boolean hasRange = (start != null && end != null);

        switch (type.toLowerCase()) {
            case "pending":
                if (hasRange) {
                    raw = inquiryRepository
                        .findAllByStatusAndCreatedAtBetweenOrderByCreatedAtAsc(0, start, end);
                } else {
                    raw = inquiryRepository.findAllByStatusOrderByCreatedAtAsc(0);
                }
                break;

            case "mine":
                if (hasRange) {
                    raw = inquiryRepository
                        .findAllByResponderIdAndCreatedAtBetweenOrderByCreatedAtDesc(adminId, start, end);
                } else {
                    raw = inquiryRepository.findAllByResponderIdOrderByCreatedAtDesc(adminId);
                }
                break;

            case "all":
            default:
                if (hasRange) {
                    raw = inquiryRepository
                        .findAllByCreatedAtBetweenOrderByCreatedAtDesc(start, end);
                } else {
                    raw = inquiryRepository.findAllByOrderByCreatedAtDesc();
                }
                break;
        }

        return raw.stream()
            .map(inq -> AdminInquirySummaryDto.builder()
                .inquiryId(inq.getId())
                .title(inq.getTitle())
                .userId(inq.getUser().getId())
                .userName(inq.getUser().getUserName())
                .status(inq.getStatus())
                .createdAt(inq.getCreatedAt().toString())
                .build()
            )
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AdminInquiryDetailDto getInquiryDetail(Long inquiryId) {
        Inquiry inq = inquiryRepository.findById(inquiryId)
            .orElseThrow(() -> new IllegalArgumentException("문의가 없습니다. id=" + inquiryId));

        return AdminInquiryDetailDto.builder()
            .inquiryId(inq.getId())
            .title(inq.getTitle())
            .content(inq.getContent())
            .userId(inq.getUser().getId())
            .userName(inq.getUser().getUserName())
            .status(inq.getStatus())
            .createdAt(inq.getCreatedAt())                      // LocalDateTime 그대로
            .replyContent(inq.getReplyContent())
            .answeredAt(inq.getAnsweredAt())                    // LocalDateTime 그대로
            .adminName(inq.getResponder() != null
                ? inq.getResponder().getAdminName() : null)     // 관리자 이름 매핑
            .build();
    }

    @Transactional
    public void answerInquiry(Integer adminId, Long inquiryId, String reply) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
            .orElseThrow(() -> new IllegalArgumentException("해당 문의가 존재하지 않습니다."));

        // 이미 답변 완료라면 막기 (정책에 따라 허용/덮어쓰기 가능)
        if (inquiry.getStatus() != null && inquiry.getStatus() == 1) { // 0: 진행중, 1: 완료
            throw new IllegalStateException("이미 답변이 등록된 문의입니다.");
        }

        Admin admin = adminRepository.findById(adminId)
            .orElseThrow(() -> new IllegalArgumentException("관리자 계정을 찾을 수 없습니다."));

        inquiry.setReplyContent(reply);
        inquiry.setResponder(admin);
        inquiry.setAnsweredAt(LocalDateTime.now());
        inquiry.setStatus(1);

        inquiryRepository.save(inquiry);

        // (선택) 사용자에게 알림
        // notificationService.createNotification(
        //     adminId, inquiry.getUser().getId(),
        //     GlobalEnum.NotificationType.SYSTEM_NOTICE,
        //     "문의에 대한 답변이 등록되었습니다.",
        //     inquiry.getId()
        // );
    }


    @Transactional(readOnly = true)
    public List<UserUpdateRequestAdminDto> listByStatus(GlobalEnum.RequestStatus status) {
        List<UserUpdateRequest> rows = requestRepository.findAllByStatusOrderByCreatedAtDesc(status);

        return rows.stream().map(uur -> {
            var user = uur.getUser();

            String  oldUserName  = user.getUserName();
            Integer oldUnivCode  = null;
            // University가 enum이라면 getCode() 같은 메서드가 있을 것이라 가정
            if (user.getUniversity() != null) {
                oldUnivCode = user.getUniversity() instanceof GlobalEnum.University
                    ? ((GlobalEnum.University) user.getUniversity()).getCode()
                    : null;
            }
            String  oldPhone     = user.getPhoneNumber();
            var     oldBirth     = user.getBirthDate();

            // NEW(요청된 값)
            String  newUserName  = uur.getRequestedUserName();
            Integer newUnivCode  = uur.getRequestedUnivCode();
            String  newPhone     = uur.getRequestedPhoneNumber();
            var     newBirth     = uur.getRequestedBirthDate();

            return UserUpdateRequestAdminDto.builder()
                .requestId(uur.getId())
                .userId(user.getId())

                .oldUserName(oldUserName)
                .oldUnivCode(oldUnivCode)
                .oldPhoneNumber(oldPhone)
                .oldBirthDate(oldBirth)

                .newUserName(newUserName)
                .newUnivCode(newUnivCode)
                .newPhoneNumber(newPhone)
                .newBirthDate(newBirth)

                .statusCode(uur.getStatus().getCode())
                .statusName(uur.getStatus().getDisplayName())
                .reviewReason(uur.getReviewReason())
                .createdAt(uur.getCreatedAt())
                .reviewedAt(uur.getReviewedAt())
                .build();
        }).toList();
    }

    @Transactional
    public void approve(Long adminUserId, Long requestId) {
        UserUpdateRequest req = requestRepository.findById(requestId)
            .orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없습니다."));
        if (req.getStatus() != GlobalEnum.RequestStatus.PENDING)
            throw new IllegalStateException("이미 처리된 요청입니다.");

        User user = req.getUser();
        if (req.getRequestedUserName()!=null) user.setUserName(req.getRequestedUserName());
        if (req.getRequestedUnivCode()!=null) user.setUniversity(GlobalEnum.University.fromCode(req.getRequestedUnivCode()));
        if (req.getRequestedPhoneNumber()!=null) user.setPhoneNumber(req.getRequestedPhoneNumber());
        if (req.getRequestedBirthDate()!=null) user.setBirthDate(req.getRequestedBirthDate());

        req.setStatus(GlobalEnum.RequestStatus.ACCEPTED); // code=2
        req.setAdminId(adminUserId);
        req.setReviewedAt(LocalDateTime.now());

        // 알림
        Notification noti = Notification.builder()
            .sender(userRepository.findById(adminUserId).orElse(null))
            .user(user)
            .type(GlobalEnum.NotificationType.SYSTEM_NOTICE)
            .targetId(null)
            .content("프로필 정보 변경 요청이 승인되었습니다.")
            .isRead(false)
            .createdAt(LocalDateTime.now())
            .build();
        notificationRepository.save(noti);
    }

    @Transactional
    public void reject(Long adminUserId, Long requestId, String reason) {
        UserUpdateRequest req = requestRepository.findById(requestId)
            .orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없습니다."));
        if (req.getStatus() != GlobalEnum.RequestStatus.PENDING)
            throw new IllegalStateException("이미 처리된 요청입니다.");

        req.setStatus(GlobalEnum.RequestStatus.REJECTED); // code=3
        req.setAdminId(adminUserId);
        req.setReviewReason(reason);
        req.setReviewedAt(LocalDateTime.now());

        Notification noti = Notification.builder()
            .sender(userRepository.findById(adminUserId).orElse(null))
            .user(req.getUser())
            .type(GlobalEnum.NotificationType.SYSTEM_NOTICE)
            .targetId(null)
            .content("프로필 정보 변경 요청이 반려되었습니다. 사유: " + (reason==null? "-" : reason))
            .isRead(false)
            .createdAt(LocalDateTime.now())
            .build();
        notificationRepository.save(noti);
    }
}
