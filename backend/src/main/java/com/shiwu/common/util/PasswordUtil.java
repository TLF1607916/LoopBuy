package com.shiwu.common.util;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * 密码工具类，用于密码加密和验证
 */
public class PasswordUtil {
    private static final Logger logger = LoggerFactory.getLogger(PasswordUtil.class);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    private static final int BCRYPT_ROUNDS = 12; // 计算强度，数值越高安全性越高但性能越低

    /**
     * 对密码进行BCrypt加盐哈希处理
     * 
     * @param password 原始密码
     * @return 加密后的密码（包含盐值）
     */
    public static String encrypt(String password) {
        if (password == null) {
            return null;
        }
        
        try {
            // 使用BCrypt加盐哈希，生成的哈希值会自动包含盐值
            return BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_ROUNDS));
        } catch (Exception e) {
            logger.error("密码加密失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 验证密码是否匹配
     * 
     * @param inputPassword 输入的密码（明文）
     * @param hashedPassword 已哈希的密码（包含盐值）
     * @return 是否匹配
     */
    public static boolean matches(String inputPassword, String hashedPassword) {
        if (inputPassword == null || hashedPassword == null) {
            return false;
        }
        
        try {
            // 使用BCrypt验证密码，它会自动从哈希值中提取盐值
            return BCrypt.checkpw(inputPassword, hashedPassword);
        } catch (Exception e) {
            // 如果密码格式不是BCrypt格式（例如旧版密码），则会抛出异常
            logger.warn("密码格式不正确或验证过程出错: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 判断密码是否为BCrypt格式
     * 
     * @param password 密码哈希
     * @return 如果是BCrypt格式则返回true
     */
    public static boolean isBCryptHash(String password) {
        return password != null && password.startsWith("$2a$");
    }
    
    /**
     * 检查密码强度是否符合要求
     * 密码必须至少包含8个字符，包括至少一个字母和一个数字
     * 
     * @param password 密码
     * @return 密码是否符合强度要求
     */
    public static boolean isStrongPassword(String password) {
        if (password == null) {
            return false;
        }
        
        return PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * 升级密码哈希算法（从旧格式升级到BCrypt格式）
     * 
     * @param password 明文密码
     * @param oldHash 旧格式的密码哈希
     * @return BCrypt格式的新密码哈希，如果旧密码不匹配则返回null
     */
    public static String upgradeHash(String password, String oldHash) {
        // 如果已经是BCrypt格式，则无需升级
        if (isBCryptHash(oldHash)) {
            return oldHash;
        }
        
        // 暂时使用旧算法验证密码（为了向后兼容）
        if (legacyMatches(password, oldHash)) {
            // 验证成功后，使用新算法重新加密
            return encrypt(password);
        }
        
        return null;
    }
    
    /**
     * 使用旧算法验证密码（MD5加盐）
     * 为了向后兼容旧密码
     * 
     * @param inputPassword 输入的密码
     * @param oldHash 旧格式的密码哈希
     * @return 是否匹配
     */
    public static boolean legacyMatches(String inputPassword, String oldHash) {
        if (inputPassword == null || oldHash == null) {
            return false;
        }
        
        try {
            // 旧版MD5加盐算法
            String legacySalt = "shiwu_marketplace_salt";
            String inputHash = org.apache.commons.codec.digest.DigestUtils.md5Hex(inputPassword + legacySalt);
            return oldHash.equals(inputHash);
        } catch (Exception e) {
            logger.error("旧版密码验证失败: {}", e.getMessage(), e);
            return false;
        }
    }
}