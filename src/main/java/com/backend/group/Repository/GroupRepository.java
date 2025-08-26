package com.backend.group.Repository;

import com.backend.group.Entity.Group;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupRepository extends JpaRepository<Group, Long> {

    boolean existsByUniqueCode(String code);

    @Query("""
      SELECT g.category, COUNT(g)
      FROM Group g
      GROUP BY g.category
    """)
    List<Object[]> countGroupByCategory();


    @Query(value = "SELECT id FROM `Group` WHERE master_id = :userId", nativeQuery = true)
    List<Long> findIdsByMasterId(@Param("userId") Long userId);

    @Modifying
    @Query(value = "UPDATE `Group` SET master_id = :newMasterId WHERE id = :groupId", nativeQuery = true)
    int updateMasterId(@Param("groupId") Long groupId, @Param("newMasterId") Long newMasterId);


}
