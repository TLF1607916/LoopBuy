package com.shiwu.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * 请求工具类
 */
public class RequestUtil {
    private static final Logger logger = LoggerFactory.getLogger(RequestUtil.class);

    private RequestUtil() {
        // 工具类私有构造函数
    }

    /**
     * 从请求中获取当前用户ID
     * 
     * @param request HTTP请求对象
     * @return 当前用户ID，如果未找到则返回null
     */
    public static Long getCurrentUserId(HttpServletRequest request) {
        if (request == null) {
            logger.warn("获取当前用户ID失败: 请求对象为空");
            return null;
        }
        
        Object userId = request.getAttribute("userId");
        if (userId == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("获取当前用户ID失败: 请求中未找到userId属性");
            }
            return null;
        }
        
        try {
            return (Long) userId;
        } catch (ClassCastException e) {
            logger.error("获取当前用户ID失败: 类型转换异常", e);
            return null;
        }
    }
    
    /**
     * 检查当前用户是否有权限访问指定用户的资源
     * 
     * @param request HTTP请求对象
     * @param resourceOwnerId 资源所有者ID
     * @return 如果当前用户有权限则返回true，否则返回false
     */
    public static boolean checkPermission(HttpServletRequest request, Long resourceOwnerId) {
        Long currentUserId = getCurrentUserId(request);
        
        if (currentUserId == null || resourceOwnerId == null) {
            return false;
        }
        
        // 当前实现仅允许资源所有者访问自己的资源
        // 更复杂的权限控制可以根据业务需求扩展
        return currentUserId.equals(resourceOwnerId);
    }
} 