package com.backend.Notification.Repository;

import com.backend.Notification.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

}
