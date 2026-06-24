package com.example.backend.controller;

import com.example.backend.entity.Reservation;
import com.example.backend.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/reserve")
public class AdminReserveController {
    @Autowired
    private ReservationService reservationService;

    // 管理员查看全部预约
    @GetMapping("/list")
    public ResponseEntity<?> listAll() {
        List<Reservation> list = reservationService.listAll();
        return ResponseEntity.ok(Map.of("success", true, "count", list.size(), "data", list));
    }

    // 确认满足预约（pending -> fulfilled）
    @PutMapping("/fulfill/{id}")
    public ResponseEntity<?> fulfill(@PathVariable Integer id) {
        try {
            Reservation updated = reservationService.fulfillReserve(id);
            return ResponseEntity.ok(Map.of("success", true, "msg", "已确认满足", "data", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "msg", e.getMessage()));
        }
    }

    // 管理员取消预约
    @PutMapping("/cancel/{id}")
    public ResponseEntity<?> adminCancel(@PathVariable Integer id) {
        try {
            Reservation updated = reservationService.adminCancelReserve(id);
            return ResponseEntity.ok(Map.of("success", true, "msg", "已取消", "data", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "msg", e.getMessage()));
        }
    }

    // 管理员物理删除预约
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            reservationService.deleteReserve(id);
            return ResponseEntity.ok(Map.of("success", true, "msg", "已删除"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "msg", e.getMessage()));
        }
    }
}
