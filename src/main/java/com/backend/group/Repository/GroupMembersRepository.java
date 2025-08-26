package com.backend.group.Repository;

import com.backend.group.Entity.Group;
import com.backend.group.Entity.GroupMembers;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupMembersRepository extends JpaRepository<GroupMembers, Long> {

    @Query(value = """
        SELECT gm.user_id
        FROM GroupMembers gm
        WHERE gm.group_id = :groupId AND gm.user_id <> :excludingUser
        LIMIT 1
        """, nativeQuery = true)
    Long findAnyOtherMemberId(@Param("groupId") Long groupId, @Param("excludingUser") Long excludingUser);

    int countByGroupId(Long groupId);

    boolean existsByGroupIdAndUserId(Long groupId, Long userId);

    @EntityGraph(attributePaths = "user")
    List<GroupMembers> findByGroupId(Long groupId);

    Optional<GroupMembers> findByGroupIdAndUserId(Long groupId, Long userId);


    @Query("SELECT gm.group.id FROM GroupMembers gm WHERE gm.user.id = :userId")
    List<Long> findGroupIdsByUserId(@Param("userId") Long userId);

	boolean existsByGroup_IdAndUser_Id(Long groupId, Long userId);

    @Query("""
    select distinct gm.group
    from GroupMembers gm
    join gm.group g
    where gm.user.id = :userId
    """)
    List<Group> findGroupsByUserId(@org.springframework.data.repository.query.Param("userId") Long userId);

    // 2) 여러 그룹의 멤버 수를 한 번에 집계 (1쿼리)
    public interface GroupMemberCount {
        Long getGroupId();
        Long getMemberCount();
    }

    @Query("""
    select gm.group.id as groupId, count(gm.id) as memberCount
    from GroupMembers gm
    where gm.group.id in :groupIds
    group by gm.group.id
    """)
    List<GroupMemberCount> countMembersByGroupIds(@org.springframework.data.repository.query.Param("groupIds") List<Long> groupIds);

    @Query("""
    select gm
    from GroupMembers gm
    join fetch gm.user u
    where gm.group.id = :groupId
    order by gm.id
    """)
    List<GroupMembers> findByGroupIdFetchUser(@Param("groupId") Long groupId);
}
