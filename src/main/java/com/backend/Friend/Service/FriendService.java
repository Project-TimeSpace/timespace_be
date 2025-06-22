package com.backend.Friend.Service;

import com.backend.Config.GlobalEnum;
import com.backend.Config.GlobalEnum.SortOption;
import com.backend.Config.GlobalEnum.Visibility;
import com.backend.Friend.Dto.FriendDto;
import com.backend.Friend.Dto.SimpleScheduleDto;
import com.backend.Friend.Entity.Friend;
import com.backend.User.Dto.UserScheduleDto;
import com.backend.Friend.Repository.FriendRepository;
import com.backend.User.Service.UserScheduleService;
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
    private final UserScheduleService userScheduleService;

    /*
    @Transactional(readOnly = true)
    public List<FriendDto> getFriends(Long userId) {
        List<Friend> friends = friendRepository.findAllByUserId(userId);

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
    }*/

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


    public Friend getFriendRelationOrThrow(Long userId, Long friendId) {
        return friendRepository.findByUserIdAndFriendId(userId, friendId)
                .orElseThrow(() -> new IllegalArgumentException("친구 관계가 아닙니다."));
    }

    // 2. 친구 즐겨찾기 설정/해제
    @Transactional
    public void updateFavorite(Long userId, Long friendId, boolean favorite) {
        Friend relation = friendRepository.findByUserIdAndFriendId(userId, friendId)
                .orElseThrow(() -> new IllegalArgumentException("친구 관계가 아닙니다."));
        relation.setIsFavorite(favorite);
        friendRepository.save(relation);
    }

    // 3. 내 캘린더 공개범위 변경
    @Transactional
    public void updateVisibility(Long userId, Long friendId, Visibility visibility) {
        Friend relation = friendRepository.findByUserIdAndFriendId(userId, friendId)
                .orElseThrow(() -> new IllegalArgumentException("친구 관계가 아닙니다."));
        relation.setVisibility(visibility);
        friendRepository.save(relation);
    }

    // 4. 친구 캘린더 조회
    @Transactional(readOnly = true)
    public Object getFriendCalendar(Long userId, Long friendId, String startDate, String endDate) {
        Friend relation = getFriendRelationOrThrow(userId, friendId);
        Visibility visibility = relation.getVisibility();

        if(visibility.equals(Visibility.SECRET)){
            throw new AccessDeniedException("이 사람의 캘린더는 비공개 설정입니다.");
        }

        List<UserScheduleDto> combined = userScheduleService.getSingleSchedulesByPeriod(friendId, startDate, endDate);
        combined.addAll(userScheduleService.getRepeatSchedulesByPeriod(friendId, startDate, endDate));
        if (visibility.equals(Visibility.SECRET)) {
            return combined.stream()
                    .map(s -> SimpleScheduleDto.builder()
                            .date(s.getDate())
                            .day(s.getDay())
                            .startTime(s.getStartTime())
                            .endTime(s.getEndTime())
                            .build()
                    ).collect(Collectors.toList());
        }
        return combined;
    }



}
