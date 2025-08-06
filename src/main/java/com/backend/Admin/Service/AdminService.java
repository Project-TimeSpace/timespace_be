package com.backend.Admin.Service;

import com.backend.Admin.Dto.AdminInquiryDetailDto;
import com.backend.Admin.Dto.AdminInquirySummaryDto;
import com.backend.ConfigEnum.GlobalEnum.GroupCategory;
import com.backend.Group.Repository.GroupRepository;
import com.backend.User.Entity.Inquiry;
import com.backend.User.Repository.InquiryRepository;
import com.backend.User.Repository.UserRepository;
import com.backend.Login.Auth.VisitLogRepository;

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
            .createdAt(inq.getCreatedAt().toString())
            .replyContent(inq.getReplyContent())
            .answeredAt(inq.getAnsweredAt() != null ? inq.getAnsweredAt().toString() : null)
            .responderId(inq.getResponder() != null ? inq.getResponder().getId() : null)
            .build();
    }

}
