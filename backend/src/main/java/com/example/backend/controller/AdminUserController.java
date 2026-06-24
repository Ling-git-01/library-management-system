package com.example.backend.controller;

import com.example.backend.entity.BorrowRecord;
import com.example.backend.entity.User;
import com.example.backend.repository.BorrowRecordRepository;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/user")
public class AdminUserController {
    @Autowired
    private UserService userService;
    @Autowired
    private BorrowRecordRepository borrowRepo;

    // 查询所有用户（附带当前借阅数 + 角色；前端可直接读 currentBorrowCount）
    @GetMapping("/list")
    public ResponseEntity<?> listAllUser() {
        List<User> list = userService.listAllUser();
        // 一次性把借阅表里未归还的记录按 userId 分组
        List<BorrowRecord> activeBorrows = borrowRepo.findByStatus(BorrowRecord.Status.borrowing);
        Map<Integer, Integer> borrowCountMap = new HashMap<>();
        for (BorrowRecord br : activeBorrows) {
            borrowCountMap.merge(br.getUserId(), 1, Integer::sum);
        }
        List<Map<String, Object>> data = new ArrayList<>();
        for (User u : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", u.getId());
            m.put("username", u.getUsername());
            m.put("email", u.getEmail());
            m.put("phone", u.getPhone());
            m.put("realName", u.getRealName());
            m.put("role", u.getRole());
            m.put("status", u.getStatus());
            m.put("maxBorrowCount", u.getMaxBorrowCount());
            m.put("currentBorrowCount", borrowCountMap.getOrDefault(u.getId(), 0));
            data.add(m);
        }
        return ResponseEntity.ok(Map.of("success", true, "count", data.size(), "data", data));
    }

    // 修改用户角色/状态
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody Map<String, Object> params) {
        try {
            User updated = userService.updateUserRoleAndStatus(id,
                    (String) params.get("role"),
                    (Integer) params.get("status"));
            return ResponseEntity.ok(Map.of("success", true, "msg", "修改用户成功", "data", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "msg", e.getMessage()));
        }
    }

    // 修改用户最大借阅上限
    @PutMapping("/maxBorrow/{id}")
    public ResponseEntity<?> updateMaxBorrowCount(@PathVariable Integer id, @RequestBody Map<String, Object> params) {
        try {
            User updated = userService.updateMaxBorrowCount(id,
                    (Integer) params.get("maxBorrowCount"));
            return ResponseEntity.ok(Map.of("success", true, "msg", "修改最大借阅数成功", "data", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "msg", e.getMessage()));
        }
    }

    // 重置用户密码
    @PutMapping("/resetPwd/{id}")
    public ResponseEntity<?> resetPassword(@PathVariable Integer id, @RequestParam String newPwd) {
        try {
            userService.resetPassword(id, newPwd);
            return ResponseEntity.ok(Map.of("success", true, "msg", "密码重置成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "msg", e.getMessage()));
        }
    }

    // 删除用户（逻辑删除）
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("success", true, "msg", "删除用户成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "msg", e.getMessage()));
        }
    }
}
