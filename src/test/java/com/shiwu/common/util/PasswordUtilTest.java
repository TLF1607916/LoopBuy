package com.shiwu.common.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * PasswordUtil测试类
 * 遵循AIR原则：Automatic, Independent, Repeatable
 * 遵循BCDE原则：Border, Correct, Design, Error
 */
public class PasswordUtilTest {

    /**
     * 测试密码加密功能 - 正常情况
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    public void testEncrypt_Success() {
        // Given: 一个有效的密码
        String password = "password123";
        
        // When: 加密密码
        String encrypted = PasswordUtil.encrypt(password);
        
        // Then: 验证结果
        assertNotNull(encrypted, "加密后的密码不应为空");
        assertTrue(encrypted.startsWith("$2a$"), "应该使用BCrypt格式");
        assertNotEquals(password, encrypted, "加密后的密码应该与原密码不同");
        
        // 验证每次加密结果都不同（因为盐值随机）
        String encrypted2 = PasswordUtil.encrypt(password);
        assertNotEquals(encrypted, encrypted2, "每次加密的结果应该不同");
    }

    /**
     * 测试密码加密功能 - 边界条件
     * BCDE原则中的Border：测试边界条件
     */
    @Test
    public void testEncrypt_NullPassword() {
        // Given: null密码
        String password = null;
        
        // When: 加密密码
        String encrypted = PasswordUtil.encrypt(password);
        
        // Then: 应该返回null
        assertNull(encrypted, "null密码加密后应该返回null");
    }

    /**
     * 测试密码验证功能 - 正常情况
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    public void testMatches_Success() {
        // Given: 一个密码和它的加密版本
        String password = "testPassword123";
        String encrypted = PasswordUtil.encrypt(password);
        
        // When & Then: 验证密码匹配
        assertTrue(PasswordUtil.matches(password, encrypted), "正确的密码应该匹配");
        assertFalse(PasswordUtil.matches("wrongPassword", encrypted), "错误的密码不应该匹配");
    }

    /**
     * 测试密码验证功能 - 边界条件
     * BCDE原则中的Border：测试边界条件
     */
    @Test
    public void testMatches_NullInputs() {
        // Given: null输入
        String validPassword = "password123";
        String validHash = PasswordUtil.encrypt(validPassword);
        
        // When & Then: 测试各种null情况
        assertFalse(PasswordUtil.matches(null, validHash), "null密码不应该匹配");
        assertFalse(PasswordUtil.matches(validPassword, null), "null哈希不应该匹配");
        assertFalse(PasswordUtil.matches(null, null), "双null不应该匹配");
    }

    /**
     * 测试BCrypt格式检查功能
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    public void testIsBCryptHash() {
        // Given: 不同格式的密码哈希
        String bcryptHash = PasswordUtil.encrypt("password123");
        String nonBcryptHash = "plaintext";
        String md5Hash = "5d41402abc4b2a76b9719d911017c592";
        
        // When & Then: 验证格式检查
        assertTrue(PasswordUtil.isBCryptHash(bcryptHash), "BCrypt哈希应该被识别");
        assertFalse(PasswordUtil.isBCryptHash(nonBcryptHash), "普通文本不应该被识别为BCrypt");
        assertFalse(PasswordUtil.isBCryptHash(md5Hash), "MD5哈希不应该被识别为BCrypt");
        assertFalse(PasswordUtil.isBCryptHash(null), "null不应该被识别为BCrypt");
    }

    /**
     * 测试密码强度检查功能
     * BCDE原则中的Border和Correct：测试边界条件和正确输入
     */
    @Test
    public void testIsStrongPassword() {
        // Given & When & Then: 测试各种密码强度
        
        // 强密码（至少8位，包含字母和数字）
        assertTrue(PasswordUtil.isStrongPassword("password123"), "包含字母和数字的8位密码应该是强密码");
        assertTrue(PasswordUtil.isStrongPassword("Test1234"), "包含大小写字母和数字的密码应该是强密码");
        
        // 弱密码
        assertFalse(PasswordUtil.isStrongPassword("password"), "只有字母的密码应该是弱密码");
        assertFalse(PasswordUtil.isStrongPassword("12345678"), "只有数字的密码应该是弱密码");
        assertFalse(PasswordUtil.isStrongPassword("pass123"), "少于8位的密码应该是弱密码");
        assertFalse(PasswordUtil.isStrongPassword(""), "空字符串应该是弱密码");
        assertFalse(PasswordUtil.isStrongPassword(null), "null应该是弱密码");
    }

    /**
     * 测试旧版密码验证功能
     * BCDE原则中的Design：根据设计文档测试向后兼容性
     */
    @Test
    public void testLegacyMatches() {
        // Given: 使用旧算法生成的密码哈希
        String password = "testPassword";
        String legacySalt = "shiwu_marketplace_salt";
        String expectedLegacyHash = org.apache.commons.codec.digest.DigestUtils.md5Hex(password + legacySalt);
        
        // When & Then: 验证旧版密码匹配
        assertTrue(PasswordUtil.legacyMatches(password, expectedLegacyHash), "旧版密码应该能正确验证");
        assertFalse(PasswordUtil.legacyMatches("wrongPassword", expectedLegacyHash), "错误的旧版密码不应该匹配");
        assertFalse(PasswordUtil.legacyMatches(null, expectedLegacyHash), "null密码不应该匹配旧版哈希");
        assertFalse(PasswordUtil.legacyMatches(password, null), "null哈希不应该匹配");
    }

    /**
     * 测试密码哈希升级功能
     * BCDE原则中的Design：根据设计文档测试密码升级逻辑
     */
    @Test
    public void testUpgradeHash() {
        // Given: 旧版密码哈希
        String password = "testPassword";
        String legacySalt = "shiwu_marketplace_salt";
        String oldHash = org.apache.commons.codec.digest.DigestUtils.md5Hex(password + legacySalt);
        
        // When: 升级密码哈希
        String upgradedHash = PasswordUtil.upgradeHash(password, oldHash);
        
        // Then: 验证升级结果
        assertNotNull(upgradedHash, "升级后的哈希不应为空");
        assertTrue(PasswordUtil.isBCryptHash(upgradedHash), "升级后应该是BCrypt格式");
        assertTrue(PasswordUtil.matches(password, upgradedHash), "升级后的哈希应该能验证原密码");
        
        // 测试已经是BCrypt格式的密码不需要升级
        String bcryptHash = PasswordUtil.encrypt(password);
        String notUpgraded = PasswordUtil.upgradeHash(password, bcryptHash);
        assertEquals(bcryptHash, notUpgraded, "BCrypt格式的密码不应该被升级");
    }
}
