package com.backend.Admin.Controller;

import com.backend.Admin.Entity.SystemNotice;
import com.backend.Admin.Service.SystemNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "7. 시스템 공지사항 관련 API")
@RequiredArgsConstructor
public class SystemNoticeController {
    private final SystemNoticeService systemNoticeService;

    private void assertAdmin(UserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_Admin"));
        if (!isAdmin) {
            throw new RuntimeException("관리자 권한이 필요합니다.");
        }
    }


    @PreAuthorize("hasRole('Admin')")
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

    @PreAuthorize("hasRole('Admin')")
    @Operation(summary = "7. 시스템 공지 수정")
    @PutMapping("/system-notices/{id}")
    public ResponseEntity<SystemNotice> updateSystemNotice(@AuthenticationPrincipal UserDetails ud,
            @PathVariable Integer id, @RequestBody SystemNotice dto) {
        assertAdmin(ud);
        return ResponseEntity.ok(systemNoticeService.update(id, dto));
    }

    @PreAuthorize("hasRole('Admin')")
    @Operation(summary = "8. 시스템 공지 삭제")
    @DeleteMapping("/system-notices/{id}")
    public ResponseEntity<Void> deleteSystemNotice(@AuthenticationPrincipal UserDetails ud,
            @PathVariable Integer id) {
        assertAdmin(ud);
        systemNoticeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
