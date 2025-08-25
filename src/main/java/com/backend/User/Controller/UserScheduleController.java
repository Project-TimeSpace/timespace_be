package com.backend.User.Controller;

import com.backend.Group.Dto.GroupScheduleDto;
import com.backend.Group.Service.GroupScheduleService;
import com.backend.User.Dto.CreateRepeatScheduleDto;
import com.backend.User.Dto.CreateSingleScheduleDto;
import com.backend.User.Dto.UserRepeatScheduleDto;
import com.backend.User.Dto.UserScheduleDto;
import com.backend.User.Service.UserRepeatScheduleService;
import com.backend.User.Service.UserSingleScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user-schedule")
@RequiredArgsConstructor
@PreAuthorize("hasRole('User')")
@Tag(name = "2. 유저 개인의 스케쥴 관련 api")
public class UserScheduleController {

    private final UserSingleScheduleService userSingleScheduleService;
    private final UserRepeatScheduleService userRepeatScheduleService;
    private final GroupScheduleService groupScheduleService;


    @Operation(summary = "1. 기간별 일정 조회-> 개인일정(친구약속 수락한거 포함)", description = "지정된 날짜 범위 내의 단일 및 반복 일정을 조회합니다. YYYY-MM-DD 형식 사용.")
    @GetMapping("/range")
    public ResponseEntity<List<UserScheduleDto>> getSchedulesByPeriod(@AuthenticationPrincipal UserDetails userDetails,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = Long.parseLong(userDetails.getUsername());

        // 1) 단일, 반복 일정 조회
        List<UserScheduleDto> singles = userSingleScheduleService.getSingleSchedulesByPeriod(userId, startDate, endDate);
        List<UserScheduleDto> repeats = userRepeatScheduleService.getRepeatSchedulesByPeriod(userId, startDate, endDate);

        List<UserScheduleDto> combined = new ArrayList<>();
        combined.addAll(singles);
        combined.addAll(repeats);

        return ResponseEntity.ok(combined);
    }

    @Operation(summary = "2-2. 기간별 그룹 일정 조회",
        description = "로그인한 사용자가 속한 모든 그룹의 일정 중, 기간 내 일정만 조회합니다. YYYY-MM-DD 형식 사용.")
    @GetMapping("/range/group-schedule")
    public ResponseEntity<List<GroupScheduleDto>> getMyGroupSchedulesByPeriod(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Long userId = Long.parseLong(userDetails.getUsername());
        List<GroupScheduleDto> list = groupScheduleService.getMyGroupSchedules(userId, startDate, endDate);
        return ResponseEntity.ok(list);
    }

    // ------------------- 3. Single Schedule CRUD -----------------

    @Operation(summary = "3-1.단일 일정 추가", description = "단일 일정을 생성합니다.")
    @PostMapping("/single")
    public ResponseEntity<String> createSingle(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateSingleScheduleDto dto) {
        Long userId = Long.parseLong(userDetails.getUsername());
        userSingleScheduleService.createSingleSchedule(userId, dto);
        return ResponseEntity.ok("단일 일정이 성공적으로 생성되었습니다.");
    }

    @Operation(summary = "3-2. 단일 일정 상세 조회",
            description = "단일 일정의 ID를 받아 해당 일정의 상세 정보를 반환합니다.")
    @GetMapping("/single/{id}")
    public ResponseEntity<UserScheduleDto> getSingleDetail(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        Long userId = Long.parseLong(userDetails.getUsername());
        UserScheduleDto dto = userSingleScheduleService.getSingleScheduleDetail(userId, id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "3-3.단일 일정 수정", description = "CreateSingleScheduleDto의 Not null 필드만 업데이트합니다.")
    @PutMapping("/single/{id}")
    public ResponseEntity<String> updateSingle(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id, @RequestBody CreateSingleScheduleDto dto) {
        Long userId = Long.parseLong(userDetails.getUsername());
        userSingleScheduleService.updateSingleSchedule(userId, id, dto);
        return ResponseEntity.ok("단일 일정이 성공적으로 수정되었습니다.");
    }

    @Operation(summary = "3-4.단일 일정 삭제", description = "단일 스케줄 ID를 받아 해당 일정을 삭제합니다.")
    @DeleteMapping("/single/{id}")
    public ResponseEntity<String> deleteSingle(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        Long userId = Long.parseLong(userDetails.getUsername());
        userSingleScheduleService.deleteSingleSchedule(userId, id);
        return ResponseEntity.ok("단일 일정이 성공적으로 삭제되었습니다.");
    }

    // -----------4. Repeat Schedule CRUD --------------
    @Operation(summary = "4-1.반복 일정 추가", description = "반복 일정을 생성합니다.")
    @PostMapping("/repeat")
    public ResponseEntity<String> createRepeat(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateRepeatScheduleDto dto) {
        Long userId = Long.parseLong(userDetails.getUsername());
        userRepeatScheduleService.createRepeatSchedule(userId, dto);
        return ResponseEntity.ok("반복 일정이 성공적으로 생성되었습니다.");
    }

    @Operation(summary = "4-2. 반복 일정 상세 조회",
            description = "반복 일정의 ID를 받아 해당 일정의 정의(시작일, 종료일, 요일 등) 정보를 반환합니다.")
    @GetMapping("/repeat/{id}")
    public ResponseEntity<UserRepeatScheduleDto> getRepeatDetail(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        Long userId = Long.parseLong(userDetails.getUsername());
        UserRepeatScheduleDto dto = userRepeatScheduleService.getRepeatScheduleDetail(userId, id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary= "4-3. 반복 일정 수정",
            description = "CreateRepeatScheduleDto의 non-null/0 필드만 업데이트합니다.")
    @PutMapping("/repeat/{id}")
    public ResponseEntity<String> updateRepeat(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id, @RequestBody CreateRepeatScheduleDto dto) {
        Long userId = Long.parseLong(userDetails.getUsername());
        userRepeatScheduleService.updateRepeatSchedule(userId, id, dto);
        return ResponseEntity.ok("반복 일정이 성공적으로 수정되었습니다.");
    }

    @Operation(summary = "4-4. 반복 일정 전체 삭제", description = "반복 스케줄 ID를 받아 스케줄 자체를 삭제합니다.")
    @DeleteMapping("/repeat/{id}")
    public ResponseEntity<String> deleteRepeat(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        Long userId = Long.parseLong(userDetails.getUsername());
        userRepeatScheduleService.deleteRepeatSchedule(userId, id);
        return ResponseEntity.ok("반복 일정이 성공적으로 삭제되었습니다.");
    }

    @Operation(summary = "4-5.반복 일정 특정 날짜 삭제", description = "반복 스케줄 ID와 date(YYYY-MM-DD)를 받아 해당 날짜만 예외(삭제) 처리합니다.")
    @DeleteMapping("/repeat/{id}/exception")
    public ResponseEntity<String> deleteRepeatException(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long userId = Long.parseLong(userDetails.getUsername());
        userRepeatScheduleService.deleteRepeatOccurrence(userId, id, date);
        return ResponseEntity.ok("해당 날짜 일정이 성공적으로 예외 처리되었습니다.");
    }
}
