package com.example.backend.Test;

import com.example.backend.util.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestMailController {
    @Autowired
    private EmailUtil emailUtil;

    // 手动测试邮件发送
    @GetMapping("/sendMail")
    public ResponseEntity<?> testMail(
            @RequestParam String email,
            @RequestParam String userName,
            @RequestParam String bookName
    ){
        try{
            emailUtil.sendOverdueNotice(email, userName, bookName, LocalDateTime.now().minusDays(2));
            return ResponseEntity.ok(Map.of("success",true,"msg","测试邮件发送成功，请查收邮箱"));
        }catch (Exception e){
            return ResponseEntity.ok(Map.of("success",false,"msg","发送失败："+e.getMessage()));
        }
    }
}
