package com.backend.Admin.Controller;

import com.backend.Admin.Dto.SystemNoticeRequestDto;
import com.backend.Admin.Dto.SystemNoticeResponseDto;
import com.backend.Admin.Entity.SystemNotice;
import com.backend.Admin.Service.SystemNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
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
@PreAuthorize("hasRole('Admin')")
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
    @Operation(summary = "1. 시스템 공지 생성",
        description = "관리자 전용. 요청은 SystemNoticeRequestDto로 받고, 응답은 SystemNoticeResponseDto로 반환합니다.")
    @PostMapping
    public ResponseEntity<SystemNoticeResponseDto> create(
        @AuthenticationPrincipal UserDetails ud,
        @RequestBody SystemNoticeRequestDto request) {
        //assertAdmin(ud);
        Long adminId = Long.parseLong(ud.getUsername());
        SystemNoticeResponseDto res = systemNoticeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @Operation(summary = "2. 시스템 공지 목록 조회",
        description = "최신 ID 내림차순으로 전체 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<SystemNoticeResponseDto>> list() {
        return ResponseEntity.ok(systemNoticeService.list());
    }

    @Operation(summary = "3. 시스템 공지 단건 조회",
        description = "공지 ID로 단건 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<SystemNoticeResponseDto> get(@PathVariable Integer id) {
        return ResponseEntity.ok(systemNoticeService.get(id));
    }

    @Operation(summary = "4. 시스템 공지 삭제",
        description = "공지 ID로 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@AuthenticationPrincipal UserDetails ud,
        @PathVariable Integer id) {
        systemNoticeService.delete(id);
        return ResponseEntity.ok("관리자가 공지를 삭제했습니다.");
    }
}
