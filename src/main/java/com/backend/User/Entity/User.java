package com.backend.User.Entity;

import com.backend.ConfigEnum.Converter.UniversityConverter;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.CreationTimestamp;
import com.backend.ConfigEnum.GlobalEnum.University;

@Entity
@Table(name = "`User`")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "유저 고유 ID", example = "1")
    private Long id;

    @Column(name = "user_name", nullable = false, length = 50)
    @Schema(description = "사용자 이름", example = "홍길동")
    private String userName;

    @Column(nullable = false, unique = true, length = 50)
    @Schema(description = "이메일 주소", example = "hong@example.com")
    private String email;

    @Column(nullable = false, length = 100)
    @Schema(description = "비밀번호 (암호화 저장)", example = "$2a$10$...")
    private String password;

    @Convert(converter = UniversityConverter.class)
    @Column(nullable = false)
    @Schema(description = "대학교 코드", example = "1")
    private University university;

    @Column(name = "phone_number", length = 20)
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phoneNumber;

    @Column(name = "max_friend", nullable = false)
    @Schema(description = "최대 친구 수", example = "50")
    private Integer maxFriend = 50;

    @Column(name = "max_group", nullable = false)
    @Schema(description = "최대 그룹 수", example = "10")
    private Integer maxGroup = 10;

    @Column(name = "birth_date")
    @Schema(description = "생년월일", example = "2000-01-01")
    private LocalDate birthDate;

    @Column(name = "profile_image_url", length = 255)
    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    @Schema(description = "가입일시", example = "2025-06-01T11:00:00")
    private LocalDateTime createdAt;
}
