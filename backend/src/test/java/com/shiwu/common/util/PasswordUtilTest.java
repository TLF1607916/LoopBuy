package com.shiwu.common.util;

import com.shiwu.test.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 密码工具类测试
 * 测试密码加密、验证等功能
 * 
 * 测试覆盖：
 * 1. 密码加密功能
 * 2. 密码验证功能
 * 3. 边界条件测试
 * 4. 安全性测试
 * 5. 性能测试
 */
@DisplayName("密码工具类测试")
public class PasswordUtilTest extends TestBase {

    @Test
    @DisplayName("密码加密功能 - 正常情况")
    public void testEncrypt_Normal() {
        // Given
        String plainPassword = "password123";

        // When
        String hashedPassword = PasswordUtil.encrypt(plainPassword);

        // Then
        assertNotNull(hashedPassword, "加密后的密码不应为null");
        assertFalse(hashedPassword.isEmpty(), "加密后的密码不应为空");
        assertNotEquals(plainPassword, hashedPassword, "加密后的密码应与原密码不同");
        assertTrue(hashedPassword.startsWith("$2a$"), "BCrypt加密的密码应以$2a$开头");
        assertTrue(hashedPassword.length() >= 60, "BCrypt加密的密码长度应该至少60字符");
    }

    @Test
    @DisplayName("密码验证功能 - 正确密码")
    public void testMatches_Correct() {
        // Given
        String plainPassword = "password123";
        String hashedPassword = PasswordUtil.encrypt(plainPassword);

        // When
        boolean isValid = PasswordUtil.matches(plainPassword, hashedPassword);

        // Then
        assertTrue(isValid, "正确的密码应该验证通过");
    }

    @Test
    @DisplayName("密码验证功能 - 错误密码")
    public void testMatches_Incorrect() {
        // Given
        String plainPassword = "password123";
        String wrongPassword = "wrongpassword";
        String hashedPassword = PasswordUtil.encrypt(plainPassword);

        // When
        boolean isValid = PasswordUtil.matches(wrongPassword, hashedPassword);

        // Then
        assertFalse(isValid, "错误的密码应该验证失败");
    }

    @Test
    @DisplayName("密码验证功能 - null参数")
    public void testMatches_NullParameters() {
        // Given
        String hashedPassword = PasswordUtil.encrypt("password123");

        // When & Then
        assertFalse(PasswordUtil.matches(null, hashedPassword),
                   "null明文密码应该验证失败");
        assertFalse(PasswordUtil.matches("password123", null),
                   "null加密密码应该验证失败");
        assertFalse(PasswordUtil.matches(null, null),
                   "null参数应该验证失败");
    }

    @Test
    @DisplayName("密码验证功能 - 空字符串")
    public void testMatches_EmptyStrings() {
        // Given
        String hashedPassword = PasswordUtil.encrypt("password123");

        // When & Then
        assertFalse(PasswordUtil.matches("", hashedPassword),
                   "空字符串密码应该验证失败");
        assertFalse(PasswordUtil.matches("password123", ""),
                   "空字符串加密密码应该验证失败");
    }

    @Test
    @DisplayName("密码验证功能 - 无效的加密密码格式")
    public void testMatches_InvalidHashFormat() {
        // Given
        String plainPassword = "password123";
        String[] invalidHashes = {
            "invalid_hash_format",
            "not_bcrypt_hash",
            "$2a$invalid",
            "plain_text_password",
            "md5_like_hash_32_characters_long"
        };

        // When & Then
        for (String invalidHash : invalidHashes) {
            boolean isValid = PasswordUtil.matches(plainPassword, invalidHash);
            assertFalse(isValid, "无效的加密密码格式应该验证失败: " + invalidHash);
        }
    }

    @Test
    @DisplayName("相同密码多次加密结果不同")
    public void testMultipleEncryptionDifferentResults() {
        // Given
        String plainPassword = "password123";

        // When
        String hash1 = PasswordUtil.encrypt(plainPassword);
        String hash2 = PasswordUtil.encrypt(plainPassword);
        String hash3 = PasswordUtil.encrypt(plainPassword);

        // Then
        assertNotEquals(hash1, hash2, "相同密码多次加密应产生不同的结果（盐值不同）");
        assertNotEquals(hash2, hash3, "相同密码多次加密应产生不同的结果（盐值不同）");
        assertNotEquals(hash1, hash3, "相同密码多次加密应产生不同的结果（盐值不同）");

        // But all should verify correctly
        assertTrue(PasswordUtil.matches(plainPassword, hash1), "第一个加密结果应该验证通过");
        assertTrue(PasswordUtil.matches(plainPassword, hash2), "第二个加密结果应该验证通过");
        assertTrue(PasswordUtil.matches(plainPassword, hash3), "第三个加密结果应该验证通过");
    }

    @Test
    @DisplayName("测试特殊字符密码")
    public void testSpecialCharacterPasswords() {
        // Given
        String[] specialPasswords = {
            "password!@#$%^&*()",
            "密码123",
            "пароль123",
            "パスワード123",
            "🔒🔑password",
            "pass word with spaces",
            "pass\nword\twith\rwhitespace",
            "\"quoted'password\"",
            "password\\with\\backslashes",
            "password/with/slashes"
        };
        
        // When & Then
        for (String password : specialPasswords) {
            String hashedPassword = PasswordUtil.encrypt(password);
            assertNotNull(hashedPassword, "特殊字符密码应该能够加密: " + password);
            assertTrue(PasswordUtil.matches(password, hashedPassword),
                      "特殊字符密码应该能够验证: " + password);
        }
    }

