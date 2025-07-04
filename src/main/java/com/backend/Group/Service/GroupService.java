package com.backend.Group.Service;

import com.backend.Config.GlobalEnum;
import com.backend.Config.GlobalEnum.RequestStatus;
import com.backend.Friend.Repository.FriendRepository;
import com.backend.Group.Dto.GroupCreateRequestDto;
import com.backend.Group.Dto.GroupDto;
import com.backend.Group.Dto.GroupInfoDto;
import com.backend.Group.Dto.GroupMemberDto;
import com.backend.Group.Dto.GroupSummaryDto;
import com.backend.Group.Entity.Group;
import com.backend.Group.Entity.GroupMembers;
import com.backend.Group.Entity.GroupRequest;
import com.backend.Group.Repository.GroupMembersRepository;
import com.backend.Group.Repository.GroupRepository;
import com.backend.Group.Repository.GroupRequestRepository;
import com.backend.User.Entity.User;
import com.backend.User.Repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMembersRepository groupMembersRepository;


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
        // return groupMembersRepository.findGroupSummariesByUserId(userId);
        return null;
    }


    public void createGroup(Long userId, GroupCreateRequestDto request) {
        // 1. 그룹 생성
        Group group = Group.builder()
                .groupName(request.getGroupName())
                .groupType(request.getGroupType())
                .maxMember(request.getMaxMember())
                .master(User.builder().id(userId).build())
                .createdAt(LocalDateTime.now())
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


    public GroupInfoDto getGroupInfo(Long userId, Long groupId) {
        // 1. 그룹 소속 여부 확인
        boolean isMember = groupMembersRepository.existsByGroupIdAndUserId(groupId, userId);
        if (!isMember) {
            throw new AccessDeniedException("해당 그룹에 대한 접근 권한이 없습니다.");
        }

        // 2. 그룹 정보 조회
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("그룹이 존재하지 않습니다."));
        int memberCount = groupMembersRepository.countByGroupId(groupId);

        // 3. 멤버 목록 조회
        List<GroupMembers> groupMembers = groupMembersRepository.findByGroupId(groupId);
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



}

