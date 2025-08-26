package com.backend.Notification.Repository;

import com.backend.configenum.GlobalEnum;
import com.backend.Notification.Entity.Notification;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

	List<Notification> findByUser_IdAndIsReadFalseOrderByCreatedAtDesc(Long userId);
    long countByUserIdAndIsReadFalse(Long userId);

	// 조회된 id들만 읽음 처리 (레이스 조건 방지)
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update Notification n set n.isRead = true where n.id in :ids")
	int markAsReadByIds(@Param("ids") List<Integer> ids);

	// 새벽 3시 일괄 삭제
	@Modifying(clearAutomatically = true)
	int deleteByIsReadTrue();

	@EntityGraph(attributePaths = "sender")
	List<Notification> findByUser_IdAndDeletedAtIsNullAndTypeInOrderByCreatedAtDesc(Long userId, List<GlobalEnum.NotificationType> types);
}
