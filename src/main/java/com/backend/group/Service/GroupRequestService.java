package com.backend.group.Service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.configenum.GlobalEnum;
import com.backend.group.Dto.GroupInviteResponse;
import com.backend.group.Entity.Group;
import com.backend.group.Entity.GroupMembers;
import com.backend.group.Entity.GroupRequest;
import com.backend.group.Repository.GroupMembersRepository;
import com.backend.group.Repository.GroupRepository;
import com.backend.group.Repository.GroupRequestRepository;
import com.backend.Notification.Service.NotificationService;
import com.backend.group.error.GroupErrorCode;
import com.backend.response.BusinessException;
import com.backend.user.Entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupRequestService {

	private final GroupMembersRepository groupMembersRepository;
	private final GroupRepository groupRepository;
	private final GroupRequestRepository groupRequestRepository;
	private final NotificationService notificationService;

	private Group getGroupOrThrow(Long groupId) {
		return groupRepository.findById(groupId)
			.orElseThrow(() -> new BusinessException(GroupErrorCode.GROUP_NOT_FOUND));
	}

	private GroupRequest getInviteOrThrow(Long groupId, Long userId) {
		return groupRequestRepository.findByGroup_IdAndReceiver_Id(groupId, userId)
			.orElseThrow(() -> new BusinessException(GroupErrorCode.GROUP_INVITE_NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public List<GroupInviteResponse> getMyReceivedInvites(Long userId) {
		List<GroupRequest> requests = groupRequestRepository.findAllByReceiver_IdOrderByRequestedAtDesc(userId);

		return requests.stream()
			.map(GroupInviteResponse::from)
			.toList();
	}

	@Transactional
	public void acceptInvite(Long userId, Long groupId) {
		Group group = getGroupOrThrow(groupId);

		if (groupMembersRepository.existsByGroup_IdAndUser_Id(groupId, userId)) {
			return;
		}

		GroupRequest req = getInviteOrThrow(groupId, userId);
		GroupMembers member = GroupMembers.builder()
			.group(group)
			.user(User.builder().id(userId).build())
			.isFavorite(false)
			.build();
		groupMembersRepository.save(member);
		groupRequestRepository.delete(req);

		Long masterId = group.getMaster().getId();
		notificationService.createNotification(userId, masterId, GlobalEnum.NotificationType.GROUP_INVITE,
			"초대를 수락했습니다: " + group.getGroupName(), groupId);
	}

	@Transactional
	public void declineInvite(Long userId, Long groupId) {
		int deleted = groupRequestRepository.deleteByGroup_IdAndReceiver_Id(groupId, userId);

		if (deleted == 0) {
			throw new BusinessException(GroupErrorCode.GROUP_INVITE_NOT_FOUND);
		}
	}
}
