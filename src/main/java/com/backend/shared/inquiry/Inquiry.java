package com.backend.shared.inquiry;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.backend.Admin.Entity.Admin;
import com.backend.user.Entity.User;

@Entity
@Table(name = "Inquiry",
	uniqueConstraints = @UniqueConstraint(
		name = "uq_inquiry_pending",
		columnNames = {"user_id","status"}
	)
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Inquiry {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 문의자
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// 문의 제목
	@Column(nullable = false, length = 255)
	private String title;

	// 문의 내용
	@Column(columnDefinition = "TEXT", nullable = false)
	private String content;

	// 0: PENDING, 1: RESOLVED
	@Column(nullable = false)
	private Integer status;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	// ★ 처리한 Admin (nullable)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "responder_id")
	private Admin responder;

	// ★ 처리 시각
	@Column(name = "answered_at")
	private LocalDateTime answeredAt;

	@Column(name = "reply_content", columnDefinition = "TEXT", nullable = false)
	private String replyContent;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}
