package com.backend.User.Repository;

import java.time.LocalDateTime;
import java.util.List;

import com.backend.User.Entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
	boolean existsByUserIdAndStatus(Long userId, Integer status);
	List<Inquiry> findAllByUserIdOrderByCreatedAtDesc(Long userId);
	List<Inquiry> findAllByStatusOrderByCreatedAtAsc(Integer status);

	List<Inquiry> findAllByOrderByCreatedAtDesc();
	List<Inquiry> findAllByResponderIdOrderByCreatedAtDesc(Long responderId);

	List<Inquiry> findAllByCreatedAtBetweenOrderByCreatedAtDesc(
		LocalDateTime start,
		LocalDateTime end
	);

	List<Inquiry> findAllByStatusAndCreatedAtBetweenOrderByCreatedAtAsc(
		Integer status,
		LocalDateTime start,
		LocalDateTime end
	);

	List<Inquiry> findAllByResponderIdAndCreatedAtBetweenOrderByCreatedAtDesc(
		Long responderId,
		LocalDateTime start,
		LocalDateTime end
	);
}
