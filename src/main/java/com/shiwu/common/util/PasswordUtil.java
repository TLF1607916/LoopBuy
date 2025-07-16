package com.shiwu.common.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 密码工具类，用于密码加密和验证
 */
public class PasswordUtil {
    private static final Logger logger = LoggerFactory.getLogger(PasswordUtil.class);
    private static final String SALT = "shiwu_marketplace_salt";

    /**
     * 对密码进行加密
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String encrypt(String password) {
        if (password == null) {
            return null;
        }
        
        try {
            // 使用MD5加盐加密
            return DigestUtils.md5Hex(password + SALT);
        } catch (Exception e) {
            logger.error("密码加密失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 验证密码是否匹配
     * @param inputPassword 输入的密码
     * @param encryptedPassword 已加密的密码
     * @return 是否匹配
     */
    public static boolean matches(String inputPassword, String encryptedPassword) {
        if (inputPassword == null || encryptedPassword == null) {
            return false;
        }
        
        String inputEncrypted = encrypt(inputPassword);
        return encryptedPassword.equals(inputEncrypted);
    }
}