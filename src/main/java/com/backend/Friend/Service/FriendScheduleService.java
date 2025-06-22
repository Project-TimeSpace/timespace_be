package com.backend.Friend.Service;


import com.backend.Config.GlobalEnum.RequestStatus;
import com.backend.Friend.Dto.FriendScheduleRequestDto;
import com.backend.Friend.Entity.FriendScheduleRequest;
import com.backend.Friend.Repository.FriendScheduleRequestRepository;
import com.backend.User.Entity.User;
import com.backend.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FriendScheduleService {

    private final UserRepository userRepository;
    private final FriendScheduleRequestRepository scheduleRequestRepository;

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
