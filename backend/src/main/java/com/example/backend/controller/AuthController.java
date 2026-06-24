package com.example.backend.controller;

import com.example.backend.entity.User;
import com.example.backend.service.UserService;
import com.example.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    // 用户注册
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User save = userService.register(user);
            Map<String,Object> res = new HashMap<>();
            res.put("success",true);
            res.put("msg","注册成功");
            res.put("userId",save.getId());
            return ResponseEntity.ok(res);
        }catch (RuntimeException e){
            Map<String,Object> res = new HashMap<>();
            res.put("success",false);
            res.put("msg",e.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

    // 用户登录返回JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> req) {
        String username = req.get("username");
        String pwd = req.get("password");
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(username,pwd));
            UserDetails userDetail = userService.loadUserByUsername(username);
            String token = jwtUtil.generateToken(userDetail);
            User user = userService.findUserByName(username);
            // 检查用户状态：禁用用户禁止登录
            if (user.getStatus() == null || user.getStatus() != 1) {
                Map<String,Object> errRes = new HashMap<>();
                errRes.put("success",false);
                errRes.put("msg","该账号已被禁用，请联系管理员");
                return ResponseEntity.badRequest().body(errRes);
            }
            Map<String,Object> res = new HashMap<>();
            res.put("success",true);
            res.put("token",token);
            res.put("username",username);
            res.put("role",user.getRole());
            res.put("userId",user.getId());
            return ResponseEntity.ok(res);
        }catch (Exception e){
            Map<String,Object> res = new HashMap<>();
            res.put("success",false);
            res.put("msg","账号或密码错误");
            return ResponseEntity.badRequest().body(res);
        }
    }
}
