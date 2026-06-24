package com.example.backend.service;

import com.example.backend.entity.Book;
import com.example.backend.entity.Reservation;
import com.example.backend.repository.BookRepository;
import com.example.backend.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository resRepo;
    @Autowired
    private BookRepository bookRepo;

    // 预约图书
    @Transactional
    public Reservation reserveBook(Integer userId, Integer bookId) {
        Book book = bookRepo.findById(bookId).orElseThrow(() -> new RuntimeException("图书不存在"));
        // 已有待处理预约则禁止重复预约
        boolean existPending = resRepo.existsByUserIdAndBookIdAndStatus(userId, bookId, Reservation.Status.pending);
        if(existPending) {
            throw new RuntimeException("你已预约此书，请勿重复操作");
        }
        // 查询是否存在已取消的旧预约
        Optional<Reservation> cancelResOpt = resRepo.findByUserIdAndBookIdAndStatus(userId, bookId, Reservation.Status.cancelled);
        if(cancelResOpt.isPresent()){
            Reservation oldRes = cancelResOpt.get();
            oldRes.setStatus(Reservation.Status.pending);
            oldRes.setReserveDate(LocalDateTime.now());
            oldRes.setExpireDate(LocalDateTime.now().plusDays(3));
            return resRepo.save(oldRes);
        }
        // 无旧取消记录，新建预约
        Reservation res = new Reservation();
        res.setUserId(userId);
        res.setBookId(bookId);
        res.setReserveDate(LocalDateTime.now());
        res.setExpireDate(LocalDateTime.now().plusDays(3)); // 预约3天有效期
        res.setStatus(Reservation.Status.pending);
        return resRepo.save(res);
    }

    // 取消预约
    @Transactional
    public Reservation cancelReserve(Integer resId) {
        Reservation res = resRepo.findById(resId).orElseThrow(() -> new RuntimeException("预约记录不存在"));
        if(res.getStatus() != Reservation.Status.pending) throw new RuntimeException("仅待处理预约可取消");
        res.setStatus(Reservation.Status.cancelled);
        return resRepo.save(res);
    }

    // 管理员确认满足预约（仅 pending 可履行）
    @Transactional
    public Reservation fulfillReserve(Integer resId) {
        Reservation res = resRepo.findById(resId).orElseThrow(() -> new RuntimeException("预约记录不存在"));
        if(res.getStatus() != Reservation.Status.pending) throw new RuntimeException("仅待处理预约可确认满足");
        res.setStatus(Reservation.Status.fulfilled);
        return resRepo.save(res);
    }

    // 管理员强制取消任意状态的预约
    @Transactional
    public Reservation adminCancelReserve(Integer resId) {
        Reservation res = resRepo.findById(resId).orElseThrow(() -> new RuntimeException("预约记录不存在"));
        if(res.getStatus() == Reservation.Status.fulfilled) throw new RuntimeException("已履行的预约不可取消");
        res.setStatus(Reservation.Status.cancelled);
        return resRepo.save(res);
    }

    // 管理员物理删除预约
    @Transactional
    public void deleteReserve(Integer resId) {
        if (!resRepo.existsById(resId)) {
            throw new RuntimeException("预约记录不存在");
        }
        resRepo.deleteById(resId);
    }

    public List<Reservation> getUserReserve(Integer userId) {
        return resRepo.findByUserId(userId);
    }

    // 管理员查询全部预约
    public List<Reservation> listAll() {
        return resRepo.findAll();
    }
}
