package com.example.backend.service;

import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.util.PasswordEncoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepo;

    // 用户注册
    public User register(User user) {
        if(userRepo.existsByUsername(user.getUsername())){
            throw new RuntimeException("用户名已存在");
        }
        // 密码加密
        user.setPassword(PasswordEncoderUtil.encode(user.getPassword()));
        user.setRegisterTime(LocalDateTime.now());
        if(user.getRole() == null) user.setRole(User.Role.reader);
        user.setStatus(1);
        user.setMaxBorrowCount(5);
        return userRepo.save(user);
    }

    // Security登录校验
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> opt = userRepo.findByUsername(username);
        if(opt.isEmpty()) throw new UsernameNotFoundException("用户不存在");
        User u = opt.get();
        return org.springframework.security.core.userdetails.User.builder()
                .username(u.getUsername())
                .password(u.getPassword())
                .roles(u.getRole().name())
                .build();
    }

    public User findUserByName(String username) {
        return userRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    // 查询所有用户
    public List<User> listAllUser() {
        return userRepo.findAll();
    }

    // 修改用户角色/状态
    @Transactional
    public User updateUserRoleAndStatus(Integer id, String roleStr, Integer status) {
        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("用户不存在"));
        if (roleStr != null) {
            user.setRole(User.Role.valueOf(roleStr));
        }
        if (status != null) {
            user.setStatus(status);
        }
        return userRepo.save(user);
    }

    // 修改用户最大借阅上限
    @Transactional
    public User updateMaxBorrowCount(Integer id, Integer maxBorrowCount) {
        if (maxBorrowCount == null) {
            throw new RuntimeException("最大借阅数不能为空");
        }
        if (maxBorrowCount < 0) {
            throw new RuntimeException("最大借阅数不能为负数");
        }
        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setMaxBorrowCount(maxBorrowCount);
        return userRepo.save(user);
    }

    // 重置密码
    @Transactional
    public void resetPassword(Integer id, String newPwd) {
        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setPassword(PasswordEncoderUtil.encode(newPwd));
        userRepo.save(user);
    }

    // 删除用户（逻辑删除：修改状态为0）
    @Transactional
    public void deleteUser(Integer id) {
        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setStatus(0);
        userRepo.save(user);
    }
}
