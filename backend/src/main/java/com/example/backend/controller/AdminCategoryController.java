package com.example.backend.controller;

import com.example.backend.entity.BookCategory;
import com.example.backend.service.BookCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/category")
public class AdminCategoryController {
    @Autowired
    private BookCategoryService categoryService;

    // 新增分类
    @PostMapping("/add")
    public ResponseEntity<?> addCategory(@RequestBody BookCategory category) {
        try {
            BookCategory saved = categoryService.addCategory(category);
            return ResponseEntity.ok(Map.of("success", true, "msg", "新增分类成功", "data", saved));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "msg", e.getMessage()));
        }
    }

    // 修改分类
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Integer id, @RequestBody BookCategory category) {
        try {
            BookCategory updated = categoryService.updateCategory(id, category);
            return ResponseEntity.ok(Map.of("success", true, "msg", "修改分类成功", "data", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "msg", e.getMessage()));
        }
    }

    // 删除分类（需检查是否关联图书）
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(Map.of("success", true, "msg", "删除分类成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "msg", e.getMessage()));
        }
    }
}
