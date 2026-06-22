package com.example.backend.service;

import com.example.backend.entity.Fine;
import com.example.backend.repository.FineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FineService {
    @Autowired
    private FineRepository fineRepo;

    // 缴纳罚金
    @Transactional
    public Fine payFine(Integer fineId) {
        Fine fine = fineRepo.findById(fineId).orElseThrow(() -> new RuntimeException("罚金记录不存在"));
        if(fine.getStatus() == Fine.Status.paid) throw new RuntimeException("该罚金已缴纳");
        fine.setStatus(Fine.Status.paid);
        fine.setPaidAt(LocalDateTime.now());
        return fineRepo.save(fine);
    }

    // 查询用户所有罚金
    public List<Fine> getUserAllFine(Integer userId) {
        return fineRepo.findByUserId(userId);
    }
    // 查询未缴罚金
    public List<Fine> getUserUnpaidFine(Integer userId) {
        return fineRepo.findByUserIdAndStatus(userId, Fine.Status.unpaid);
    }

    @Transactional
    public void batchHandleFine(List<Integer> fineIds, String type) {
        for (Integer fineId : fineIds) {
            Fine fine = fineRepo.findById(fineId).orElseThrow(() -> new RuntimeException("罚金记录不存在：" + fineId));
            if ("paid".equals(type)) {
                fine.setStatus(Fine.Status.paid);
                fine.setPaidAt(LocalDateTime.now());
            } else if ("waive".equals(type)) {
                fineRepo.delete(fine); // 减免=删除罚金记录
                continue;
            } else {
                throw new RuntimeException("不支持的处理类型：" + type);
            }
            fineRepo.save(fine);
        }
    }

    // 查询所有罚金
    public List<Fine> listAllFine() {
        return fineRepo.findAll();
    }
}
