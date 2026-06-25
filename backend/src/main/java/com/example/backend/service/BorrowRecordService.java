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
import java.time.format.DateTimeFormatter;
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
    @Autowired
    private EmailService emailService;
    @Autowired
    private NotificationService notificationService;

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

    // 还书操作：判断逾期、生成罚金、发送邮件
    @Transactional
    public BorrowRecord returnBook(Integer borrowId) {
        BorrowRecord record = borrowRepo.findById(borrowId).orElseThrow(() -> new RuntimeException("借阅记录不存在"));
        if(record.getStatus() == BorrowRecord.Status.returned) throw new RuntimeException("该书已归还");
        LocalDateTime now = LocalDateTime.now();
        // 1. 判断是否逾期
        boolean isOverdue = now.isAfter(record.getDueDate());
        long overDay = 0;
        BigDecimal amount = BigDecimal.ZERO;
        if (isOverdue) {
            overDay = ChronoUnit.DAYS.between(record.getDueDate(), now);
            amount = new BigDecimal("0.5").multiply(new BigDecimal(overDay));
        }
        // 2. 更新归还时间、状态
        record.setReturnDate(now);
        record.setStatus(BorrowRecord.Status.returned);
        borrowRepo.save(record);
        // 3. 归还图书库存+1，并通知预约用户
        final Integer returnedBookId = record.getBookId();
        bookRepo.findById(returnedBookId).ifPresent(b -> {
            b.setAvailableCopies(b.getAvailableCopies() + 1);
            bookRepo.save(b);
        });
        // 3.5 发送预约可借阅通知（给预约该书的其他用户）
        try {
            notificationService.sendReserveAvailableNotification(returnedBookId);
        } catch (Exception ignore) {
            // 通知发送失败不影响还书主流程
        }
        // 4. 逾期生成罚金（每日0.5元）
        if (isOverdue) {
            Fine fine = new Fine();
            fine.setUserId(record.getUserId());
            fine.setBorrowId(record.getId());
            fine.setAmount(amount);
            fine.setReason("图书逾期" + overDay + "天");
            fine.setStatus(Fine.Status.unpaid);
            fine.setCreatedAt(now);
            fineRepo.save(fine);
        }
        // 5. 发送归还通知邮件（有罚金时一并告知）
        try {
            com.example.backend.entity.User user = userService.findUserById(record.getUserId());
            if (user != null && user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
                String bookTitle = "";
                try { bookTitle = record.getBook() != null ? record.getBook().getTitle() : ""; } catch (Exception ignore) {}
                if (isOverdue) {
                    emailService.sendReturnNotice(
                            user.getEmail(),
                            user.getUsername(),
                            bookTitle,
                            now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                            overDay,
                            amount.toString()
                    );
                }
            }
        } catch (Exception ignore) {
            // 邮件发送失败不影响还书主流程
        }
        return record;
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
