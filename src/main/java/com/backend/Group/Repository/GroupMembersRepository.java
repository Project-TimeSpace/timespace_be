package com.backend.Group.Repository;

import com.backend.Group.Dto.GroupSummaryDto;
import com.backend.Group.Entity.GroupMembers;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupMembersRepository extends JpaRepository<GroupMembers, Long> {

    @Query("""
            SELECT new com.backend.Group.Dto.GroupSummaryDto(
            g.id,
            g.groupName,
            g.groupType,
            COUNT(m.id),
            g.maxMember
        )
        FROM GroupMembers gm
        JOIN gm.group g
        LEFT JOIN GroupMembers m ON m.group.id = g.id
        WHERE gm.user.id = :userId
        GROUP BY g.id, g.groupName, g.groupType, g.maxMember
    """)
    List<GroupSummaryDto> findGroupSummariesByUserId(@Param("userId") Long userId);


    int countByGroupId(Long groupId);

    boolean existsByGroupIdAndUserId(Long groupId, Long userId);

    List<GroupMembers> findByGroupId(Long groupId);

    Optional<GroupMembers> findByGroupIdAndUserId(Long groupId, Long userId);
}
