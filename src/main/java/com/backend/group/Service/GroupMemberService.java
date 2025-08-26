package com.backend.group.Service;

import com.backend.configenum.GlobalEnum.NotificationType;
import com.backend.friend.Repository.FriendRepository;
import com.backend.friend.error.FriendErrorCode;
import com.backend.group.Dto.GroupMemberDto;
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
import com.backend.user.Error.UserErrorCode;
import com.backend.user.Repository.UserRepository;
import com.backend.user.Service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupMemberService {

    private final GroupMembersRepository groupMembersRepository;
    private final GroupRepository groupRepository;
    private final GroupRequestRepository groupRequestRepository;
    private final FriendRepository friendRepository;
    private final NotificationService notificationService;
    private final UserService userService;


    // ─────────── 공통 헬퍼 ───────────
    private Group getGroupOrThrow(Long groupId) {
        return groupRepository.findById(groupId)
            .orElseThrow(() -> new BusinessException(GroupErrorCode.GROUP_NOT_FOUND));
    }
    private GroupMembers getMembershipOrThrow(Long groupId, Long userId) {
        return groupMembersRepository.findByGroupIdAndUserId(groupId, userId)
            .orElseThrow(() -> new BusinessException(GroupErrorCode.MEMBERSHIP_NOT_FOUND));
    }
    private void assertNotAlreadyMemberOrInvited(Long groupId, Long receiverId) {
        if (groupMembersRepository.existsByGroupIdAndUserId(groupId, receiverId)) {
            throw new BusinessException(GroupErrorCode.ALREADY_GROUP_MEMBER);
        }
        if (groupRequestRepository.existsByGroupIdAndReceiverId(groupId, receiverId)) {
            throw new BusinessException(GroupErrorCode.INVITE_ALREADY_SENT);
        }
    }
    public List<Long> getGroupIdsByUserId(Long userId) {
        return groupMembersRepository.findGroupIdsByUserId(userId);
    }

    @Transactional
    public List<GroupMemberDto> getGroupMembers(Long groupId) {
        Group group = getGroupOrThrow(groupId);
        Long masterId = group.getMaster().getId();

        return groupMembersRepository.findByGroupId(groupId).stream()
            .map(gm -> GroupMemberDto.from(gm, masterId))
            .collect(Collectors.toList());
    }

    public void isUserInGroup(Long groupId, Long userId) {
        boolean inGroup = groupMembersRepository.existsByGroupIdAndUserId(groupId, userId);
        if (!inGroup) {
            throw new AccessDeniedException(GroupErrorCode.NO_PERMISSION.message());
        }
    }

    @Transactional
    public void inviteByEmail(Long inviterId, Long groupId, String email) {
        User inviter = userService.getUserById(inviterId);
        User receiver = userService.getUserByEmail(email);
        Group group = getGroupOrThrow(groupId);

        assertNotAlreadyMemberOrInvited(groupId, receiver.getId());

        GroupRequest request = GroupRequest.builder()
                .group(group)
                .inviter(inviter)
                .receiver(receiver)
                .requestedAt(LocalDateTime.now())
                .build();
        groupRequestRepository.save(request);

        String content = String.format("%s님이 \"%s\" 그룹에 초대했습니다.",
                inviter.getUserName(), group.getGroupName());
        notificationService.createNotification(
                inviterId,receiver.getId(), NotificationType.GROUP_INVITE, content,request.getId());
    }

    @Transactional
    public void inviteFriend(Long inviterId, Long groupId, Long friendId) {
        User inviter = userService.getUserById(inviterId);

        if (!groupMembersRepository.existsByGroupIdAndUserId(groupId, inviterId)) {
            throw new AccessDeniedException(GroupErrorCode.NO_PERMISSION.message());
        }
        if (!friendRepository.existsByUserIdAndFriendId(inviterId, friendId)) {
            throw new BusinessException(FriendErrorCode.RELATION_NOT_FOUND);
        }
        User receiver = userService.getUserById(friendId);

        Group group = getGroupOrThrow(groupId);
        assertNotAlreadyMemberOrInvited(groupId, friendId);

        GroupRequest request = GroupRequest.builder()
                .group(group)
                .inviter(inviter)
                .receiver(receiver)
                .requestedAt(LocalDateTime.now())
                .build();
        groupRequestRepository.save(request);

        String content = String.format("%s님이 \"%s\" 그룹에 초대했습니다.", inviter.getUserName(), group.getGroupName());
        notificationService.createNotification(
                inviterId, friendId, NotificationType.GROUP_INVITE, content, request.getId());
    }

    @Transactional
    public void leaveGroup(Long groupId, Long userId) {
        GroupMembers membership = getMembershipOrThrow(groupId, userId);
        Group group = membership.getGroup();

        long memberCount = groupMembersRepository.countByGroupId(groupId);

        if (group.getMaster().getId().equals(userId)) {
            if (memberCount == 1) {
                groupMembersRepository.delete(membership);
                groupRepository.delete(group);
                return;
            }
            throw new BusinessException(GroupErrorCode.MASTER_CANNOT_LEAVE);
        }

        groupMembersRepository.delete(membership);
    }
}
