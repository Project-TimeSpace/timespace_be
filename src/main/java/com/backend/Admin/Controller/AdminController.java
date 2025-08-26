package com.backend.Admin.Controller;

import com.backend.Admin.Dto.AdminInquiryAnswerRequestDto;
import com.backend.Admin.Dto.AdminInquiryDetailDto;
import com.backend.Admin.Dto.AdminInquirySummaryDto;
import com.backend.Admin.Dto.UserUpdateRequestAdminDto;
import com.backend.Admin.Service.AdminService;
import com.backend.configenum.GlobalEnum;
import com.backend.user.Repository.UserUpdateRequestRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

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
@Tag(name = "6. Admin 기능 관련 API")
@AllArgsConstructor
public class AdminController {

    private final UserUpdateRequestRepository requestRepository;
    private final AdminService adminService;

    private void assertAdmin(UserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            throw new RuntimeException("관리자 권한이 필요합니다.");
        }
    }

    @Operation(summary = "1. 전체 회원 수 조회")
    @GetMapping("/users/count")
    public ResponseEntity<Long> getTotalUserCount(@AuthenticationPrincipal UserDetails ud) {
        //assertAdmin(ud);
        return ResponseEntity.ok(adminService.getTotalUserCount());
    }

    @Operation(summary = "2. 특정 날짜 범위에 방문한 유저 수 조회")
    @GetMapping("/users/visit-count")
    public ResponseEntity<Long> getVisitUserCount(@AuthenticationPrincipal UserDetails ud,
            @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {

        return ResponseEntity.ok(adminService.getVisitUserCount(startDate, endDate));
    }

    @Operation(summary = "3. 전체 그룹 수 및 카테고리별 그룹 수 조회 (에러상태임)")
    @GetMapping("/groups/count-by-category")
    public ResponseEntity<Map<String, Long>> getGroupCountByCategory(@AuthenticationPrincipal UserDetails ud) {
        //assertAdmin(ud);
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

    @Operation(summary = "문의 상세 조회", description = "문의 ID로 단건 상세 정보를 반환합니다.")
    @GetMapping("/inquiries/{inqueryId}")
    public ResponseEntity<AdminInquiryDetailDto> getInquiryDetail(@PathVariable Long inqueryId) {
        return ResponseEntity.ok(adminService.getInquiryDetail(inqueryId));
    }

    @Operation(summary = "문의 답변 등록", description = "관리자가 특정 문의에 대해 답변을 등록합니다.")
    @PostMapping("/inquiries/{inquiryId}/answer")
    public ResponseEntity<String> answerInquiry(@AuthenticationPrincipal UserDetails adminDetails,
        @PathVariable Long inquiryId,
        @RequestBody AdminInquiryAnswerRequestDto request
    ) {
        Integer adminId = Integer.parseInt(adminDetails.getUsername());
        adminService.answerInquiry(adminId, inquiryId, request.getReplyContent());
        return ResponseEntity.ok("정상적으로 관리자 답변이 등록되었습니다.");
    }


    // user 개인정보 변경요청
    @Operation(summary="1. 변경 요청 대기 목록 조회", description="statusCode(1=대기,2=수락,3=거절). 기본 1")
    @GetMapping("/list")
    public ResponseEntity<List<UserUpdateRequestAdminDto>> list(
        @RequestParam(required=false, defaultValue="PENDING") GlobalEnum.RequestStatus status) {

        List<UserUpdateRequestAdminDto> rows = adminService.listByStatus(status);
        return ResponseEntity.ok(rows);
    }

    @Operation(summary="2. 변경 요청 승인")
    @PostMapping("/{requestId}/approve")
    public ResponseEntity<Map<String,String>> approve(@AuthenticationPrincipal UserDetails admin,
        @PathVariable Long requestId) {
        Long adminId = Long.parseLong(admin.getUsername());
        adminService.approve(adminId, requestId);
        return ResponseEntity.ok(Map.of("message","승인 처리 완료"));
    }

    public record RejectionDto(String reason) {}
    @Operation(summary="3. 변경 요청 거절")
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<Map<String,String>> reject(@AuthenticationPrincipal UserDetails admin,
        @PathVariable Long requestId, @RequestBody RejectionDto body) {
        Long adminId = Long.parseLong(admin.getUsername());
        adminService.reject(adminId, requestId, body==null?null:body.reason());
        return ResponseEntity.ok(Map.of("message","거절 처리 완료"));
    }

}
