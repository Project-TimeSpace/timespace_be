package com.backend.User.Service;

import java.util.List;
import com.backend.ConfigEnum.GlobalEnum;
import com.backend.ConfigEnum.GlobalEnum.ProfileImageType;
import com.backend.Group.Service.GroupService;
import com.backend.shared.ProfileImageService;
import com.backend.User.Dto.InquiryRequestDto;
import com.backend.User.Dto.InquiryResponseDto;
import com.backend.User.Dto.UserInfoDto;
import com.backend.User.Dto.UserUpdateRequestDto;
import com.backend.User.Entity.User;
import com.backend.User.Entity.UserUpdateRequest;
import com.backend.User.Error.UserErrorCode;
import com.backend.User.Repository.UserRepository;
import com.backend.User.Repository.UserUpdateRequestRepository;
import com.backend.response.BusinessException;
import com.backend.shared.inquiry.InquiryService;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserUpdateRequestRepository requestRepository;
    private final ProfileImageService imageService;
    private final InquiryService inquiryService;
    private final GroupService groupService;

    public User getUserById(Long id) {
        if (id == null)
            throw new BusinessException(UserErrorCode.USER_ID_REQUIRED);
        return userRepository.findById(id)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public UserInfoDto getMyInfo(Long id) {
        User user = getUserById(id);
        return UserInfoDto.from(user);
    }

    @Transactional
    public void submitUpdateRequest(Long userId, UserUpdateRequestDto dto) {
        User user = getUserById(userId);

        boolean hasAny = dto.getUserName()!=null || dto.getUnivCode()!=null
            || dto.getPhoneNumber()!=null || dto.getBirthDate()!=null;
        if (!hasAny)
            throw new BusinessException(UserErrorCode.UPDATE_FIELDS_REQUIRED);

        requestRepository.findByUser_IdAndStatus(userId, GlobalEnum.RequestStatus.PENDING)
            .ifPresent(r -> { throw new BusinessException(UserErrorCode.UPDATE_REQUEST_ALREADY_PENDING); });

        UserUpdateRequest req = UserUpdateRequest.builder()
            .user(user)
            .requestedUserName(dto.getUserName())
            .requestedUnivCode(dto.getUnivCode())
            .requestedPhoneNumber(dto.getPhoneNumber())
            .requestedBirthDate(dto.getBirthDate())
            .status(GlobalEnum.RequestStatus.PENDING)
            .build();

        requestRepository.save(req);
    }

    @Transactional
    public void createInquiry(Long userId, InquiryRequestDto dto) {
        User user = getUserById(userId);
        inquiryService.createInquiry(user, dto);
    }

    @Transactional(readOnly = true)
    public List<InquiryResponseDto> getMyInquiries(Long userId) {
        User user = getUserById(userId);
        return inquiryService.getMyInquiries(user.getId());
    }

    @Transactional
    public String updateMyProfileImage(Long userId, MultipartFile file) throws Exception {
        User user = getUserById(userId);

        String newUrl = imageService.uploadImage(ProfileImageType.USER,userId, file, user.getProfileImageUrl());
        user.setProfileImageUrl(newUrl);
        return newUrl;
    }

    @Transactional
    public void deleteMyProfileImage(Long userId) {
        User user = getUserById(userId);

        String url = user.getProfileImageUrl();
        if (url != null && !url.isBlank()) {
            imageService.deleteByUrl(url);
            user.setProfileImageUrl(null);
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void deleteAccount(Long userId) {
        groupService.handoverOrCloseOwnedGroups(userId);

        UserService proxy = (UserService) AopContext.currentProxy();
        proxy.userHardDeleteTx(userId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void userHardDeleteTx(Long userId) {
        var user = userRepository.findByIdForUpdate(userId)
            .orElseThrow(() -> new EntityNotFoundException("이미 탈퇴했거나 존재하지 않는 사용자입니다."));
        userRepository.delete(user);
    }
}
