package com.example.backend.service;

import com.example.backend.entity.Notification;
import com.example.backend.entity.Reservation;
import com.example.backend.entity.Book;
import com.example.backend.repository.NotificationRepository;
import com.example.backend.repository.ReservationRepository;
import com.example.backend.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepo;
    @Autowired
    private ReservationRepository reservationRepo;
    @Autowired
    private BookRepository bookRepo;

    // 获取用户全部通知
    public List<Notification> getUserNotifications(Integer userId) {
        return notificationRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // 获取用户未读通知数
    public long getUnreadCount(Integer userId) {
        return notificationRepo.countByUserIdAndRead(userId, false);
    }

    // 标记通知已读
    public Notification markAsRead(Integer notifId) {
        Notification n = notificationRepo.findById(notifId)
                .orElseThrow(() -> new RuntimeException("通知不存在"));
        n.setRead(true);
        return notificationRepo.save(n);
    }

    // 标记用户全部通知已读
    public void markAllAsRead(Integer userId) {
        List<Notification> list = notificationRepo.findByUserIdAndReadOrderByCreatedAtDesc(userId, false);
        list.forEach(n -> n.setRead(true));
        notificationRepo.saveAll(list);
    }

    // 删除通知
    public void deleteNotification(Integer notifId, Integer userId) {
        Notification n = notificationRepo.findById(notifId)
                .orElseThrow(() -> new RuntimeException("通知不存在"));
        if (!n.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此通知");
        }
        notificationRepo.delete(n);
    }

    // 图书归还后，给预约该书的用户发送通知
    public void sendReserveAvailableNotification(Integer bookId) {
        Optional<Book> bookOpt = bookRepo.findById(bookId);
        if (!bookOpt.isPresent()) return;
        Book book = bookOpt.get();

        // 查找该书的所有 pending 预约用户
        List<Reservation> reservations = reservationRepo.findByBookIdAndStatus(bookId, Reservation.Status.pending);
        for (Reservation res : reservations) {
            // 避免重复通知：检查最近是否已发过相同通知
            List<Notification> existing = notificationRepo.findByUserIdOrderByCreatedAtDesc(res.getUserId());
            boolean alreadyNotified = existing.stream()
                    .filter(n -> n.getBookId() != null && n.getBookId().equals(bookId))
                    .filter(n -> n.getType() == Notification.Type.reserve_available)
                    .filter(n -> n.getCreatedAt() != null && n.getCreatedAt().isAfter(LocalDateTime.now().minusDays(7)))
                    .findFirst().isPresent();
            if (alreadyNotified) continue;

            Notification notif = new Notification();
            notif.setUserId(res.getUserId());
            notif.setBookId(bookId);
            notif.setTitle("预约可借阅提醒");
            notif.setContent("您预约的《" + book.getTitle() + "》已可借阅，请尽快前往借阅！");
            notif.setType(Notification.Type.reserve_available);
            notif.setRead(false);
            notif.setCreatedAt(LocalDateTime.now());
            notificationRepo.save(notif);
        }
    }
}
