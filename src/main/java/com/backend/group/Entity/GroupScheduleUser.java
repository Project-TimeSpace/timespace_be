package com.backend.group.Entity;

import com.backend.configenum.Converter.RequestStatusConverter;
import com.backend.configenum.GlobalEnum;
import com.backend.user.Entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GroupScheduleUser {

	public enum Status { PENDING, ACCEPTED, DECLINED }

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY) // 기존 GroupSchedule 엔티티
	@JoinColumn(name = "group_schedule_id", nullable = false)
	private GroupSchedule groupSchedule;

	@ManyToOne(fetch = FetchType.LAZY) // 기존 user 엔티티
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Convert(converter = RequestStatusConverter.class)
	@Column(name = "status", nullable = false)
	private GlobalEnum.RequestStatus status;

	@Column(name = "accepted_at")
	private LocalDateTime acceptedAt;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@PrePersist
	void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		if (createdAt == null) createdAt = now;
		if (updatedAt == null) updatedAt = now;
		if (status == null) status = GlobalEnum.RequestStatus.PENDING;
	}

	@PreUpdate
	void preUpdate() { this.updatedAt = LocalDateTime.now(); }
}
