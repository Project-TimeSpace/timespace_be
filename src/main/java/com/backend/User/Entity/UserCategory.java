package com.backend.User.Entity;

import com.backend.ConfigEnum.GlobalEnum.ScheduleColor;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "UserCategory",
        uniqueConstraints = @UniqueConstraint(name = "uq_user_category", columnNames = {"user_id", "category_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "category_number", nullable = false)
    private Integer categoryNumber;

    @Column(name = "category_name", nullable = false, length = 50)
    private String categoryName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleColor color;
}
