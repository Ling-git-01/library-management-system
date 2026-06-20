package com.example.backend.service;

import com.example.backend.entity.BookReview;
import com.example.backend.repository.BookRepository;
import com.example.backend.repository.BookReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookReviewService {
    @Autowired
    private BookReviewRepository reviewRepo;
    @Autowired
    private BookRepository bookRepo;

    // 新增书评
    @Transactional
    public BookReview addReview(Integer userId, Integer bookId, Integer rating, String content) {
        bookRepo.findById(bookId).orElseThrow(() -> new RuntimeException("图书不存在"));
        if(rating < 1 || rating > 5) throw new RuntimeException("评分必须1-5分");
        BookReview review = new BookReview();
        review.setUserId(userId);
        review.setBookId(bookId);
        review.setRating(rating);
        review.setContent(content);
        review.setCreatedAt(LocalDateTime.now());
        return reviewRepo.save(review);
    }

    // 查询图书全部书评
    public List<BookReview> getBookReview(Integer bookId) {
        return reviewRepo.findByBookId(bookId);
    }

    // 查询用户自己书评
    public List<BookReview> getUserReview(Integer userId) {
        return reviewRepo.findByUserId(userId);
    }

    // 删除书评
    @Transactional
    public void deleteReview(Integer reviewId, Integer userId) {
        BookReview review = reviewRepo.findById(reviewId).orElseThrow(() -> new RuntimeException("书评不存在"));
        if(!review.getUserId().equals(userId)) throw new RuntimeException("仅作者可删除书评");
        reviewRepo.delete(review);
    }
}
