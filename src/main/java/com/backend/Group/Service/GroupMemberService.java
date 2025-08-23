package com.backend.Group.Service;

import com.backend.ConfigEnum.GlobalEnum;
import com.backend.ConfigEnum.GlobalEnum.NotificationType;
import com.backend.ConfigEnum.GlobalEnum.RequestStatus;
import com.backend.Friend.Repository.FriendRepository;
import com.backend.Group.Dto.GroupInviteResponse;
import com.backend.Group.Dto.GroupMemberDto;
import com.backend.Group.Entity.Group;
import com.backend.Group.Entity.GroupMembers;
import com.backend.Group.Entity.GroupRequest;
import com.backend.Group.Entity.GroupScheduleUser;
import com.backend.Group.Repository.GroupMembersRepository;
import com.backend.Group.Repository.GroupRepository;
import com.backend.Group.Repository.GroupRequestRepository;
import com.backend.Notification.Repository.NotificationRepository;
import com.backend.Notification.Service.NotificationService;
import com.backend.User.Entity.User;
import com.backend.User.Repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupMemberService {

    private final GroupMembersRepository groupMembersRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupRequestRepository groupRequestRepository;
    private final FriendRepository friendRepository;
    private final NotificationService notificationService;

    @Transactional
    public List<GroupMemberDto> getGroupMembers(Long groupId) {
        // 1. 그룹 정보 가져오기 (master 판단용)
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("그룹이 존재하지 않습니다."));
        Long masterId = group.getMaster().getId();

        // 2. 그룹 멤버 조회
        List<GroupMembers> members = groupMembersRepository.findByGroupId(groupId);

        // 3. DTO 변환
        return members.stream()
                .map(member -> {
                    User user = member.getUser();
                    return GroupMemberDto.builder()
                            .userId(user.getId())
                            .userName(user.getUserName())
                            .email(user.getEmail())
                            .profileImageUrl(user.getProfileImageUrl())
                            .isMaster(user.getId().equals(masterId))
                            .build();
                })
                .collect(Collectors.toList());
    }

    public void isUserInGroup(Long groupId, Long userId) {
        boolean tf = groupMembersRepository.existsByGroupIdAndUserId(groupId, userId);
        if (!tf) {
            throw new AccessDeniedException("그룹 접근 권한이 없습니다.");
        }
    }

    @Transactional
    public void inviteByEmail(Long inviterId, Long groupId, String email) {
        // 1. 초대 대상 유저 확인
        User inviter = userRepository.findById(inviterId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        User receiver = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 유저가 존재하지 않습니다."));

        // 2. 이미 그룹 멤버인지 확인
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹입니다."));
        boolean alreadyMember = groupMembersRepository.existsByGroupIdAndUserId(groupId, receiver.getId());
        if (alreadyMember) {
            throw new IllegalStateException("해당 유저는 이미 그룹의 멤버입니다.");
        }
        // 3. 이미 초대된 상태인지 확인
        boolean alreadyInvited = groupRequestRepository.existsByGroupIdAndReceiverId(groupId, receiver.getId());
        if (alreadyInvited) {
            throw new IllegalStateException("이미 초대 요청이 전송되었습니다.");
        }

        // 4. 초대 요청 생성
        GroupRequest request = GroupRequest.builder()
                .group(group)
                .inviter(inviter)
                .receiver(receiver)
                .status(RequestStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();
        groupRequestRepository.save(request);

        // 6. 알림 전송
        String content = String.format("%s님이 \"%s\" 그룹에 초대했습니다.",
                inviter.getUserName(), group.getGroupName());
        notificationService.createNotification(
                inviterId,receiver.getId(), NotificationType.GROUP_INVITE, content,request.getId());
    }

    @Transactional
    public void inviteFriend(Long inviterId, Long groupId, Long friendId) {
        // 1. 초대자(User) 조회 & 권한 확인
        User inviter = userRepository.findById(inviterId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        if (!groupMembersRepository.existsByGroupIdAndUserId(groupId, inviterId)) {
            throw new AccessDeniedException("그룹 초대 권한이 없습니다.");
        }

        // 2. 친구 관계 확인
        if (!friendRepository.existsByUserIdAndFriendId(inviterId, friendId)) {
            throw new IllegalArgumentException("해당 유저는 친구가 아닙니다.");
        }

        // 3. 그룹(Group) 조회
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹입니다."));

        // 4. 이미 멤버인지/초대 요청 중인지 검사
        if (groupMembersRepository.existsByGroupIdAndUserId(groupId, friendId)) {
            throw new IllegalStateException("이미 그룹 멤버입니다.");
        }
        if (groupRequestRepository.existsByGroupIdAndReceiverId(groupId, friendId)) {
            throw new IllegalStateException("이미 초대 요청이 전송되었습니다.");
        }

        // 5. 수신자(User) 조회
        User receiver = userRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("초대받을 유저를 찾을 수 없습니다."));

        // 6. 초대 요청 생성
        GroupRequest request = GroupRequest.builder()
                .group(group)
                .inviter(inviter)
                .receiver(receiver)
                .status(RequestStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();
        groupRequestRepository.save(request);

        // 7. 알림 전송
        String content = String.format("%s님이 \"%s\" 그룹에 초대했습니다.", inviter.getUserName(), group.getGroupName());
        notificationService.createNotification(
                inviterId, friendId, NotificationType.GROUP_INVITE, content, request.getId());
    }

    // 이거는 로직 업데이트 필요
    @Transactional
    public void leaveGroup(Long groupId, Long userId) {
        // 1) 멤버십 조회
        GroupMembers membership = groupMembersRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new IllegalArgumentException("그룹 멤버십이 존재하지 않습니다."));

        Group group = membership.getGroup();

        // 2) 현재 그룹 멤버 수 확인
        long memberCount = groupMembersRepository.countByGroupId(groupId);

        // 3) 마스터가 나갈 때
        if (group.getMaster().getId().equals(userId)) {
            if (memberCount == 1) {
                // 3-1) 혼자 남은 마스터라면, 그룹 삭제
                groupMembersRepository.delete(membership);
                groupRepository.delete(group);
                return;
            } else {
                // 3-2) 멤버가 더 있으면, 마스터는 탈퇴 불가
                throw new IllegalStateException("그룹 마스터는 그룹을 탈퇴할 수 없습니다. 먼저 방장을 변경하세요.");
            }
        }

        // 4) 일반 멤버는 그냥 탈퇴
        groupMembersRepository.delete(membership);
    }

    public List<Long> getGroupIdsByUserId(Long userId) {
        return groupMembersRepository.findGroupIdsByUserId(userId);
    }




}
