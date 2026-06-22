package com.example.backend.service;

import com.example.backend.entity.BorrowRecord;
import com.example.backend.entity.User;
import com.example.backend.repository.BorrowRecordRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.util.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OverdueTaskService {
    @Autowired
    private BorrowRecordRepository borrowRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private EmailUtil emailUtil;

    // 每日凌晨0点执行定时任务，扫描逾期未还图书并发送邮件
    @Scheduled(cron = "0 0 0 * * ?")
    //测试 10秒发一次
    // @Scheduled(cron = "0/10 * * * * ?")
    public void autoSendOverdueMail() {
        // 查询所有逾期未还记录
        List<BorrowRecord> overdueList = borrowRepo.findOverdueRecords();
        for(BorrowRecord record : overdueList) {
            User user = userRepo.findById(record.getUserId()).get();
            // 用户邮箱不为空才发送
            if(user.getEmail() != null && !user.getEmail().isEmpty()) {
                emailUtil.sendOverdueNotice(
                        user.getEmail(),
                        user.getRealName(),
                        record.getBook().getTitle(),
                        record.getDueDate()
                );
            }
            // 更新记录状态为overdue
            record.setStatus(BorrowRecord.Status.overdue);
            borrowRepo.save(record);
        }
    }
}
