package com.backend.group.Repository;

import java.util.List;
import java.util.Optional;

import com.backend.group.Entity.GroupRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import jakarta.transaction.Transactional;

public interface GroupRequestRepository extends JpaRepository<GroupRequest, Long> {

    boolean existsByGroupIdAndReceiverId(Long groupId, Long id);

	Optional<GroupRequest> findByGroup_IdAndReceiver_Id(Long groupId, Long receiverId);

	List<GroupRequest> findAllByReceiver_IdOrderByRequestedAtDesc(Long receiverId);

	@Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	int deleteByGroup_IdAndReceiver_Id(
		Long groupId, Long receiverId);

}
