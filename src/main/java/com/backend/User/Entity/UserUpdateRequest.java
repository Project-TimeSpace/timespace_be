package com.backend.User.Entity;

import com.backend.ConfigEnum.Converter.RequestStatusConverter;
import com.backend.ConfigEnum.GlobalEnum;
import com.backend.User.Entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_update_requests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserUpdateRequest {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "requested_user_name", length = 50)
	private String requestedUserName;

	@Column(name = "requested_univ_code")
	private Integer requestedUnivCode;

	@Column(name = "requested_phone_number", length = 20)
	private String requestedPhoneNumber;

	@Column(name = "requested_birth_date")
	private LocalDate requestedBirthDate;

	@Convert(converter = RequestStatusConverter.class)
	@Column(name = "status", nullable = false)
	private GlobalEnum.RequestStatus status;   // DB에는 int code로 저장됨

	@Column(name = "admin_id")
	private Long adminId;

	@Column(name = "review_reason", length = 255)
	private String reviewReason;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "reviewed_at")
	private LocalDateTime reviewedAt;

	@PrePersist
	void prePersist() {
		if (status == null) status = GlobalEnum.RequestStatus.PENDING; // code=1
		if (createdAt == null) createdAt = LocalDateTime.now();
	}
}
