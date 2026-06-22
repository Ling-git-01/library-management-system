package com.example.backend.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
@DynamicInsert
@DynamicUpdate
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(name = "real_name", length = 50)
    private String realName;

    @Column(name = "register_time")
    private LocalDateTime registerTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.reader;

    private Integer status = 1;

    @Column(name = "max_borrow_count")
    private Integer maxBorrowCount = 5;

    public enum Role {
        reader, admin
    }
}
