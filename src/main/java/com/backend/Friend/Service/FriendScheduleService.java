package com.backend.Friend.Service;


import com.backend.Config.GlobalEnum.RequestStatus;
import com.backend.Config.GlobalEnum.Visibility;
import com.backend.Friend.Dto.FriendScheduleRequestDto;
import com.backend.Friend.Dto.SimpleScheduleDto;
import com.backend.Friend.Entity.Friend;
import com.backend.Friend.Entity.FriendScheduleRequest;
import com.backend.Friend.Repository.FriendRepository;
import com.backend.Friend.Repository.FriendScheduleRequestRepository;
import com.backend.User.Dto.UserScheduleDto;
import com.backend.User.Entity.User;
import com.backend.User.Repository.UserRepository;
import com.backend.User.Service.UserScheduleService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FriendScheduleService {

    private final UserRepository userRepository;
    private final FriendScheduleRequestRepository scheduleRequestRepository;
    private final UserScheduleService userScheduleService;
    private final FriendRepository friendRepository;

    public Friend getFriendRelationOrThrow(Long userId, Long friendId) {
        return friendRepository.findByUserIdAndFriendId(userId, friendId)
                .orElseThrow(() -> new IllegalArgumentException("친구 관계가 아닙니다."));
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

    /** 5. 약속 신청하기 */
    @Transactional
    public void sendScheduleRequest(Long userId, Long friendId, FriendScheduleRequestDto dto) {
        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        User receiver = userRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("요청 대상 친구를 찾을 수 없습니다."));

        FriendScheduleRequest req = FriendScheduleRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .title(dto.getTitle())
                .date(dto.getDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .status(RequestStatus.PENDING.name())
                .build();
        scheduleRequestRepository.save(req);
    }
}
