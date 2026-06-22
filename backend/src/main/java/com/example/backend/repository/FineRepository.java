package com.example.backend.repository;

import com.example.backend.entity.Fine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FineRepository extends JpaRepository<Fine, Integer> {
    List<Fine> findByUserId(Integer userId);
    List<Fine> findByUserIdAndStatus(Integer userId, Fine.Status status);
    List<Fine> findByBorrowId(Integer borrowId);
}
