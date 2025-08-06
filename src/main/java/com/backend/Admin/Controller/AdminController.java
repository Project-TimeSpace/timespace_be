package com.backend.Admin.Controller;

import com.backend.Admin.Dto.AdminInquiryDetailDto;
import com.backend.Admin.Dto.AdminInquirySummaryDto;
import com.backend.Admin.Entity.SystemNotice;
import com.backend.Admin.Service.AdminService;
import com.backend.Admin.Service.SystemNoticeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@PreAuthorize("hasRole('Admin')")
@RequestMapping("/api/v1/admin")
@Tag(name = "5. Admin 기능 관련 API")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    private void assertAdmin(UserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_Admin"));
        if (!isAdmin) {
            throw new RuntimeException("관리자 권한이 필요합니다.");
        }
    }

    @Operation(summary = "1. 전체 회원 수 조회")
    @GetMapping("/users/count")
    public ResponseEntity<Long> getTotalUserCount(@AuthenticationPrincipal UserDetails ud) {
        assertAdmin(ud);
        return ResponseEntity.ok(adminService.getTotalUserCount());
    }

    @Operation(summary = "2. 특정 날짜 범위에 방문한 유저 수 조회")
    @GetMapping("/users/visit-count")
    public ResponseEntity<Long> getVisitUserCount(@AuthenticationPrincipal UserDetails ud,
            @RequestParam String startDate, @RequestParam String endDate) {
        assertAdmin(ud);
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end   = LocalDate.parse(endDate);
        return ResponseEntity.ok(adminService.getVisitUserCount(start, end));
    }

    @Operation(summary = "3. 전체 그룹 수 및 카테고리별 그룹 수 조회")
    @GetMapping("/groups/count-by-category")
    public ResponseEntity<Map<String, Long>> getGroupCountByCategory(@AuthenticationPrincipal UserDetails ud) {
        assertAdmin(ud);
        return ResponseEntity.ok(adminService.getGroupCountByCategory());
    }

    @Operation(summary = "문의 목록 조회",
        description = "type=all|pending|mine, start/end으로 기간 필터링(ISO 날짜시간)")
    @GetMapping("/inquiries")
    public ResponseEntity<List<AdminInquirySummaryDto>> listInquiries(@AuthenticationPrincipal UserDetails ud,
        @RequestParam(defaultValue = "all") String type,
        @RequestParam(required = false) LocalDateTime start,
        @RequestParam(required = false) LocalDateTime end) {
        Long adminId = Long.parseLong(ud.getUsername());
        return ResponseEntity.ok(adminService.listInquiries(adminId, type, start, end));
    }

    @Operation(summary = "문의 상세 조회",
        description = "문의 ID로 단건 상세 정보를 반환합니다.")
    @GetMapping("/inquiries/{id}")
    public ResponseEntity<AdminInquiryDetailDto> getInquiryDetail(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getInquiryDetail(id));
    }
}
