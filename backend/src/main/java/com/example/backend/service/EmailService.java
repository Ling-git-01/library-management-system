package com.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 发送简单文本邮件
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param text    邮件正文
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        if (to == null || to.trim().isEmpty()) {
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    /**
     * 发送逾期提醒邮件
     */
    public void sendOverdueNotice(String toEmail, String username, String bookTitle, String dueDate) {
        String subject = "📖 图书馆逾期提醒";
        String text = "亲爱的 " + username + "：\n\n" +
                "您借阅的图书已逾期，请尽快归还，以免产生更多罚金。\n\n" +
                "图书名称：" + bookTitle + "\n" +
                "应还日期：" + dueDate + "\n" +
                "罚金标准：每日 0.5 元\n\n" +
                "如有疑问，请联系图书馆管理员。\n\n" +
                "图书馆管理系统";
        sendSimpleEmail(toEmail, subject, text);
    }

    /**
     * 发送归还确认 + 罚金通知邮件
     */
    public void sendReturnNotice(String toEmail, String username, String bookTitle,
                                  String returnDate, long overdueDays, String fineAmount) {
        String subject = "📖 图书归还通知";
        String text = "亲爱的 " + username + "：\n\n" +
                "您借阅的图书已归还，详情如下：\n\n" +
                "图书名称：" + bookTitle + "\n" +
                "归还日期：" + returnDate + "\n";
        if (overdueDays > 0) {
            text += "逾期天数：" + overdueDays + " 天\n" +
                    "罚金金额：" + fineAmount + " 元\n\n" +
                    "请及时缴纳罚金，以免影响后续借阅。\n";
        } else {
            text += "\n感谢您按时归还！\n";
        }
        text += "\n图书馆管理系统";
        sendSimpleEmail(toEmail, subject, text);
    }
}
