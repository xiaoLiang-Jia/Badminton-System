package com.badminton.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoder {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 加密密码
     */
    public static String encode(String password) {
        return encoder.encode(password);
    }

    /**
     * 验证密码
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        // Handle $2b$ prefix (bcryptjs uses this prefix)
        if (encodedPassword != null && encodedPassword.startsWith("$2b$")) {
            encodedPassword = "$2a$" + encodedPassword.substring(4);
        }
        return encoder.matches(rawPassword, encodedPassword);
    }
}
