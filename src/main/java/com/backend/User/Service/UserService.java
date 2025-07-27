package com.backend.User.Service;

import com.backend.ConfigEnum.GlobalEnum.University;
import com.backend.User.Dto.UserInfoDto;
import com.backend.User.Dto.UserUpdateRequestDto;
import com.backend.User.Entity.User;
import com.backend.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserInfoDto getMyInfo(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return UserInfoDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .univCode(user.getUniversity().getCode())
                .univName(user.getUniversity().getDisplayName())
                .phoneNumber(user.getPhoneNumber())
                .birthDate(user.getBirthDate())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    public void updateMyInfo(Long userId, UserUpdateRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (dto.getUserName() != null) {
            user.setUserName(dto.getUserName());
        }
        if (dto.getUnivCode() != null) {
            user.setUniversity(University.fromCode(dto.getUnivCode()));
        }
        if (dto.getPhoneNumber() != null) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getBirthDate() != null) {
            user.setBirthDate(dto.getBirthDate());
        }

        userRepository.save(user);
    }


}
