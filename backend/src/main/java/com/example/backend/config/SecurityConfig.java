package com.example.backend.config;

import com.example.backend.filter.JwtAuthFilter;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                // 开放登录、注册、图书、分类接口
                // 1. 所有公开接口（Day2+Day3全部免登录）
                .antMatchers("/auth/**","/books/**","/category/**","/borrow/**","/reserve/**","/fine/**","/review/**","/notif/**").permitAll()

                // 2. 静态资源（含 admin 前端页面）全部放行
                .antMatchers(
                        "/", "/error",
                        "/login.html", "/register.html", "/index.html", "/detail.html", "/profile.html",
                        "/admin/**",          // ← 放行 admin 目录下所有静态文件（HTML/JS/CSS/图片）
                        "/*.html",
                        "/css/**", "/js/**", "/images/**", "/favicon.ico"
                ).permitAll()

                // 3. 管理员 API 接口需 admin 角色（不影响上面的静态资源放行）
                .antMatchers("/admin/books/**","/admin/borrow/**","/admin/user/**","/admin/category/**","/admin/reserve/**","/admin/fine/**").hasRole("admin")
                // 4. 所有其他接口必须登录
                .anyRequest().authenticated();
    }
}
