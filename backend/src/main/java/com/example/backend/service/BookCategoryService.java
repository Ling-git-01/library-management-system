package com.example.backend.service;

import com.example.backend.entity.BookCategory;
import com.example.backend.repository.BookCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookCategoryService {
    @Autowired
    private BookCategoryRepository categoryRepo;

    // 查询全部分类
    public List<BookCategory> getAll() {
        return categoryRepo.findAll();
    }

    // 根据id查询分类
    public BookCategory getById(Integer id) {
        Optional<BookCategory> opt = categoryRepo.findById(id);
        if(opt.isEmpty()) throw new RuntimeException("分类不存在");
        return opt.get();
    }
}