    @Test
    @DisplayName("测试不同长度的密码")
    public void testVariousPasswordLengths() {
        // Given
        String[] passwords = {
            "1",                    // 1字符
            "12",                   // 2字符
            "123",                  // 3字符
            "password",             // 8字符
            "verylongpassword123",  // 20字符
            generateString("a", 100),   // 100字符
            generateString("a", 1000)   // 1000字符
        };

        // When & Then
        for (String password : passwords) {
            String hashedPassword = PasswordUtil.encrypt(password);
            assertNotNull(hashedPassword, "任意长度密码应该能够加密: 长度=" + password.length());
            assertTrue(PasswordUtil.matches(password, hashedPassword),
                      "任意长度密码应该能够验证: 长度=" + password.length());
        }
    }

    /**
     * 生成重复字符串（Java 8兼容）
     */
    private String generateString(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    @Test
    @DisplayName("测试空字符串密码")
    public void testEmptyPassword() {
        // Given
        String emptyPassword = "";

        // When
        String hashedPassword = PasswordUtil.encrypt(emptyPassword);

        // Then
        assertNotNull(hashedPassword, "空字符串密码应该能够加密");
        assertTrue(PasswordUtil.matches(emptyPassword, hashedPassword),
                  "空字符串密码应该能够验证");
    }

    @Test
    @DisplayName("测试null密码加密")
    public void testEncryptNullPassword() {
        // When
        String result = PasswordUtil.encrypt(null);

        // Then
        assertNull(result, "null密码应该返回null");
    }

    @Test
    @DisplayName("测试密码强度验证")
    public void testPasswordStrengthVariations() {
        // Given
        String[] passwords = {
            "123",                      // 弱密码
            "password",                 // 常见密码
            "password123",              // 中等密码
            "Password123",              // 包含大小写和数字
            "P@ssw0rd123!",            // 强密码
            "MyVerySecurePassword2023!" // 很强的密码
        };

        // When & Then
        for (String password : passwords) {
            String hashedPassword = PasswordUtil.encrypt(password);
            assertTrue(PasswordUtil.matches(password, hashedPassword),
                      "所有强度的密码都应该能够正确验证: " + password);
        }
    }

    @Test
    @DisplayName("测试大小写敏感性")
    public void testCaseSensitivity() {
        // Given
        String password = "Password123";
        String hashedPassword = PasswordUtil.encrypt(password);

        // When & Then
        assertTrue(PasswordUtil.matches("Password123", hashedPassword),
                  "原密码应该验证通过");
        assertFalse(PasswordUtil.matches("password123", hashedPassword),
                   "小写密码应该验证失败");
        assertFalse(PasswordUtil.matches("PASSWORD123", hashedPassword),
                   "大写密码应该验证失败");
        assertFalse(PasswordUtil.matches("PassWord123", hashedPassword),
                   "不同大小写密码应该验证失败");
    }

    @Test
    @DisplayName("测试性能 - 加密时间")
    public void testHashingPerformance() {
        // Given
        String password = "testpassword123";
        int iterations = 10;
        
        // When
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            PasswordUtil.encrypt(password);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Then
        assertTrue(duration < 10000, 
                  iterations + "次密码加密应该在10秒内完成，实际耗时: " + duration + "ms");
        
        // 平均每次加密时间应该合理
        double avgTime = (double) duration / iterations;
        assertTrue(avgTime < 1000, 
                  "平均每次加密时间应该少于1秒，实际: " + avgTime + "ms");
    }

    @Test
    @DisplayName("测试性能 - 验证时间")
    public void testVerificationPerformance() {
        // Given
        String password = "testpassword123";
        String hashedPassword = PasswordUtil.encrypt(password);
        int iterations = 100;

        // When
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            PasswordUtil.matches(password, hashedPassword);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Then
        assertTrue(duration < 5000, 
                  iterations + "次密码验证应该在5秒内完成，实际耗时: " + duration + "ms");
        
        // 平均每次验证时间应该合理
        double avgTime = (double) duration / iterations;
        assertTrue(avgTime < 100, 
                  "平均每次验证时间应该少于100ms，实际: " + avgTime + "ms");
    }

    @Test
    @DisplayName("测试并发安全性")
    public void testConcurrentSafety() throws InterruptedException {
        // Given
        String password = "testpassword123";
        int threadCount = 5;
        Thread[] threads = new Thread[threadCount];
        boolean[] results = new boolean[threadCount];
        
        // When
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    // 每个线程执行加密和验证操作
                    String hashedPassword = PasswordUtil.encrypt(password);
                    boolean isValid = PasswordUtil.matches(password, hashedPassword);
                    results[index] = isValid;
                } catch (Exception e) {
                    results[index] = false;
                }
            });
            threads[i].start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join(5000); // 5 second timeout
        }
        
        // Then
        for (int i = 0; i < threadCount; i++) {
            assertTrue(results[i], "线程 " + i + " 应该成功完成密码操作");
        }
    }

    @Test
    @DisplayName("测试已知测试向量")
    public void testKnownTestVectors() {
        // Given - 使用项目中的已知密码哈希
        String knownPassword = TEST_PASSWORD; // "123456"
        String knownHash = TEST_PASSWORD_HASH; // 已知的BCrypt哈希
        
        // When
        boolean isValid = PasswordUtil.matches(knownPassword, knownHash);

        // Then
        assertTrue(isValid, "已知的测试密码应该验证通过");

        // 验证错误密码
        assertFalse(PasswordUtil.matches("wrongpassword", knownHash),
                   "错误密码应该验证失败");
    }
}
