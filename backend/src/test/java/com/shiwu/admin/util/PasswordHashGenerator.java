package com.shiwu.admin.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * 密码哈希生成器，用于生成测试数据
 */
public class PasswordHashGenerator {
    public static void main(String[] args) {
        String password = "admin123";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println("原始密码: " + password);
        System.out.println("BCrypt哈希: " + hashedPassword);
        
        // 验证哈希是否正确
        boolean matches = BCrypt.checkpw(password, hashedPassword);
        System.out.println("验证结果: " + matches);
    }
}
