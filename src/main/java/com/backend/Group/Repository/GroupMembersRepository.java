package com.backend.Group.Repository;

import com.backend.ConfigEnum.GlobalEnum;
import com.backend.Group.Dto.GroupSummaryDto;
import com.backend.Group.Entity.GroupMembers;
import com.backend.Group.Entity.GroupRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupMembersRepository extends JpaRepository<GroupMembers, Long> {

    /*
    @Query("""
            SELECT new com.backend.Group.Dto.GroupSummaryDto(
            g.id,
            g.groupName,
            g.groupType,
            g.category,
            COUNT(m.id),
            g.maxMember
        )
        FROM GroupMembers gm
        JOIN gm.group g
        LEFT JOIN GroupMembers m ON m.group.id = g.id
        WHERE gm.user.id = :userId
        GROUP BY g.id, g.groupName, g.groupType, g.category ,g.maxMember
    """)
    List<GroupSummaryDto> findGroupSummariesByUserId(@Param("userId") Long userId);
     */


    int countByGroupId(Long groupId);

    boolean existsByGroupIdAndUserId(Long groupId, Long userId);

    @EntityGraph(attributePaths = "user")
    List<GroupMembers> findByGroupId(Long groupId);

    Optional<GroupMembers> findByGroupIdAndUserId(Long groupId, Long userId);


    @Query("SELECT gm.group.id FROM GroupMembers gm WHERE gm.user.id = :userId")
    List<Long> findGroupIdsByUserId(@Param("userId") Long userId);

    List<GroupMembers> findByUserId(Long userId);

	boolean existsByGroup_IdAndUser_Id(Long groupId, Long userId);


}
