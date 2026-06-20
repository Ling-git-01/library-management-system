package com.example.backend.controller;

import com.example.backend.entity.Reservation;
import com.example.backend.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reserve")
public class ReservationController {
    @Autowired
    private ReservationService reserveService;

    // 预约图书
    @PostMapping("/add")
    public ResponseEntity<?> addReserve(@RequestBody Map<String,Integer> params){
        try{
            Reservation res = reserveService.reserveBook(params.get("userId"), params.get("bookId"));
            return ResponseEntity.ok(Map.of("success",true,"msg","预约成功","data",res));
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of("success",false,"msg",e.getMessage()));
        }
    }

    // 取消预约
    @PutMapping("/cancel/{resId}")
    public ResponseEntity<?> cancelReserve(@PathVariable Integer resId){
        try{
            Reservation res = reserveService.cancelReserve(resId);
            return ResponseEntity.ok(Map.of("success",true,"msg","取消预约成功","data",res));
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of("success",false,"msg",e.getMessage()));
        }
    }

    // 查询用户预约
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserReserve(@PathVariable Integer userId){
        List<Reservation> list = reserveService.getUserReserve(userId);
        return ResponseEntity.ok(Map.of("success",true,"count",list.size(),"data",list));
    }
}
