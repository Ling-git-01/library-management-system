package com.example.backend.controller;

import com.example.backend.entity.BorrowRecord;
import com.example.backend.entity.Fine;
import com.example.backend.service.BorrowRecordService;
import com.example.backend.service.FineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
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

    // 查询所有罚金记录（手动组装：附带 username、bookTitle、overdueDays）
    @GetMapping("/fine/list")
    public ResponseEntity<?> listAllFine() {
        List<Fine> fines = fineService.listAllFine();
        List<Map<String, Object>> data = new ArrayList<>();
        for (Fine f : fines) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", f.getId());
            m.put("userId", f.getUserId());
            m.put("borrowId", f.getBorrowId());
            m.put("amount", f.getAmount());
            m.put("reason", f.getReason());
            m.put("status", f.getStatus());
            m.put("createdAt", f.getCreatedAt());
            m.put("paidAt", f.getPaidAt());
            // 借阅关联对象（避免 JPA 代理失败，包成 try/catch）
            String username = "", bookTitle = "";
            LocalDateTime dueDate = null, returnDate = null;
            try {
                BorrowRecord br = f.getBorrowRecord();
                if (br != null) {
                    dueDate = br.getDueDate();
                    returnDate = br.getReturnDate();
                    try {
                        if (br.getUser() != null) username = br.getUser().getUsername();
                    } catch (Exception ignore) {}
                    try {
                        if (br.getBook() != null) bookTitle = br.getBook().getTitle();
                    } catch (Exception ignore) {}
                }
            } catch (Exception ignore) {}
            m.put("username", username);
            m.put("bookTitle", bookTitle);
            // 逾期天数 = returnDate - dueDate（已还）；未还则取今天 - dueDate
            int overdueDays = 0;
            if (dueDate != null) {
                LocalDateTime ref = returnDate != null ? returnDate : LocalDateTime.now();
                if (ref.isAfter(dueDate)) {
                    overdueDays = (int) ChronoUnit.DAYS.between(dueDate, ref);
                    if (overdueDays < 0) overdueDays = 0;
                }
            }
            m.put("overdueDays", overdueDays);
            data.add(m);
        }
        return ResponseEntity.ok(Map.of("success", true, "count", data.size(), "data", data));
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
