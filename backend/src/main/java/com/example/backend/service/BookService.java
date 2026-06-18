package com.example.backend.service;

import com.example.backend.entity.Book;
import com.example.backend.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepo;

    // 关键词搜索图书
    public List<Book> search(String keyword) {
        return bookRepo.searchBooks(keyword);
    }

    // 根据id获取图书详情
    public Book getById(Integer id) {
        Optional<Book> opt = bookRepo.findById(id);
        if(opt.isEmpty()) throw new RuntimeException("图书不存在");
        return opt.get();
    }

    // 根据分类id查图书
    public List<Book> getByCategory(Integer cid) {
        return bookRepo.findByCategoryId(cid);
    }

    // 查询可借阅图书
    public List<Book> getAvailable() {
        return bookRepo.findByAvailableCopiesGreaterThan(0);
    }
}
