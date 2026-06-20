package com.example.backend.repository;

import com.example.backend.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Integer> {
    List<Reservation> findByUserId(Integer userId);
    List<Reservation> findByBookIdAndStatus(Integer bookId, Reservation.Status status);
    Optional<Reservation> findByUserIdAndBookIdAndStatus(Integer userId, Integer bookId, Reservation.Status status);
    boolean existsByUserIdAndBookIdAndStatus(Integer userId, Integer bookId, Reservation.Status status);
}
