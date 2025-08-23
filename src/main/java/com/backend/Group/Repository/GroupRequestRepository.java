package com.backend.Group.Repository;

import java.util.List;
import java.util.Optional;

import com.backend.ConfigEnum.GlobalEnum;
import com.backend.Group.Entity.GroupRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import jakarta.transaction.Transactional;

public interface GroupRequestRepository extends JpaRepository<GroupRequest, Long> {

    boolean existsByGroupIdAndReceiverId(Long groupId, Long id);

	Optional<GroupRequest> findByGroup_IdAndReceiver_IdAndStatus(Long groupId, Long receiverId, GlobalEnum.RequestStatus status);

	List<GroupRequest> findAllByReceiver_IdOrderByRequestedAtDesc(Long receiverId);

	@Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	int deleteByGroup_IdAndReceiver_IdAndStatus(
		Long groupId, Long receiverId, GlobalEnum.RequestStatus status);

}
