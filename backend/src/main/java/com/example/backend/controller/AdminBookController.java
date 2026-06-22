package com.example.backend.controller;

import com.example.backend.entity.Book;
import com.example.backend.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/books")
public class AdminBookController {
    @Autowired
    private BookService bookService;

    // 新增图书
    @PostMapping("/add")
    public ResponseEntity<?> addBook(@RequestBody Book book) {
        try {
            Book saved = bookService.addBook(book);
            return ResponseEntity.ok(Map.of("success", true, "msg", "新增图书成功", "data", saved));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "msg", e.getMessage()));
        }
    }

    // 修改图书
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Integer id, @RequestBody Book book) {
        try {
            Book updated = bookService.updateBook(id, book);
            return ResponseEntity.ok(Map.of("success", true, "msg", "修改图书成功", "data", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "msg", e.getMessage()));
        }
    }

    // 删除图书（逻辑删除/物理删除）
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Integer id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.ok(Map.of("success", true, "msg", "删除图书成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "msg", e.getMessage()));
        }
    }

    // 批量更新图书库存
    @PutMapping("/updateStock")
    public ResponseEntity<?> updateStock(@RequestBody Map<String, Integer> params) {
        try {
            Integer bookId = params.get("bookId");
            Integer stock = params.get("stock");
            bookService.updateBookStock(bookId, stock);
            return ResponseEntity.ok(Map.of("success", true, "msg", "库存更新成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "msg", e.getMessage()));
        }
    }
}
