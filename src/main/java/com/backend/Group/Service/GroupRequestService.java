package com.backend.Group.Service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.ConfigEnum.GlobalEnum;
import com.backend.Friend.Repository.FriendRepository;
import com.backend.Group.Dto.GroupInviteResponse;
import com.backend.Group.Entity.Group;
import com.backend.Group.Entity.GroupMembers;
import com.backend.Group.Entity.GroupRequest;
import com.backend.Group.Repository.GroupMembersRepository;
import com.backend.Group.Repository.GroupRepository;
import com.backend.Group.Repository.GroupRequestRepository;
import com.backend.Notification.Service.NotificationService;
import com.backend.User.Entity.User;
import com.backend.User.Repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupRequestService {

	private final GroupMembersRepository groupMembersRepository;
	private final GroupRepository groupRepository;
	private final GroupRequestRepository groupRequestRepository;
	private final NotificationService notificationService;

	@Transactional(readOnly = true)
	public List<GroupInviteResponse> getMyReceivedInvites(Long userId) {
		List<GroupRequest> requests =
			groupRequestRepository.findAllByReceiver_IdOrderByRequestedAtDesc(userId);

		List<GroupInviteResponse> invites = requests.stream()
			.map(gr -> GroupInviteResponse.builder()
				.groupId(gr.getGroup().getId())
				.groupName(gr.getGroup().getGroupName())
				.inviterName(gr.getInviter().getUserName())
				.inviterEmail(gr.getInviter().getEmail())
				.status(gr.getStatus() != null ? gr.getStatus().name() : null)
				.requestedAt(gr.getRequestedAt())
				.build())
			.toList();

		return invites;
	}


	@Transactional
	public void acceptInvite(Long userId, Long groupId) {
		// 1) 그룹 존재/권한 체크(그룹 폐기 등)
		Group group = groupRepository.findById(groupId)
			.orElseThrow(() -> new NoSuchElementException("그룹이 존재하지 않습니다."));

		// 2) 이미 멤버면 멱등 처리
		if (groupMembersRepository.existsByGroup_IdAndUser_Id(groupId, userId)) {
			// 이미 멤버면 그냥 OK
			return;
		}

		// 3) 초대 상태 확인 (이메일/친구 초대 공통: 상태 PENDING이어야 함)
		GroupRequest req = groupRequestRepository
			.findByGroup_IdAndReceiver_IdAndStatus(groupId, userId, GlobalEnum.RequestStatus.PENDING)
			.orElseThrow(() -> new IllegalStateException("수락할 초대가 없습니다."));

		// 4) 멤버 등록
		GroupMembers member = GroupMembers.builder()
			.group(group)
			.user(User.builder().id(userId).build())
			.isFavorite(false)
			.build();
		groupMembersRepository.save(member);

		// 5) 초대 상태 업데이트
		//req.setStatus(GlobalEnum.RequestStatus.ACCEPTED);
		//groupRequestRepository.save(req);
		groupRequestRepository.delete(req);

		// 6) 알림: 그룹장에게 알려주기 (type=GROUP_INVITE, targetId=groupId)
		Long masterId = group.getMaster().getId(); // 엔티티 필드명에 맞게 조정
		notificationService.createNotification(
			userId, masterId,
			GlobalEnum.NotificationType.GROUP_INVITE,
			"초대를 수락했습니다: " + group.getGroupName(),
			groupId
		);
	}

	@Transactional
	public void declineInvite(Long userId, Long groupId) {
		int deleted = groupRequestRepository
			.deleteByGroup_IdAndReceiver_IdAndStatus(groupId, userId, GlobalEnum.RequestStatus.PENDING);

		if (deleted == 0) {
			throw new NoSuchElementException("초대가 없습니다.");
		}
	}
}
