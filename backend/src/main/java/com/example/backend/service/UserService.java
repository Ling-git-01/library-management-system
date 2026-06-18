package com.example.backend.service;

import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.util.PasswordEncoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
}
