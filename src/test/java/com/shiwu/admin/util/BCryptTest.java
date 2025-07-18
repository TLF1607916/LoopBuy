package com.shiwu.admin.util;

import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

/**
 * BCrypt测试，用于生成正确的密码哈希
 */
public class BCryptTest {
    
    @Test
    public void generatePasswordHash() {
        String password = "admin123";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        
        System.out.println("原始密码: " + password);
        System.out.println("BCrypt哈希: " + hashedPassword);
        
        // 验证哈希是否正确
        boolean matches = BCrypt.checkpw(password, hashedPassword);
        System.out.println("验证结果: " + matches);
        
        // 生成SQL更新语句
        System.out.println("\nSQL更新语句:");
        System.out.println("UPDATE administrator SET password = '" + hashedPassword + "' WHERE username = 'admin';");
        System.out.println("UPDATE administrator SET password = '" + hashedPassword + "' WHERE username = 'moderator';");
    }
}
