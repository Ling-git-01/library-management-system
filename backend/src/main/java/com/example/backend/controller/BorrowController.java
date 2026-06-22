package com.example.backend.controller;

import com.example.backend.entity.BorrowRecord;
import com.example.backend.service.BorrowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/borrow")
public class BorrowController {
    @Autowired
    private BorrowRecordService borrowService;

    // 借书 POST /borrow/add
    @PostMapping("/add")
    public ResponseEntity<?> borrowBook(@RequestBody Map<String,Integer> params){
        try{
            Integer userId = params.get("userId");
            Integer bookId = params.get("bookId");
            Integer days = params.getOrDefault("borrowDays",30);
            BorrowRecord record = borrowService.borrowBook(userId, bookId, days);
            return ResponseEntity.ok(Map.of("success",true,"msg","借书成功","data",record));
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of("success",false,"msg",e.getMessage()));
        }
    }

    // 还书 PUT /borrow/return/{borrowId}
    @PutMapping("/return/{borrowId}")
    public ResponseEntity<?> returnBook(@PathVariable Integer borrowId){
        try{
            BorrowRecord record = borrowService.returnBook(borrowId);
            return ResponseEntity.ok(Map.of("success",true,"msg","还书成功","data",record));
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of("success",false,"msg",e.getMessage()));
        }
    }

    // 查询用户全部借阅 GET /borrow/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserBorrow(@PathVariable Integer userId){
        List<BorrowRecord> list = borrowService.getUserBorrow(userId);
        return ResponseEntity.ok(Map.of("success",true,"count",list.size(),"data",list));
    }
    // 借阅排行榜 GET /api/borrow/rank
    @GetMapping("/rank")
    public ResponseEntity<?> borrowRank() {
        List<Map<String,Object>> rankList = borrowService.getBorrowRank();
        return ResponseEntity.ok(Map.of("success",true,"data",rankList));
    }
}
