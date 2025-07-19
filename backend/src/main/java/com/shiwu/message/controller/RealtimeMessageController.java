package com.shiwu.message.controller;

import com.shiwu.common.result.Result;
import com.shiwu.common.util.JsonUtil;
import com.shiwu.common.util.JwtUtil;
import com.shiwu.message.dto.MessagePollDTO;
import com.shiwu.message.service.RealtimeMessageService;
import com.shiwu.message.service.impl.RealtimeMessageServiceImpl;
import com.shiwu.message.vo.MessagePollVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 实时消息控制器
 * 
 * 处理基于轮询的实时消息推送请求
 * 支持短轮询和长轮询两种模式
 * 
 * @author LoopBuy Team
 * @version 1.0
 */
@WebServlet("/api/realtime/*")
public class RealtimeMessageController extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(RealtimeMessageController.class);
    
    private RealtimeMessageService realtimeMessageService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.realtimeMessageService = new RealtimeMessageServiceImpl();
        logger.info("RealtimeMessageController初始化完成");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        logger.debug("处理实时消息GET请求: {}", pathInfo);
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                handlePollMessages(request, response);
            } else if (pathInfo.startsWith("/poll")) {
                handlePollMessages(request, response);
            } else if (pathInfo.startsWith("/long-poll")) {
                handleLongPollMessages(request, response);
            } else if (pathInfo.startsWith("/status")) {
                handleGetRealtimeStatus(request, response);
            } else if (pathInfo.startsWith("/check")) {
                handleCheckNewMessages(request, response);
            } else if (pathInfo.startsWith("/online-count")) {
                handleGetOnlineCount(request, response);
            } else {
                sendErrorResponse(response, 404, "接口不存在");
            }
        } catch (Exception e) {
            logger.error("处理实时消息GET请求时发生异常: {}", pathInfo, e);
            sendErrorResponse(response, 500, "服务器内部错误");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        logger.debug("处理实时消息POST请求: {}", pathInfo);
        
        try {
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/poll")) {
                handlePollMessagesWithBody(request, response);
            } else if (pathInfo.startsWith("/long-poll")) {
                handleLongPollMessagesWithBody(request, response);
            } else {
                sendErrorResponse(response, 404, "接口不存在");
            }
        } catch (Exception e) {
            logger.error("处理实时消息POST请求时发生异常: {}", pathInfo, e);
            sendErrorResponse(response, 500, "服务器内部错误");
        }
    }
    
    /**
     * 处理短轮询请求（GET方式）
     */
    private void handlePollMessages(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // 验证用户身份
        Long userId = getUserIdFromToken(request);
        if (userId == null) {
            sendErrorResponse(response, 401, "未授权访问");
            return;
        }
        
        // 构建轮询参数
        MessagePollDTO pollDTO = new MessagePollDTO();
        pollDTO.setLastMessageTime(getLongParameter(request, "lastMessageTime", null));
        pollDTO.setUnreadOnly(getBooleanParameter(request, "unreadOnly", false));
        pollDTO.setLimit(getIntParameter(request, "limit", 50));
        
        Result<MessagePollVO> result = realtimeMessageService.pollNewMessages(userId, pollDTO);
        sendJsonResponse(response, result);
    }
    
    /**
     * 处理短轮询请求（POST方式）
     */
    private void handlePollMessagesWithBody(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // 验证用户身份
        Long userId = getUserIdFromToken(request);
        if (userId == null) {
            sendErrorResponse(response, 401, "未授权访问");
            return;
        }
        
        // 解析请求体
        MessagePollDTO pollDTO = parseRequestBody(request, MessagePollDTO.class);
        if (pollDTO == null) {
            pollDTO = new MessagePollDTO();
        }
        
        Result<MessagePollVO> result = realtimeMessageService.pollNewMessages(userId, pollDTO);
        sendJsonResponse(response, result);
    }
    
    /**
     * 处理长轮询请求（GET方式）
     */
    private void handleLongPollMessages(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // 验证用户身份
        Long userId = getUserIdFromToken(request);
        if (userId == null) {
            sendErrorResponse(response, 401, "未授权访问");
            return;
        }
        
        // 构建轮询参数
        MessagePollDTO pollDTO = new MessagePollDTO();
        pollDTO.setLastMessageTime(getLongParameter(request, "lastMessageTime", null));
        pollDTO.setUnreadOnly(getBooleanParameter(request, "unreadOnly", false));
        pollDTO.setLimit(getIntParameter(request, "limit", 50));
        
        int timeout = getIntParameter(request, "timeout", 30);
        
        Result<MessagePollVO> result = realtimeMessageService.longPollNewMessages(userId, pollDTO, timeout);
        sendJsonResponse(response, result);
    }
    
    /**
     * 处理长轮询请求（POST方式）
     */
    private void handleLongPollMessagesWithBody(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // 验证用户身份
        Long userId = getUserIdFromToken(request);
        if (userId == null) {
            sendErrorResponse(response, 401, "未授权访问");
            return;
        }
        
        // 解析请求体
        MessagePollDTO pollDTO = parseRequestBody(request, MessagePollDTO.class);
        if (pollDTO == null) {
            pollDTO = new MessagePollDTO();
        }
        
        int timeout = getIntParameter(request, "timeout", 30);
        
        Result<MessagePollVO> result = realtimeMessageService.longPollNewMessages(userId, pollDTO, timeout);
        sendJsonResponse(response, result);
    }
    
    /**
     * 获取用户实时状态
     */
    private void handleGetRealtimeStatus(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // 验证用户身份
        Long userId = getUserIdFromToken(request);
        if (userId == null) {
            sendErrorResponse(response, 401, "未授权访问");
            return;
        }
        
        Result<MessagePollVO> result = realtimeMessageService.getUserRealtimeStatus(userId);
        sendJsonResponse(response, result);
    }
    
    /**
     * 检查是否有新消息
     */
    private void handleCheckNewMessages(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // 验证用户身份
        Long userId = getUserIdFromToken(request);
        if (userId == null) {
            sendErrorResponse(response, 401, "未授权访问");
            return;
        }
        
        Long lastCheckTime = getLongParameter(request, "lastCheckTime", null);
        
        Result<Boolean> result = realtimeMessageService.hasNewMessages(userId, lastCheckTime);
        sendJsonResponse(response, result);
    }
    
    /**
     * 获取在线用户数量
     */
    private void handleGetOnlineCount(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        int onlineCount = realtimeMessageService.getOnlineUserCount();
        Result<Integer> result = Result.success(onlineCount);
        sendJsonResponse(response, result);
    }
    
    /**
     * 从JWT Token中获取用户ID
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        
        String token = authHeader.substring(7);
        try {
            return JwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            logger.warn("解析JWT Token失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 解析请求体
     */
    private <T> T parseRequestBody(HttpServletRequest request, Class<T> clazz) {
        try {
            String requestBody = getRequestBody(request);
            if (requestBody == null || requestBody.trim().isEmpty()) {
                return null;
            }
            return JsonUtil.fromJson(requestBody, clazz);
        } catch (Exception e) {
            logger.warn("解析请求体失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 获取请求体内容
     */
    private String getRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
    
    /**
     * 获取整数参数
     */
    private int getIntParameter(HttpServletRequest request, String name, int defaultValue) {
        String value = request.getParameter(name);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * 获取长整数参数
     */
    private Long getLongParameter(HttpServletRequest request, String name, Long defaultValue) {
        String value = request.getParameter(name);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * 获取布尔参数
     */
    private boolean getBooleanParameter(HttpServletRequest request, String name, boolean defaultValue) {
        String value = request.getParameter(name);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
    
    /**
     * 发送JSON响应
     */
    private void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        try (PrintWriter writer = response.getWriter()) {
            String json = JsonUtil.toJson(data);
            writer.write(json);
            writer.flush();
        }
    }
    
    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        Result<Object> errorResult = Result.error(message);
        sendJsonResponse(response, errorResult);
    }
}
