package com.backend.Admin.Controller;

import com.backend.Admin.Entity.SystemNotice;
import com.backend.Admin.Service.AdminService;
import com.backend.Admin.Service.SystemNoticeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "8. Admin 기능 관련 API")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService           adminService;
    private final SystemNoticeService    systemNoticeService;

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

    // --- SystemNotice CRUD ---

    @Operation(summary = "4. 시스템 공지 생성")
    @PostMapping("/system-notices")
    public ResponseEntity<SystemNotice> createSystemNotice(@AuthenticationPrincipal UserDetails ud,
            @RequestBody SystemNotice notice) {
        assertAdmin(ud);
        SystemNotice saved = systemNoticeService.create(notice);
        return ResponseEntity.status(201).body(saved);
    }

    @Operation(summary = "5. 시스템 공지 목록 조회")
    @GetMapping("/system-notices")
    public ResponseEntity<List<SystemNotice>> listSystemNotices(@AuthenticationPrincipal UserDetails ud) {
        //assertAdmin(ud);
        return ResponseEntity.ok(systemNoticeService.listAll());
    }

    @Operation(summary = "6. 특정 시스템 공지 조회")
    @GetMapping("/system-notices/{id}")
    public ResponseEntity<SystemNotice> getSystemNotice(@AuthenticationPrincipal UserDetails ud,
            @PathVariable Integer id) {
        //assertAdmin(ud);
        return ResponseEntity.ok(systemNoticeService.getById(id));
    }

    @Operation(summary = "7. 시스템 공지 수정")
    @PutMapping("/system-notices/{id}")
    public ResponseEntity<SystemNotice> updateSystemNotice(@AuthenticationPrincipal UserDetails ud,
            @PathVariable Integer id, @RequestBody SystemNotice dto) {
        assertAdmin(ud);
        return ResponseEntity.ok(systemNoticeService.update(id, dto));
    }

    @Operation(summary = "8. 시스템 공지 삭제")
    @DeleteMapping("/system-notices/{id}")
    public ResponseEntity<Void> deleteSystemNotice(@AuthenticationPrincipal UserDetails ud,
            @PathVariable Integer id) {
        assertAdmin(ud);
        systemNoticeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
