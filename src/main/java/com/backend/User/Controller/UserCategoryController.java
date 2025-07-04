package com.backend.User.Controller;


import com.backend.User.Dto.UserCategoryDto;
import com.backend.User.Service.UserCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-category")
@RequiredArgsConstructor
@PreAuthorize("hasRole('User')")
public class UserCategoryController {

    private final UserCategoryService userCategoryService;

    @Operation(summary = "1. Home 카테고리 조회", description = "로그인된 사용자의 일정 카테고리를 조회합니다.")
    @GetMapping
    public List<UserCategoryDto> getMyCategories(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return userCategoryService.getUserCategories(userId);
    }

    @Operation(summary = "2. Home 카테고리 추가", description = "최대 8개까지 추가할 수 있습니다. "
            + "DTO로 CategoryName과 Color만 전달해 주세요. categoryId는 순차 배정")
    @PostMapping
    public ResponseEntity<String> addCategory(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserCategoryDto dto) {
        Long userId = Long.parseLong(userDetails.getUsername());
        userCategoryService.addUserCategory(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("카테고리가 성공적으로 추가되었습니다.");
    }

    @Operation(summary = "3. Home 카테고리 수정", description = "기존 카테고리명을 기반으로 해당 카테고리의 이름 또는 색상을 수정합니다.")
    @PutMapping
    public ResponseEntity<String> updateCategory(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserCategoryDto dto) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok("카테고리가 성공적으로 수정되었습니다.");
    }

    @Operation(summary = "카테고리 삭제", description = "카테고리를 삭제하면, categoryId가 업데이트 됩니다. 12345에서 3 삭제하면 기존 4,5가 3,4로 당겨짐")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<String> deleteUserCategory(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer categoryId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        userCategoryService.deleteUserCategory(userId, categoryId);
        return ResponseEntity.ok("카테고리가 성공적으로 삭제되었습니다.");
    }
}
