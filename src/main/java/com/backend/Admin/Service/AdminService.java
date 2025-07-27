package com.backend.Admin.Service;

import com.backend.ConfigEnum.GlobalEnum.GroupCategory;
import com.backend.Group.Repository.GroupRepository;
import com.backend.User.Repository.UserRepository;
import com.backend.Login.Auth.VisitLogRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final VisitLogRepository visitLogRepository;
    private final GroupRepository groupRepository;

    @Transactional(readOnly = true)
    public long getTotalUserCount() {
        return userRepository.count();
    }

    @Transactional(readOnly = true)
    public long getVisitUserCount(LocalDate start, LocalDate end) {
        return visitLogRepository
                .countDistinctUserIdByVisitDateBetween(start, end);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getGroupCountByCategory() {
        List<Object[]> results = groupRepository.countGroupByCategory();
        Map<String, Long> counts = new LinkedHashMap<>();
        long total = 0L;

        for (Object[] row : results) {
            Integer categoryCode = (Integer) row[0];
            Long    cnt          = (Long)    row[1];
            total += cnt;
            String categoryName = GroupCategory.fromCode(categoryCode).getDisplayName();
            counts.put(categoryName, cnt);
        }
        counts.put("전체", total);
        return counts;
    }
}
