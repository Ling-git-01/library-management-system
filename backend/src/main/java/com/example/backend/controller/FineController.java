package com.example.backend.controller;

import com.example.backend.entity.Fine;
import com.example.backend.service.FineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/fine")
public class FineController {
    @Autowired
    private FineService fineService;

    // 缴纳罚金
    @PutMapping("/pay/{fineId}")
    public ResponseEntity<?> payFine(@PathVariable Integer fineId){
        try{
            Fine fine = fineService.payFine(fineId);
            return ResponseEntity.ok(Map.of("success",true,"msg","罚金缴纳完成","data",fine));
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of("success",false,"msg",e.getMessage()));
        }
    }

    // 查询用户全部罚金
    @GetMapping("/user/all/{userId}")
    public ResponseEntity<?> allFine(@PathVariable Integer userId){
        List<Fine> list = fineService.getUserAllFine(userId);
        return ResponseEntity.ok(Map.of("success",true,"count",list.size(),"data",list));
    }

    // 查询未缴罚金
    @GetMapping("/user/unpaid/{userId}")
    public ResponseEntity<?> unpaidFine(@PathVariable Integer userId){
        List<Fine> list = fineService.getUserUnpaidFine(userId);
        return ResponseEntity.ok(Map.of("success",true,"count",list.size(),"data",list));
    }
}
