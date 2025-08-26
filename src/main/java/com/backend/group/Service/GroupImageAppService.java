package com.backend.group.Service;

import com.backend.configenum.GlobalEnum;
import com.backend.group.Entity.Group;
import com.backend.group.Repository.GroupRepository;
import com.backend.shared.ProfileImageService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class GroupImageAppService {

	private final GroupRepository groupRepository;
	private final ProfileImageService imageService;

	@Transactional
	public String updateGroupProfileImage(Long groupId, MultipartFile file) throws Exception {
		Group group = groupRepository.findById(groupId)
			.orElseThrow(() -> new IllegalArgumentException("group not found"));

		String newUrl = imageService.uploadImage(GlobalEnum.ProfileImageType.GROUP, groupId, file, group.getGroupImageUrl());
		group.setGroupImageUrl(newUrl);
		return newUrl;
	}

	@Transactional
	public void deleteGroupProfileImage(Long groupId) {
		Group group = groupRepository.findById(groupId)
			.orElseThrow(() -> new IllegalArgumentException("group not found"));

		String url = group.getGroupImageUrl();
		if (url != null && !url.isBlank()) {
			imageService.deleteByUrl(url);
			group.setGroupImageUrl(null);
		}
	}
}
