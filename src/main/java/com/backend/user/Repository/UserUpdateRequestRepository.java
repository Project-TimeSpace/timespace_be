package com.backend.user.Repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import com.backend.configenum.GlobalEnum;
import com.backend.user.Entity.UserUpdateRequest;

public interface UserUpdateRequestRepository extends JpaRepository<UserUpdateRequest, Long> {
	Optional<UserUpdateRequest> findByUser_IdAndStatus(Long userId, GlobalEnum.RequestStatus status);

	@EntityGraph(attributePaths = "user")
	List<UserUpdateRequest> findAllByStatusOrderByCreatedAtDesc(GlobalEnum.RequestStatus status);
}
