package com.example.backend.repository;

import com.example.backend.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord,Integer> {
    // 根据用户ID查询全部借阅记录
    List<BorrowRecord> findByUserId(Integer userId);
    // 查询未归还的借阅
    List<BorrowRecord> findByUserIdAndStatus(Integer userId, BorrowRecord.Status status);
    // 检查图书是否被该用户借出未还
    boolean existsByUserIdAndBookIdAndStatus(Integer userId, Integer bookId, BorrowRecord.Status status);
    // 查询逾期未还记录
    @Query("SELECT br FROM BorrowRecord br WHERE br.dueDate < NOW() AND br.status = 'borrowing'")
    List<BorrowRecord> findOverdueRecords();
}
