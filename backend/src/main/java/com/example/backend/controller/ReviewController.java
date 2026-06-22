package com.example.backend.controller;

import com.example.backend.entity.BookReview;
import com.example.backend.service.BookReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/review")
public class ReviewController {
    @Autowired
    private BookReviewService reviewService;

    // 新增书评
    @PostMapping("/add")
    public ResponseEntity<?> addReview(@RequestBody Map<String,Object> params){
        try{
            Integer uid = Integer.parseInt(params.get("userId").toString());
            Integer bid = Integer.parseInt(params.get("bookId").toString());
            Integer star = Integer.parseInt(params.get("rating").toString());
            String content = params.get("content").toString();
            BookReview review = reviewService.addReview(uid, bid, star, content);
            return ResponseEntity.ok(Map.of("success",true,"msg","发布书评成功","data",review));
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of("success",false,"msg",e.getMessage()));
        }
    }

    // 查询图书书评
    @GetMapping("/book/{bookId}")
    public ResponseEntity<?> bookReview(@PathVariable Integer bookId){
        List<BookReview> list = reviewService.getBookReview(bookId);
        return ResponseEntity.ok(Map.of("success",true,"count",list.size(),"data",list));
    }

    // 删除书评
    @DeleteMapping("/del/{reviewId}")
    public ResponseEntity<?> delReview(@PathVariable Integer reviewId, @RequestParam Integer userId){
        try{
            reviewService.deleteReview(reviewId, userId);
            return ResponseEntity.ok(Map.of("success",true,"msg","删除书评成功"));
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of("success",false,"msg",e.getMessage()));
        }
    }
}
