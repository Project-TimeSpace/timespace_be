package com.backend.Group.Service;

import com.backend.Config.GlobalEnum.RequestStatus;
import com.backend.Friend.Repository.FriendRepository;
import com.backend.Group.Dto.GroupMemberDto;
import com.backend.Group.Entity.Group;
import com.backend.Group.Entity.GroupMembers;
import com.backend.Group.Entity.GroupRequest;
import com.backend.Group.Repository.GroupMembersRepository;
import com.backend.Group.Repository.GroupRepository;
import com.backend.Group.Repository.GroupRequestRepository;
import com.backend.User.Entity.User;
import com.backend.User.Repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupMemberService {

    private final GroupMembersRepository groupMembersRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupRequestRepository groupRequestRepository;
    private final FriendRepository friendRepository;

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
                            .isMaster(user.getId().equals(masterId))
                            .build();
                })
                .collect(Collectors.toList());
    }

    public boolean isUserInGroup(Long groupId, Long userId) {
        return groupMembersRepository.existsByGroupIdAndUserId(groupId, userId);
    }

    public void inviteByEmail(Long inviterId, Long groupId, String email) {
        // 1. 초대 대상 유저 확인
        User receiver = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 유저가 존재하지 않습니다."));

        // 2. 이미 그룹 멤버인지 확인
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
                .group(Group.builder().id(groupId).build())
                .inviter(User.builder().id(inviterId).build())
                .receiver(receiver)
                .status(RequestStatus.PENDING.toString())
                .requestedAt(LocalDateTime.now())
                .build();
        groupRequestRepository.save(request);
    }

    public void inviteFriend(Long inviterId, Long groupId, Long friendId) {
        // 1. 초대자가 그룹 소속인지 확인
        if (!groupMembersRepository.existsByGroupIdAndUserId(groupId, inviterId)) {
            throw new AccessDeniedException("그룹 초대 권한이 없습니다.");
        }

        // 2. 친구 관계 확인
        boolean isFriend = friendRepository.existsByUserIdAndFriendId(inviterId, friendId);
        if (!isFriend) {
            throw new IllegalArgumentException("해당 유저는 친구가 아닙니다.");
        }

        // 3. 이미 멤버인지 확인
        if (groupMembersRepository.existsByGroupIdAndUserId(groupId, friendId)) {
            throw new IllegalStateException("이미 그룹 멤버입니다.");
        }

        // 4. 이미 초대했는지 확인
        if (groupRequestRepository.existsByGroupIdAndReceiverId(groupId, friendId)) {
            throw new IllegalStateException("이미 초대 요청이 전송되었습니다.");
        }

        // 5. 초대 요청 생성
        GroupRequest request = GroupRequest.builder()
                .group(Group.builder().id(groupId).build())
                .inviter(User.builder().id(inviterId).build())
                .receiver(User.builder().id(friendId).build())
                .status("PENDING")
                .requestedAt(LocalDateTime.now())
                .build();
        groupRequestRepository.save(request);
    }

}
