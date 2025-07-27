package com.backend.Login.Auth;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitLogRepository extends JpaRepository<VisitLog, Long> {

    long countDistinctUserIdByVisitDateBetween(LocalDate start, LocalDate end);

    Optional<VisitLog> findByUserIdAndVisitDate(Long userId, LocalDate today);
}
