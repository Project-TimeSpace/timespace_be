package com.backend.Group.Repository;

import com.backend.Group.Entity.Group;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GroupRepository extends JpaRepository<Group, Long> {

    boolean existsByUniqueCode(String code);

    @Query("""
      SELECT g.category, COUNT(g)
      FROM Group g
      GROUP BY g.category
    """)
    List<Object[]> countGroupByCategory();
}
