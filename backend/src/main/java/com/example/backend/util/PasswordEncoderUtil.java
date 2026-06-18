package com.example.backend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderUtil {
    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();
    public static String encode(String rawPwd) {
        return encoder.encode(rawPwd);
    }
    public static boolean match(String raw, String encode) {
        return encoder.matches(raw, encode);
    }
}
