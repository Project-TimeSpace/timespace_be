package com.backend.User.Service;


import com.backend.ConfigEnum.GlobalEnum.ScheduleColor;
import com.backend.User.Dto.UserCategoryDto;
import com.backend.User.Entity.User;
import com.backend.User.Entity.UserCategory;
import com.backend.User.Repository.UserCategoryRepository;
import com.backend.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCategoryService {

    private final UserCategoryRepository userCategoryRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserCategoryDto> getUserCategories(Long userId) {
        return userCategoryRepository.findByUserId(userId).stream()
                .map(category -> UserCategoryDto.builder()
                        .categoryId(category.getCategoryId())  // categoryId 추가
                        .categoryName(category.getCategoryName())
                        .color(category.getColor().name())
                        .build())
                .collect(Collectors.toList());
    }


    @Transactional
    public void addUserCategory(Long userId, UserCategoryDto dto) {
        List<UserCategory> existing = userCategoryRepository.findByUserId(userId);
        if (existing.size() >= 8) {
            throw new IllegalStateException("카테고리는 최대 8개까지 등록할 수 있습니다.");
        }

        for(UserCategory c : existing){
            if (c.getCategoryName().equals(dto.getCategoryName())) {
                throw new IllegalStateException("이미 존재하는 카테고리 이름입니다.");
            }
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 다음 categoryId 계산 (1부터 시작)
        int nextId = existing.isEmpty() ? 1 : existing.get(existing.size() - 1).getCategoryId() + 1;

        UserCategory category = UserCategory.builder()
                .user(user)
                .categoryId(nextId)
                .categoryName(dto.getCategoryName())
                .color(ScheduleColor.valueOf(dto.getColor()))
                .build();

        userCategoryRepository.save(category);
    }

    @Transactional
    public void updateUserCategory(Long userId, UserCategoryDto dto) {
        UserCategory category = userCategoryRepository.findByUserIdAndCategoryId(userId, dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 이름의 카테고리를 찾을 수 없습니다."));

        // 색상 업데이트
        if (dto.getColor() != null) {
            category.setColor(ScheduleColor.valueOf(dto.getColor()));
        }

        // 이름이 null이 아니고 기존 이름과 다르면 수정
        if (dto.getCategoryName() != null && !dto.getCategoryName().isBlank()
                && !dto.getCategoryName().equals(category.getCategoryName())) {
            category.setCategoryName(dto.getCategoryName());
        }
    }

    @Transactional
    public void deleteUserCategory(Long userId, Integer categoryId) {
        List<UserCategory> list = userCategoryRepository.findByUserIdOrderByCategoryId(userId);

        UserCategory target = list.stream()
                .filter(c -> c.getCategoryId().equals(categoryId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 카테고리를 찾을 수 없습니다."));

        userCategoryRepository.delete(target);

        // 삭제된 뒤의 카테고리들은 ID -1
        list.stream()
                .filter(c -> c.getCategoryId() > categoryId)
                .forEach(c -> c.setCategoryId(c.getCategoryId() - 1));
    }
}
