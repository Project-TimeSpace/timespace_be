package com.backend.group.Service;

import com.backend.configenum.GlobalEnum;
import com.backend.configenum.GlobalEnum.GroupCategory;
import com.backend.configenum.GlobalEnum.NotificationType;
import com.backend.group.Dto.GroupCreateRequestDto;
import com.backend.group.Dto.GroupInfoDto;
import com.backend.group.Dto.GroupMemberDto;
import com.backend.group.Dto.GroupSummaryDto;
import com.backend.group.Entity.Group;
import com.backend.group.Entity.GroupMembers;
import com.backend.group.Repository.GroupMembersRepository;
import com.backend.group.Repository.GroupRepository;
import com.backend.Notification.Service.NotificationService;
import com.backend.group.error.GroupErrorCode;
import com.backend.response.BusinessException;
import com.backend.shared.ProfileImageService;
import com.backend.user.Entity.User;
import com.backend.user.Repository.UserRepository;
import com.backend.user.Service.UserService;

import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMembersRepository groupMembersRepository;
    private final NotificationService notificationService;
    private final ProfileImageService profileImageService;

    public void validateGroupMaster(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("그룹이 존재하지 않습니다."));
        if (!group.getMaster().getId().equals(userId)) {
            throw new AccessDeniedException("마스터만 사용할 수 있는 기능입니다.");
        }
    }
    private String generateUniqueGroupCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().substring(0, 16); // 예: 16자리
        } while (groupRepository.existsByUniqueCode(code));
        return code;
    }
    public Group getGroupOrThrow(Long groupId) {
        return groupRepository.findById(groupId)
            .orElseThrow(() -> new BusinessException(GroupErrorCode.GROUP_NOT_FOUND));
    }
    private GroupMembers getMembershipOrThrow(Long groupId, Long userId) {
        return groupMembersRepository.findByGroupIdAndUserId(groupId, userId)
            .orElseThrow(() -> new BusinessException(GroupErrorCode.NOT_GROUP_MEMBER));
    }

    public List<GroupSummaryDto> getGroupsByUserId(Long userId) {

        List<Group> groups = groupMembersRepository.findGroupsByUserId(userId);
        if (groups.isEmpty()) return List.of();

        // 2) 멤버 수를 IN 절로 한 방에 집계 (또 1)
        List<Long> groupIds = groups.stream().map(Group::getId).toList();

        Map<Long, Long> countMap = groupMembersRepository.countMembersByGroupIds(groupIds)
            .stream().collect(Collectors.toMap(
                GroupMembersRepository.GroupMemberCount::getGroupId,
                GroupMembersRepository.GroupMemberCount::getMemberCount
            ));

        return groups.stream()
            .map(g -> GroupSummaryDto.from(g, countMap.getOrDefault(g.getId(), 0L)))
            .toList();
    }

    @Transactional(readOnly = true)
    public GroupInfoDto getGroupInfo(Long userId, Long groupId) {
        Group group = getGroupOrThrow(groupId);

        List<GroupMembers> groupMembers = groupMembersRepository.findByGroupIdFetchUser(groupId);
        int memberCount = groupMembers.size();

        boolean isMember = groupMembers.stream()
            .anyMatch(gm -> gm.getUser().getId().equals(userId));
        if (!isMember) {
            throw new BusinessException(GroupErrorCode.NOT_GROUP_MEMBER);
        }

        Long masterId = group.getMaster().getId();
        List<GroupMemberDto> memberDtos = groupMembers.stream()
            .map(gm -> GroupMemberDto.from(gm, masterId))
            .toList();

        return GroupInfoDto.from(group, memberCount, memberDtos);
    }

    @Transactional
    public void createGroup(Long userId, GroupCreateRequestDto request) {
        User user = User.builder().id(userId).build();

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

        GroupMembers member = GroupMembers.builder()
                .group(group)
                .user(User.builder().id(userId).build())
                .isFavorite(false)
                .build();
        groupMembersRepository.save(member);
    }

    @Transactional
    public void createGroup(Long userId, GroupCreateRequestDto request, MultipartFile image) throws IOException {
        User user = User.builder().id(userId).build();

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

        String uploadedUrl = null;
        if (image != null && !image.isEmpty()) {
            uploadedUrl = profileImageService.uploadImage(GlobalEnum.ProfileImageType.GROUP, group.getId(), image, null);
            group.setGroupImageUrl(uploadedUrl);
        }

        GroupMembers member = GroupMembers.builder()
            .group(group)
            .user(User.builder().id(userId).build())
            .isFavorite(false)
            .build();
        groupMembersRepository.save(member);
    }

    @Transactional
    public void changeMaster(Long userId,Long groupId, Long newMasterId) {
        Group group = getGroupOrThrow(groupId);
        GroupMembers membership = getMembershipOrThrow(groupId, newMasterId);

        group.setMaster(membership.getUser());
        groupRepository.save(group);

        String content = String.format("그룹 ‘%s’의 새로운 방장으로 지정되었습니다.", group.getGroupName());
        notificationService.createNotification(
                userId, newMasterId, NotificationType.GROUP_MASTER, content, (long)-1);
    }

    public String getGroupCode(Long groupId) {
        Group group = getGroupOrThrow(groupId);
        return group.getUniqueCode();
    }

    public String resetGroupCode(Long groupId) {
        Group group = getGroupOrThrow(groupId);
        group.setUniqueCode(generateUniqueGroupCode());

        return group.getUniqueCode();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handoverOrCloseOwnedGroups(Long userId) {
        List<Long> groupIds = groupRepository.findIdsByMasterId(userId);

        for (Long groupId : groupIds) {
            Long successorId = groupMembersRepository.findAnyOtherMemberId(groupId, userId);
            if (successorId != null) {
                groupRepository.updateMasterId(groupId, successorId);
            } else {
                groupRepository.deleteById(groupId);
            }
        }
    }
}

