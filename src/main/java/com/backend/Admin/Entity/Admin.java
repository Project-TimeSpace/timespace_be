package com.backend.Admin.Entity;

import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "Admin")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(description = "관리자 계정 정보를 저장하는 엔티티")
public class Admin {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "관리자 고유 ID", example = "1")
    private Long id;

    @Column(nullable = false, length = 50)
    @Schema(description = "관리자 이메일", example = "admin@example.com")
    private String email;

    @Column(nullable = false, length = 100)
    @Schema(description = "관리자 비밀번호 (암호화 저장)", example = "$2a$10$...")
    private String password;

    @Column(name = "admin_name", length = 30)
    @Schema(description = "관리자 이름", example = "관리자홍")
    private String adminName;
}
