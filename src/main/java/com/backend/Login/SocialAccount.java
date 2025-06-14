package com.backend.Login;

import jakarta.persistence.*;
import lombok.*;
import com.backend.User.Entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "SocialAccount")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 30)
    private String provider;

    @Column(name = "provider_user_id", nullable = false, length = 100)
    private String providerUserId;

    @Column(name = "connected_at", nullable = false)
    private LocalDateTime connectedAt;
}

