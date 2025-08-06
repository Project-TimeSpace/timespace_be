package com.backend.Friend.Service;

import com.backend.ConfigEnum.GlobalEnum.SortOption;
import com.backend.ConfigEnum.GlobalEnum.Visibility;
import com.backend.Friend.Dto.FriendDto;
import com.backend.Friend.Dto.FriendNicknameUpdate;
import com.backend.Friend.Entity.Friend;
import com.backend.Friend.Repository.FriendRepository;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;


    @Transactional(readOnly = true)
    public List<FriendDto> getFriends(Long userId, SortOption sort) {
        Collator collator = Collator.getInstance(Locale.KOREAN);
        List<Friend> friends = friendRepository.findAllByUserId(userId);

        Comparator<Friend> cmp;
        switch (sort) {
            case NAME_DESC:
                cmp = Comparator.comparing(Friend::getNickname, collator).reversed();
                break;
            case CREATED_ASC:
                cmp = Comparator.comparing(Friend::getCreatedAt);
                break;
            case CREATED_DESC:
                cmp = Comparator.comparing(Friend::getCreatedAt).reversed();
                break;
            case NAME_ASC:
            default:
                cmp = Comparator.comparing(Friend::getNickname, collator);
        }
        friends.sort(cmp);

        // 이후 DTO 매핑 그대로…
        return friends.stream()
                .map(f -> FriendDto.builder()
                        .id(f.getId())
                        .friendId(f.getFriend().getId())
                        .name(f.getFriend().getUserName())
                        .email(f.getFriend().getEmail())
                        .isFavorite(f.getIsFavorite())
                        .visibility(f.getVisibility())
                        .nickname(f.getNickname())
                        .createdAt(f.getCreatedAt())
                        .build()
                )
                .collect(Collectors.toList());
    }

    // 2. 친구 즐겨찾기 설정/해제
    @Transactional
    public void updateFavorite(Long userId, Long friendId) {
        Friend relation = friendRepository.findByUserIdAndFriendId(userId, friendId)
                .orElseThrow(() -> new IllegalArgumentException("친구 관계가 아닙니다."));

        if(relation.getIsFavorite() == true){
            relation.setIsFavorite(false);
        }else{
            relation.setIsFavorite(true);
        }

        friendRepository.save(relation);
    }

    // 3. 캘린더 공개범위 변경
    @Transactional
    public void updateVisibility(Long userId, Long friendId) {
        Friend relation = friendRepository.findByUserIdAndFriendId(userId, friendId)
                .orElseThrow(() -> new IllegalArgumentException("친구 관계가 아닙니다."));

        if(relation.getVisibility().equals(Visibility.ALL)){
            relation.setVisibility(Visibility.SIMPLE);
        } else if(relation.getVisibility().equals(Visibility.SIMPLE)){
            relation.setVisibility(Visibility.SECRET);
        } else if(relation.getVisibility().equals(Visibility.SECRET)){
            relation.setVisibility(Visibility.ALL);
        }

        friendRepository.save(relation);
    }

    @Transactional
    public void deleteFriend(Long userId, Long friendId) {
        // 친구 관계 찾기
        Friend rel = friendRepository.findByUserIdAndFriendId(userId, friendId)
                .orElseThrow(() -> new IllegalArgumentException("친구 관계가 아닙니다."));
        Friend urel = friendRepository.findByUserIdAndFriendId(friendId, userId)
                .orElseThrow(() -> new IllegalArgumentException("친구삭제. 이 오류는 발생하면 안됩니다."));
        friendRepository.delete(rel);
        friendRepository.delete(urel);
    }

    @Transactional
    public FriendNicknameUpdate updateNickname(Long userId, Long friendUserId, String newNickname) {
        Friend f = friendRepository
            .findByUser_IdAndFriend_Id(userId, friendUserId)
            .orElseThrow(() -> new AccessDeniedException(
                "친구 관계를 찾을 수 없거나, 권한이 없습니다."
            ));

        if (!newNickname.equals(f.getNickname())) {
            f.setNickname(newNickname);
        }
        // dirty checking 반영 후, 실제 저장된 닉네임을 반환
        return new FriendNicknameUpdate(f.getNickname());
    }
}
