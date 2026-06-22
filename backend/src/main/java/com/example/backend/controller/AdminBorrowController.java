package com.example.backend.controller;

import com.example.backend.entity.BorrowRecord;
import com.example.backend.entity.Fine;
import com.example.backend.service.BorrowRecordService;
import com.example.backend.service.FineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/borrow")
public class AdminBorrowController {
    @Autowired
    private BorrowRecordService borrowService;
    @Autowired
    private FineService fineService;

    // 查询所有借阅记录
    @GetMapping("/list")
    public ResponseEntity<?> listAllBorrow() {
        List<BorrowRecord> list = borrowService.listAllBorrow();
        return ResponseEntity.ok(Map.of("success", true, "count", list.size(), "data", list));
    }

    // 强制归还图书（管理员操作）
    @PutMapping("/forceReturn/{borrowId}")
    public ResponseEntity<?> forceReturn(@PathVariable Integer borrowId) {
        try {
            BorrowRecord record = borrowService.returnBook(borrowId);
            return ResponseEntity.ok(Map.of("success", true, "msg", "强制归还成功", "data", record));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "msg", e.getMessage()));
        }
    }

    // 查询所有罚金记录
    @GetMapping("/fine/list")
    public ResponseEntity<?> listAllFine() {
        List<Fine> list = fineService.listAllFine();
        return ResponseEntity.ok(Map.of("success", true, "count", list.size(), "data", list));
    }

    // 批量处理罚金（标记为已缴/减免）
    @SuppressWarnings("unchecked")//隐藏警告（黄色警告：未检查的 Object 强转 List<Integer>）
    @PutMapping("/fine/batchHandle")
    public ResponseEntity<?> batchHandleFine(@RequestBody Map<String, Object> params) {
        try {
            List<Integer> fineIds = (List<Integer>) params.get("fineIds");
            String type = (String) params.get("type"); // "paid" / "waive"
            fineService.batchHandleFine(fineIds, type);
            return ResponseEntity.ok(Map.of("success", true, "msg", "罚金处理成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "msg", e.getMessage()));
        }
    }
}
