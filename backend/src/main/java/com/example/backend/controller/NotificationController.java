package com.example.backend.controller;

import com.example.backend.entity.Notification;
import com.example.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notif")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    // 获取用户通知列表
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(@RequestParam("userId") Integer userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Notification> list = notificationService.getUserNotifications(userId);
            result.put("success", true);
            result.put("data", list);
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    // 获取未读通知数
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> unreadCount(@RequestParam("userId") Integer userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            long count = notificationService.getUnreadCount(userId);
            result.put("success", true);
            result.put("data", count);
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    // 标记通知已读
    @PutMapping("/read/{id}")
    public ResponseEntity<Map<String, Object>> markRead(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            notificationService.markAsRead(id);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    // 标记全部已读
    @PutMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllRead(@RequestParam("userId") Integer userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            notificationService.markAllAsRead(userId);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    // 删除通知
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id,
                                                       @RequestParam("userId") Integer userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            notificationService.deleteNotification(id, userId);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }
}
