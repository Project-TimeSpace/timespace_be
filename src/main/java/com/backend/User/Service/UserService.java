package com.backend.User.Service;

import com.backend.User.Dto.UserInfoDto;
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
                .userName(user.getUserName())
                .email(user.getEmail())
                .university(user.getUniversity())
                .major(user.getMajor())
                .phoneNumber(user.getPhoneNumber())
                .selfMemo(user.getSelfMemo())
                .build();
    }

    public void updateMyInfo(Long userId, UserInfoDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (dto.getUserName() != null) {
            user.setUserName(dto.getUserName());
        }
        if (dto.getUniversity() != null) {
            user.setUniversity(dto.getUniversity());
        }
        if (dto.getMajor() != null) {
            user.setMajor(dto.getMajor());
        }
        if (dto.getPhoneNumber() != null) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getSelfMemo() != null) {
            user.setSelfMemo(dto.getSelfMemo());
        }

        userRepository.save(user);
    }

}
