package com.example.backend.service;

import com.example.backend.entity.Book;
import com.example.backend.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // 个性化图书推荐
    public List<Book> getRecommendBook(Integer userId) {
        return bookRepo.getRecommendBookByUserBorrow(userId);
    }

    // 新增图书
    @Transactional
    public Book addBook(Book book) {
        if (book.getTotalCopies() == null) book.setTotalCopies(1);
        if (book.getAvailableCopies() == null) book.setAvailableCopies(book.getTotalCopies());
        if (book.getStatus() == null) book.setStatus(1);
        return bookRepo.save(book);
    }

    // 修改图书
    @Transactional
    public Book updateBook(Integer id, Book book) {
        Book existBook = bookRepo.findById(id).orElseThrow(() -> new RuntimeException("图书不存在"));
        existBook.setTitle(book.getTitle());
        existBook.setAuthor(book.getAuthor());
        existBook.setIsbn(book.getIsbn());
        existBook.setPublisher(book.getPublisher());
        existBook.setPublishYear(book.getPublishYear());
        existBook.setCategoryId(book.getCategoryId());
        existBook.setLocation(book.getLocation());
        existBook.setCoverUrl(book.getCoverUrl());
        existBook.setDescription(book.getDescription());
        existBook.setStatus(book.getStatus());
        existBook.setTotalCopies(book.getTotalCopies());
        existBook.setAvailableCopies(book.getAvailableCopies());
        return bookRepo.save(existBook);
    }

    // 删除图书
    @Transactional
    public void deleteBook(Integer id) {
        if (!bookRepo.existsById(id)) throw new RuntimeException("图书不存在");
        bookRepo.deleteById(id);
        // 若需逻辑删除：bookRepo.findById(id).ifPresent(b -> {b.setStatus(0); bookRepo.save(b);});
    }

    // 更新图书库存
    @Transactional
    public void updateBookStock(Integer bookId, Integer stock) {
        Book book = bookRepo.findById(bookId).orElseThrow(() -> new RuntimeException("图书不存在"));
        book.setTotalCopies(stock);
        // 保证可借库存不超过总库存
        if (book.getAvailableCopies() > stock) {
            book.setAvailableCopies(stock);
        }
        bookRepo.save(book);
    }
}
