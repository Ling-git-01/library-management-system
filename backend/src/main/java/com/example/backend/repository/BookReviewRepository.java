package com.example.backend.repository;

import com.example.backend.entity.BookReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookReviewRepository extends JpaRepository<BookReview,Integer> {
    List<BookReview> findByBookId(Integer bookId);
    List<BookReview> findByUserId(Integer userId);
}
