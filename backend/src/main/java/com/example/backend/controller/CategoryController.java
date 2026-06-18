package com.example.backend.controller;

import com.example.backend.entity.BookCategory;
import com.example.backend.service.BookCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private BookCategoryService categoryService;

    // 查询所有分类
    @GetMapping("/list")
    public ResponseEntity<?> listAll() {
        List<BookCategory> list = categoryService.getAll();
        return ResponseEntity.ok(Map.of("success",true,"count",list.size(),"data",list));
    }

    // 根据id查询单个分类
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Integer id) {
        try {
            BookCategory category = categoryService.getById(id);
            return ResponseEntity.ok(Map.of("success",true,"data",category));
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of("success",false,"msg",e.getMessage()));
        }
    }
}
