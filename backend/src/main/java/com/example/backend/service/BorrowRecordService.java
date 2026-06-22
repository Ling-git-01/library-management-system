package com.example.backend.service;

import com.example.backend.entity.Book;
import com.example.backend.entity.BorrowRecord;
import com.example.backend.entity.Fine;
import com.example.backend.repository.BookRepository;
import com.example.backend.repository.BorrowRecordRepository;
import com.example.backend.repository.FineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
public class BorrowRecordService {
    @Autowired
    private BorrowRecordRepository borrowRepo;
    @Autowired
    private BookRepository bookRepo;
    @Autowired
    private FineRepository fineRepo;
    @Autowired
    private UserService userService;

    // 借书操作（事务：扣减库存、新增借阅记录）
    @Transactional
    public BorrowRecord borrowBook(Integer userId, Integer bookId, int borrowDays) {
        // 1. 查询图书
        Book book = bookRepo.findById(bookId).orElseThrow(() -> new RuntimeException("图书不存在"));
        if(book.getAvailableCopies() <= 0) throw new RuntimeException("图书无可借库存");
        // 2. 判断用户是否已借该书未还
        boolean isBorrowed = borrowRepo.existsByUserIdAndBookIdAndStatus(userId, bookId, BorrowRecord.Status.borrowing);
        if(isBorrowed) throw new RuntimeException("你已借阅此书尚未归还");
        // 3. 扣减可借库存
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepo.save(book);
        // 4. 新增借阅记录
        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBorrowDate(LocalDateTime.now());
        record.setDueDate(LocalDateTime.now().plusDays(borrowDays));
        record.setStatus(BorrowRecord.Status.borrowing);
        record.setCreatedAt(LocalDateTime.now());
        return borrowRepo.save(record);
    }

    // 还书操作：判断逾期、生成罚金
    @Transactional
    public BorrowRecord returnBook(Integer borrowId) {
        BorrowRecord record = borrowRepo.findById(borrowId).orElseThrow(() -> new RuntimeException("借阅记录不存在"));
        if(record.getStatus() == BorrowRecord.Status.returned) throw new RuntimeException("该书已归还");
        // 1. 更新归还时间、状态
        record.setReturnDate(LocalDateTime.now());
        record.setStatus(BorrowRecord.Status.returned);
        borrowRepo.save(record);
        // 2. 归还图书库存+1
        Book book = bookRepo.findById(record.getBookId()).get();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepo.save(book);
        // 3. 判断是否逾期，逾期生成罚金（每日0.5元）
        LocalDateTime now = LocalDateTime.now();
        if(now.isAfter(record.getDueDate())){
            long overDay = ChronoUnit.DAYS.between(record.getDueDate(), now);
            BigDecimal amount = new BigDecimal("0.5").multiply(new BigDecimal(overDay));
            Fine fine = new Fine();
            fine.setUserId(record.getUserId());
            fine.setBorrowId(record.getId());
            fine.setAmount(amount);
            fine.setReason("图书逾期"+overDay+"天");
            fine.setStatus(Fine.Status.unpaid);
            fine.setCreatedAt(LocalDateTime.now());
            fineRepo.save(fine);
        }
        return record;
    }

    // 定时任务：批量更新逾期状态
    @Transactional
    public void autoUpdateOverdue() {
        List<BorrowRecord> list = borrowRepo.findOverdueRecords();
        for(BorrowRecord br : list){
            br.setStatus(BorrowRecord.Status.overdue);
            borrowRepo.save(br);
        }
    }

    public List<BorrowRecord> getUserBorrow(Integer userId) {
        return borrowRepo.findByUserId(userId);
    }

    // 获取图书借阅排行榜
    public List<Map<String,Object>> getBorrowRank() {
        return borrowRepo.getBookBorrowRank();
    }

    public List<BorrowRecord> listAllBorrow() {
        return borrowRepo.findAll();
    }
}
