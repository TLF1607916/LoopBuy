package com.shiwu.notification.controller;

import com.shiwu.common.result.Result;
import com.shiwu.common.util.JsonUtil;
import com.shiwu.notification.service.NotificationService;
import com.shiwu.notification.service.impl.NotificationServiceImpl;
import com.shiwu.notification.vo.NotificationVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知控制器
 * 
 * 用于Task4_2_1_2: 商品审核通过粉丝通知功能
 * 提供通知相关的HTTP API接口
 * 
 * @author LoopBuy Team
 * @version 1.0
 * @since 2024-01-15
 */
@WebServlet("/api/notification/*")
public class NotificationController extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    
    private final NotificationService notificationService;
    
    public NotificationController() {
        this.notificationService = new NotificationServiceImpl();
    }
    
    // 用于测试的构造函数
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null) {
            sendErrorResponse(resp, "404", "请求路径不存在");
            return;
        }
        
        try {
            switch (pathInfo) {
                case "/list":
                    handleGetNotificationList(req, resp);
                    break;
                case "/unread-count":
                    handleGetUnreadCount(req, resp);
                    break;
                default:
                    sendErrorResponse(resp, "404", "请求路径不存在");
                    break;
            }
        } catch (Exception e) {
            logger.error("处理GET请求失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "服务器内部错误");
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null) {
            sendErrorResponse(resp, "404", "请求路径不存在");
            return;
        }
        
        try {
            if (pathInfo.equals("/mark-read")) {
                handleMarkAsRead(req, resp);
            } else if (pathInfo.equals("/mark-all-read")) {
                handleMarkAllAsRead(req, resp);
            } else {
                sendErrorResponse(resp, "404", "请求路径不存在");
            }
        } catch (Exception e) {
            logger.error("处理PUT请求失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "服务器内部错误");
        }
    }
    
    /**
     * 处理获取通知列表请求
     * API: GET /api/notification/list
     */
    private void handleGetNotificationList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 获取当前登录用户ID（从JWT token中解析）
        Long userId = getCurrentUserIdFromToken(req);
        if (userId == null) {
            sendErrorResponse(resp, "401", "请先登录");
            return;
        }
        
        // 获取分页参数
        int page = getIntParameter(req, "page", 1);
        int size = getIntParameter(req, "size", 20);
        boolean onlyUnread = getBooleanParameter(req, "onlyUnread", false);
        
        // 参数验证
        if (page < 1) page = 1;
        if (size < 1 || size > 100) size = 20;
        
        // 调用服务获取通知列表
        Result<List<NotificationVO>> result = notificationService.getUserNotifications(userId, page, size, onlyUnread);
        
        if (result.isSuccess()) {
            sendSuccessResponse(resp, result.getData());
            logger.debug("获取通知列表成功: userId={}, page={}, size={}, count={}", 
                        userId, page, size, result.getData().size());
        } else {
            sendErrorResponse(resp, "400", result.getMessage());
        }
    }
    
    /**
     * 处理获取未读通知数量请求
     * API: GET /api/notification/unread-count
     */
    private void handleGetUnreadCount(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 获取当前登录用户ID
        Long userId = getCurrentUserIdFromToken(req);
        if (userId == null) {
            sendErrorResponse(resp, "401", "请先登录");
            return;
        }
        
        // 调用服务获取未读数量
        Result<Integer> result = notificationService.getUnreadNotificationCount(userId);
        
        if (result.isSuccess()) {
            sendSuccessResponse(resp, result.getData());
            logger.debug("获取未读通知数量成功: userId={}, count={}", userId, result.getData());
        } else {
            sendErrorResponse(resp, "400", result.getMessage());
        }
    }
    
    /**
     * 处理标记通知已读请求
     * API: PUT /api/notification/mark-read
     */
    private void handleMarkAsRead(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 获取当前登录用户ID
        Long userId = getCurrentUserIdFromToken(req);
        if (userId == null) {
            sendErrorResponse(resp, "401", "请先登录");
            return;
        }
        
        // 获取通知ID参数
        String notificationIdStr = req.getParameter("id");
        if (notificationIdStr == null || notificationIdStr.trim().isEmpty()) {
            sendErrorResponse(resp, "400", "通知ID不能为空");
            return;
        }
        
        try {
            Long notificationId = Long.parseLong(notificationIdStr);
            
            // 调用服务标记已读
            Result<Void> result = notificationService.markNotificationAsRead(notificationId, userId);
            
            if (result.isSuccess()) {
                sendSuccessResponse(resp, null);
                logger.info("标记通知已读成功: userId={}, notificationId={}", userId, notificationId);
            } else {
                sendErrorResponse(resp, "400", result.getMessage());
            }
            
        } catch (NumberFormatException e) {
            sendErrorResponse(resp, "400", "通知ID格式错误");
        }
    }
    
    /**
     * 处理标记所有通知已读请求
     * API: PUT /api/notification/mark-all-read
     */
    private void handleMarkAllAsRead(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 获取当前登录用户ID
        Long userId = getCurrentUserIdFromToken(req);
        if (userId == null) {
            sendErrorResponse(resp, "401", "请先登录");
            return;
        }
        
        // 获取可选的通知ID列表参数
        String idsParam = req.getParameter("ids");
        List<Long> notificationIds = null;
        
        if (idsParam != null && !idsParam.trim().isEmpty()) {
            try {
                notificationIds = Arrays.stream(idsParam.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                sendErrorResponse(resp, "400", "通知ID格式错误");
                return;
            }
        }
        
        // 调用服务批量标记已读
        Result<Integer> result = notificationService.batchMarkNotificationsAsRead(userId, notificationIds);
        
        if (result.isSuccess()) {
            sendSuccessResponse(resp, result.getData());
            logger.info("批量标记通知已读成功: userId={}, count={}", userId, result.getData());
        } else {
            sendErrorResponse(resp, "400", result.getMessage());
        }
    }
    
    /**
     * 从JWT Token中获取当前用户ID
     */
    private Long getCurrentUserIdFromToken(HttpServletRequest req) {
        // 这里应该实现JWT Token解析逻辑
        // 为了简化，暂时从请求头中获取
        String userIdHeader = req.getHeader("X-User-Id");
        if (userIdHeader != null) {
            try {
                return Long.parseLong(userIdHeader);
            } catch (NumberFormatException e) {
                logger.warn("用户ID格式错误: {}", userIdHeader);
            }
        }
        return null;
    }
    
    /**
     * 获取整数参数
     */
    private int getIntParameter(HttpServletRequest req, String name, int defaultValue) {
        String value = req.getParameter(name);
        if (value != null && !value.trim().isEmpty()) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                logger.warn("参数格式错误: {}={}", name, value);
            }
        }
        return defaultValue;
    }
    
    /**
     * 获取布尔参数
     */
    private boolean getBooleanParameter(HttpServletRequest req, String name, boolean defaultValue) {
        String value = req.getParameter(name);
        if (value != null && !value.trim().isEmpty()) {
            return "true".equalsIgnoreCase(value) || "1".equals(value);
        }
        return defaultValue;
    }
    
    /**
     * 发送成功响应
     */
    private void sendSuccessResponse(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        
        Result<Object> result = Result.success(data);
        String jsonResponse = JsonUtil.toJson(result);
        
        PrintWriter out = resp.getWriter();
        out.print(jsonResponse);
        out.flush();
    }
    
    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse resp, String code, String message) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        int statusCode = "401".equals(code) ? HttpServletResponse.SC_UNAUTHORIZED :
                        "404".equals(code) ? HttpServletResponse.SC_NOT_FOUND :
                        "500".equals(code) ? HttpServletResponse.SC_INTERNAL_SERVER_ERROR :
                        HttpServletResponse.SC_BAD_REQUEST;
        
        resp.setStatus(statusCode);
        
        Result<Object> result = Result.error(message);
        String jsonResponse = JsonUtil.toJson(result);
        
        PrintWriter out = resp.getWriter();
        out.print(jsonResponse);
        out.flush();
    }
}
