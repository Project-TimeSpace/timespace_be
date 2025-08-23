package com.backend.User.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.backend.ConfigEnum.GlobalEnum;
import com.backend.SharedFunction.ProfileImageService;
import com.backend.User.Entity.User;
import com.backend.User.Repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserProfileAppService {

	private final UserRepository userRepository;
	private final ProfileImageService imageService;

	@Transactional
	public String updateMyProfileImage(Long userId, MultipartFile file) throws Exception {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("user not found"));
		String newUrl = imageService.uploadImage(GlobalEnum.ProfileImageType.USER,userId, file, user.getProfileImageUrl());
		user.setProfileImageUrl(newUrl);   // DB에는 URL만 저장
		return newUrl;
	}

	@Transactional
	public void deleteMyProfileImage(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("user not found"));

		String url = user.getProfileImageUrl();

		if (url != null && !url.isBlank()) {
			imageService.deleteByUrl(url);
			user.setProfileImageUrl(null);
		}
	}
}

