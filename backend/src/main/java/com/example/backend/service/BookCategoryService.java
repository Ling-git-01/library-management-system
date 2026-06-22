package com.example.backend.service;

import com.example.backend.entity.Book;
import com.example.backend.entity.BookCategory;
import com.example.backend.repository.BookCategoryRepository;
import com.example.backend.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private BookRepository bookRepo; // 需要注入BookRepository

    // 新增分类
    @Transactional
    public BookCategory addCategory(BookCategory category) {
        return categoryRepo.save(category);
    }

    // 修改分类
    @Transactional
    public BookCategory updateCategory(Integer id, BookCategory category) {
        BookCategory existCate = categoryRepo.findById(id).orElseThrow(() -> new RuntimeException("分类不存在"));
        existCate.setName(category.getName());
        existCate.setDescription(category.getDescription());
        return categoryRepo.save(existCate);
    }

    // 删除分类
    @Transactional
    public void deleteCategory(Integer id) {
        BookCategory existCate = categoryRepo.findById(id).orElseThrow(() -> new RuntimeException("分类不存在"));
        // 检查是否有图书关联该分类
        List<Book> books = bookRepo.findByCategoryId(id);
        if (!books.isEmpty()) {
            throw new RuntimeException("该分类下仍有关联图书，无法删除");
        }
        categoryRepo.delete(existCate);
    }
}
