package com.example.backend.controller;

import com.example.backend.entity.User;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/user")
public class AdminUserController {
    @Autowired
    private UserService userService;

    // 查询所有用户
    @GetMapping("/list")
    public ResponseEntity<?> listAllUser() {
        List<User> list = userService.listAllUser();
        return ResponseEntity.ok(Map.of("success", true, "count", list.size(), "data", list));
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
