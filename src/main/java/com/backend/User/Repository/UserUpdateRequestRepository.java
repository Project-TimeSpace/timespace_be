package com.backend.User.Repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import com.backend.ConfigEnum.GlobalEnum;
import com.backend.User.Entity.UserUpdateRequest;

public interface UserUpdateRequestRepository extends JpaRepository<UserUpdateRequest, Long> {
	Optional<UserUpdateRequest> findByUser_IdAndStatus(Long userId, GlobalEnum.RequestStatus status);

	@EntityGraph(attributePaths = "user")
	List<UserUpdateRequest> findAllByStatusOrderByCreatedAtDesc(GlobalEnum.RequestStatus status);
}
