package com.example.backend.controller;

import com.example.backend.entity.Book;
import com.example.backend.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/books")
public class BookController {
    @Autowired
    private BookService bookService;

    // 图书模糊搜索
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String keyword) {
        List<Book> list = bookService.search(keyword);
        return ResponseEntity.ok(Map.of("success",true,"count",list.size(),"data",list));
    }

    // 图书详情
    @GetMapping("/{id}")
    public ResponseEntity<?> getDetail(@PathVariable Integer id) {
        try {
            Book book = bookService.getById(id);
            return ResponseEntity.ok(Map.of("success",true,"data",book));
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of("success",false,"msg",e.getMessage()));
        }
    }

    // 根据分类查图书
    @GetMapping("/category/{cid}")
    public ResponseEntity<?> getByCategory(@PathVariable Integer cid) {
        List<Book> list = bookService.getByCategory(cid);
        return ResponseEntity.ok(Map.of("success",true,"count",list.size(),"data",list));
    }

    // 查询可借阅图书
    @GetMapping("/available")
    public ResponseEntity<?> getAvailable() {
        List<Book> list = bookService.getAvailable();
        return ResponseEntity.ok(Map.of("success",true,"count",list.size(),"data",list));
    }
}
