package com.example.backend.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reservations")
@DynamicUpdate
@DynamicInsert
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "book_id", nullable = false)
    private Integer bookId;

    @Column(name = "reserve_date")
    private LocalDateTime reserveDate;

    @Enumerated(EnumType.STRING)
    private Status status = Status.pending;

    @Column(name = "expire_date")
    private LocalDateTime expireDate;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id", insertable = false, updatable = false)
    private Book book;

    public enum Status {
        pending, fulfilled, cancelled
    }
}
