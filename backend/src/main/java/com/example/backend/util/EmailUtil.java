package com.example.backend.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EmailUtil {
    @Autowired
    private JavaMailSender mailSender;

    // 发送逾期提醒邮件
    public void sendOverdueNotice(String toEmail, String userName, String bookName, LocalDateTime dueDate) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("2051095129@qq.com");
        msg.setTo(toEmail);
        msg.setSubject("【图书馆逾期提醒】您有图书已超期！");
        String content = "尊敬的"+userName+"先生/女士：\n您好！您借阅的《"+bookName+"》已超过归还日期"+dueDate+"，请尽快归还，逾期将持续产生罚金。";
        msg.setText(content);
        mailSender.send(msg);
    }
}
