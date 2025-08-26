package com.backend.friend.Service;

import com.backend.configenum.GlobalEnum.SortOption;
import com.backend.configenum.GlobalEnum.Visibility;
import com.backend.friend.Dto.FriendDto;
import com.backend.friend.Dto.FriendNicknameUpdate;
import com.backend.friend.Entity.Friend;
import com.backend.friend.Repository.FriendRepository;
import com.backend.friend.error.FriendErrorCode;
import com.backend.response.BusinessException;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;

    private Friend getRelationOrThrow(Long userId, Long friendId) {
        return friendRepository.findByUser_IdAndFriend_Id(userId, friendId)
            .orElseThrow(() -> new BusinessException(FriendErrorCode.RELATION_NOT_FOUND));
    }

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

        return friends.stream().map(FriendDto::from).toList();
    }

    @Transactional
    public void updateFavorite(Long userId, Long friendId) {
        Friend relation = getRelationOrThrow(userId, friendId);

        if(relation.getIsFavorite() == true){
            relation.setIsFavorite(false);
        }else{
            relation.setIsFavorite(true);
        }

        friendRepository.save(relation);
    }

    @Transactional
    public void updateVisibility(Long userId, Long friendId, Visibility target) {
        Friend relation = getRelationOrThrow(userId, friendId);

        if (relation.getVisibility() == target) {
            throw new BusinessException(FriendErrorCode.VISIBILITY_ALREADY_SET);
        }

        relation.setVisibility(target);
    }

    @Transactional
    public void deleteFriend(Long userId, Long friendId) {
        Friend relation = getRelationOrThrow(userId, friendId);
        Friend urel = friendRepository.findByUser_IdAndFriend_Id(friendId, userId)
                .orElseThrow(() -> new BusinessException(FriendErrorCode.REVERSE_RELATION_NOT_FOUND));

        friendRepository.delete(relation);
        friendRepository.delete(urel);
    }

    @Transactional
    public FriendNicknameUpdate updateNickname(Long userId, Long friendId, String newNickname) {
        Friend relation = getRelationOrThrow(userId, friendId);

        if (!newNickname.equals(relation.getNickname())) {
            relation.setNickname(newNickname);
        }
        return new FriendNicknameUpdate(relation.getNickname());
    }
}
