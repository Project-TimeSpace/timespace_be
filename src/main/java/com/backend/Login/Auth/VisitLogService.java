package com.backend.Login.Auth;

import com.backend.user.Entity.User;
import com.backend.user.Repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VisitLogService {

    private final VisitLogRepository visitLogRepository;
    private final UserRepository userRepository;

    @Transactional
    public void logVisit(Long userId) {
        // 1) 유저 존재 확인 (선택)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        LocalDate today = LocalDate.now();

        // 2) 오늘 기록 조회
        Optional<VisitLog> opt = visitLogRepository
                .findByUserIdAndVisitDate(userId, today);

        if (opt.isPresent()) {
            VisitLog log = opt.get();
            log.setCount(log.getCount() + 1);
            // 필요하면 마지막 로그인 시각도 업데이트
            log.setCreatedAt(LocalDateTime.now());
            visitLogRepository.save(log);
        } else {
            // 3) 오늘 기록이 없으면 새로 생성
            VisitLog log = VisitLog.builder()
                    .user(user)
                    .visitDate(today)
                    .count(1)
                    .createdAt(LocalDateTime.now())
                    .build();
            visitLogRepository.save(log);
        }
    }
}
