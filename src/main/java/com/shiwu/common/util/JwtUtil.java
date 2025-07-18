package com.shiwu.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类，用于生成和验证JWT令牌
 */
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    
    // 密钥，在实际生产环境中应该从配置文件或环境变量中获取
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    
    // 令牌有效期（毫秒）
    private static final long TOKEN_VALIDITY = 24 * 60 * 60 * 1000; // 24小时
    
    private JwtUtil() {
        // 工具类私有构造函数
    }
    
    /**
     * 生成JWT令牌
     *
     * @param userId 用户ID
     * @param username 用户名
     * @return 生成的JWT令牌
     */
    public static String generateToken(Long userId, String username) {
        return generateToken(userId, username, null);
    }

    /**
     * 生成JWT令牌（支持角色信息）
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param role 角色（可为null）
     * @return 生成的JWT令牌
     */
    public static String generateToken(Long userId, String username, String role) {
        if (userId == null || username == null) {
            logger.error("生成JWT令牌失败: 用户ID或用户名为空");
            return null;
        }

        try {
            Date now = new Date();
            Date expiration = new Date(now.getTime() + TOKEN_VALIDITY);

            // 设置JWT声明
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", userId);
            claims.put("username", username);
            if (role != null) {
                claims.put("role", role);
            }

            // 构建JWT
            JwtBuilder builder = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(now)
                    .setExpiration(expiration)
                    .signWith(SECRET_KEY);

            logger.info("为用户 {} 生成JWT令牌成功", username);
            return builder.compact();
        } catch (Exception e) {
            logger.error("生成JWT令牌时发生异常: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 从JWT令牌中获取用户ID
     * 
     * @param token JWT令牌
     * @return 用户ID，如果令牌无效则返回null
     */
    public static Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? Long.valueOf(claims.get("userId").toString()) : null;
    }
    
    /**
     * 从JWT令牌中获取用户名
     *
     * @param token JWT令牌
     * @return 用户名，如果令牌无效则返回null
     */
    public static String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.get("username").toString() : null;
    }

    /**
     * 从JWT令牌中获取角色
     *
     * @param token JWT令牌
     * @return 角色，如果令牌无效或没有角色信息则返回null
     */
    public static String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims != null && claims.get("role") != null) {
            return claims.get("role").toString();
        }
        return null;
    }
    
    /**
     * 验证JWT令牌是否有效
     * 
     * @param token JWT令牌
     * @return 如果令牌有效则返回true，否则返回false
     */
    public static boolean validateToken(String token) {
        return getClaimsFromToken(token) != null;
    }
    
    /**
     * 从JWT令牌中获取声明
     * 
     * @param token JWT令牌
     * @return 声明对象，如果令牌无效则返回null
     */
    private static Claims getClaimsFromToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("解析JWT令牌时发生异常: {}", e.getMessage());
            return null;
        }
    }
} 