package com.backend.Group.Service;

import com.backend.Group.Dto.GroupScheduleCreateRequest;
import com.backend.Group.Dto.GroupScheduleDto;
import com.backend.Group.Repository.GroupScheduleRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupScheduleService {

    private final GroupScheduleRepository groupScheduleRepository;


    public GroupScheduleDto createSchedule(Long groupId, GroupScheduleCreateRequest request) {
        // TODO: 일정 INSERT
        return null;
    }
}
