package com.backend.Scheduler;

import com.backend.Notification.Repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class CleanupScheduler {

	private final NotificationRepository notificationRepository;

	// 매일 03:00 KST
	@Transactional
	@Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
	public void purgeReadNotifications() {
		int deleted = notificationRepository.deleteByIsReadTrue();
		log.info("Deleted {} read notifications", deleted);
	}
}
