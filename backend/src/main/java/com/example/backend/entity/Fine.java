package com.example.backend.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "fines")
@DynamicInsert
@DynamicUpdate
public class Fine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "borrow_id", nullable = false)
    private Integer borrowId;

    @Column(nullable = false)
    private BigDecimal amount;

    private String reason;

    @Enumerated(EnumType.STRING)
    private Status status = Status.unpaid;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "borrow_id", insertable = false, updatable = false)
    private BorrowRecord borrowRecord;

    public enum Status {
        unpaid, paid
    }
}
