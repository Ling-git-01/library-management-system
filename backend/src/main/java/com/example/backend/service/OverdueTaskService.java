package com.example.backend.service;

import com.example.backend.entity.BorrowRecord;
import com.example.backend.entity.User;
import com.example.backend.repository.BorrowRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 定时任务：每日检测逾期未还图书，发送逾期提醒邮件
 * 每天凌晨 1 点执行
 */
@Service
public class OverdueTaskService {

    @Autowired
    private BorrowRecordRepository borrowRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    // 每天凌晨 1 点执行；cron 格式：秒 分 时 日 月 周
    @Scheduled(cron = "0 0 1 * * ?")
    public void autoSendOverdueMail() {
        // findOverdueRecords() 只返回 status=borrowing 且 dueDate<now 的记录
        // 标记 overdue 后不会再被查到，不会重复发邮件
        List<BorrowRecord> overdueList = borrowRepo.findOverdueRecords();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (BorrowRecord record : overdueList) {
            try {
                User user = userService.findUserById(record.getUserId());
                if (user == null) continue;
                if (user.getEmail() == null || user.getEmail().trim().isEmpty()) continue;

                String bookTitle = "";
                try {
                    bookTitle = record.getBook() != null ? record.getBook().getTitle() : "(图书名未知)";
                } catch (Exception ignore) {}

                emailService.sendOverdueNotice(
                        user.getEmail(),
                        user.getUsername(),
                        bookTitle,
                        record.getDueDate().format(fmt)
                );
            } catch (Exception ignore) {
                // 单条失败不影响其他记录
            }
            // 标记状态为逾期（下次不会再查到这条记录）
            record.setStatus(BorrowRecord.Status.overdue);
            borrowRepo.save(record);
        }
    }
}
