package com.backend.Group.Service;

import com.backend.ConfigEnum.GlobalEnum.GroupCategory;
import com.backend.ConfigEnum.GlobalEnum.NotificationType;
import com.backend.Group.Dto.GroupCreateRequestDto;
import com.backend.Group.Dto.GroupInfoDto;
import com.backend.Group.Dto.GroupMemberDto;
import com.backend.Group.Dto.GroupSummaryDto;
import com.backend.Group.Entity.Group;
import com.backend.Group.Entity.GroupMembers;
import com.backend.Group.Repository.GroupMembersRepository;
import com.backend.Group.Repository.GroupRepository;
import com.backend.Notification.Service.NotificationService;
import com.backend.User.Entity.User;
import com.backend.User.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMembersRepository groupMembersRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public void validateGroupMaster(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("그룹이 존재하지 않습니다."));
        if (!group.getMaster().getId().equals(userId)) {
            throw new AccessDeniedException("마스터만 사용할 수 있는 기능입니다.");
        }
    }
    public String generateUniqueGroupCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().substring(0, 16); // 예: 16자리
        } while (groupRepository.existsByUniqueCode(code));
        return code;
    }


    /**유저가 속한 그룹 리스트 조회
    public List<GroupSummaryDto> getGroupsByUserId(Long userId) {
        // 1. 유저가 속한 GroupMembers 조회
        List<GroupMembers> memberships = groupMembersRepository.findByUserId(userId);

        List<GroupSummaryDto> result = new ArrayList<>();

        for (GroupMembers membership : memberships) {
            Group group = groupRepository.findById(membership.getGroup().getId())
                    .orElseThrow(() -> new RuntimeException("그룹을 찾을 수 없습니다."));

            int memberCount = groupMembersRepository.countByGroupId(group.getId());

            result.add(GroupSummaryDto.builder()
                    .groupId(group.getId())
                    .groupName(group.getGroupName())
                    .groupType(group.getGroupType())
                    .memberCount(memberCount)
                    .maxMemberCount(group.getMaxMember())
                    .groupImageUrl(null) // groupImage 추후 확장
                    .build());
        }

        return result;
    }*/

    // 이게 1 join으로 해결하는 조금더 빠른 방법
    public List<GroupSummaryDto> getGroupsByUserId(Long userId) {
        return groupMembersRepository.findGroupSummariesByUserId(userId);
    }

    @Transactional
    public GroupInfoDto getGroupInfo(Long userId, Long groupId) {
        // 2. 그룹 정보 조회
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("그룹이 존재하지 않습니다."));

        if(group.getId() != groupId){
            throw  new IllegalArgumentException("");
        }
        // 3. 멤버 목록 조회
        List<GroupMembers> groupMembers = groupMembersRepository.findByGroupId(groupId);
        int memberCount = groupMembers.size();

        // 3) 조회된 리스트로 소속 여부 검사
        boolean isMember = groupMembers.stream()
                .anyMatch(gm -> gm.getUser().getId().equals(userId));
        if (!isMember) {
            throw new AccessDeniedException("해당 그룹에 대한 접근 권한이 없습니다.");
        }

        List<GroupMemberDto> memberDtos = groupMembers.stream()
                .map(gm -> {
                    User user = gm.getUser();
                    return GroupMemberDto.builder()
                            .userId(user.getId())
                            .userName(user.getUserName())
                            .email(user.getEmail())
                            .isMaster(group.getMaster().getId().equals(user.getId()))
                            .build();
                })
                .collect(Collectors.toList());

        return GroupInfoDto.builder()
                .groupId(group.getId())
                .groupName(group.getGroupName())
                .groupType(group.getGroupType())
                .memberCount(memberCount)
                .maxMember(group.getMaxMember())
                .masterId(group.getMaster().getId())
                .members(memberDtos)
                .build();

    }

    @Transactional
    public void createGroup(Long userId, GroupCreateRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. id=" + userId));

        // 1. 그룹 생성
        Group group = Group.builder()
                .groupName(request.getGroupName())
                .category(GroupCategory.fromCode(request.getCategory()))
                .groupType(request.getGroupType())
                .maxMember(request.getMaxMember())
                .master(user)
                .createdAt(LocalDateTime.now())
                .uniqueCode(generateUniqueGroupCode())
                .build();
        groupRepository.save(group);

        // 2. 그룹 생성자는 기본이 그룹 멤버로 추가
        GroupMembers member = GroupMembers.builder()
                .group(group)
                .user(User.builder().id(userId).build())
                .isFavorite(false)
                .build();
        groupMembersRepository.save(member);
    }

    @Transactional
    public void changeMaster(Long userId,Long groupId, Long newMasterId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹이 존재하지 않습니다."));
        // 새로운 마스터가 그룹 멤버인지 확인
        GroupMembers membership = groupMembersRepository.findByGroupIdAndUserId(groupId, newMasterId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자는 그룹 멤버가 아닙니다."));
        // 방장 변경
        group.setMaster(membership.getUser());
        groupRepository.save(group);

        // 5) 새로운 방장에게 알림 전송
        String content = String.format("그룹 ‘%s’의 새로운 방장으로 지정되었습니다.", group.getGroupName());
        notificationService.createNotification(
                userId,                        // 발신자: 기존 방장
                newMasterId,                       // 수신자: 새로운 방장
                NotificationType.GROUP_MASTER,  // 알림 타입: SYSTEM_NOTICE 사용
                content
        );
    }

    public String getGroupCode(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(()->new IllegalArgumentException("코드 오류"));

        return group.getUniqueCode();
    }

    public String resetGroupCode(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(()->new IllegalArgumentException("코드 오류"));

        group.setUniqueCode(generateUniqueGroupCode());

        return group.getUniqueCode();
    }
}

