package com.shiwu.message.controller;

import com.shiwu.common.result.Result;
import com.shiwu.common.util.JsonUtil;
import com.shiwu.common.util.JwtUtil;
import com.shiwu.message.dto.MessageSendDTO;
import com.shiwu.message.service.MessageService;
import com.shiwu.message.service.impl.MessageServiceImpl;
import com.shiwu.message.vo.ConversationVO;
import com.shiwu.message.vo.MessageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * 消息控制器
 * 
 * 处理实时消息收发相关的HTTP请求
 * 支持基于轮询的实时消息推送
 * 
 * @author LoopBuy Team
 * @version 1.0
 */
@WebServlet("/api/message/*")
public class MessageController extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    
    private MessageService messageService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.messageService = new MessageServiceImpl();
        logger.info("MessageController初始化完成");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        logger.debug("处理GET请求: {}", pathInfo);
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                handleGetConversations(request, response);
            } else if (pathInfo.startsWith("/conversations")) {
                handleGetConversations(request, response);
            } else if (pathInfo.startsWith("/history/")) {
                handleGetMessageHistory(request, response);
            } else if (pathInfo.startsWith("/new")) {
                handleGetNewMessages(request, response);
            } else if (pathInfo.startsWith("/unread-count")) {
                handleGetUnreadCount(request, response);
            } else if (pathInfo.startsWith("/conversation/")) {
                handleGetConversationDetail(request, response);
            } else {
                sendErrorResponse(response, 404, "接口不存在");
            }
        } catch (Exception e) {
            logger.error("处理GET请求时发生异常: {}", pathInfo, e);
            sendErrorResponse(response, 500, "服务器内部错误");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        logger.debug("处理POST请求: {}", pathInfo);
        
        try {
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/send")) {
                handleSendMessage(request, response);
            } else if (pathInfo.startsWith("/conversation")) {
                handleCreateConversation(request, response);
            } else {
                sendErrorResponse(response, 404, "接口不存在");
            }
        } catch (Exception e) {
            logger.error("处理POST请求时发生异常: {}", pathInfo, e);
            sendErrorResponse(response, 500, "服务器内部错误");
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        logger.debug("处理PUT请求: {}", pathInfo);
        
        try {
            if (pathInfo != null && pathInfo.startsWith("/read/")) {
                handleMarkAsRead(request, response);
            } else if (pathInfo != null && pathInfo.startsWith("/conversation/")) {
                handleUpdateConversationStatus(request, response);
            } else {
                sendErrorResponse(response, 404, "接口不存在");
            }
        } catch (Exception e) {
            logger.error("处理PUT请求时发生异常: {}", pathInfo, e);
            sendErrorResponse(response, 500, "服务器内部错误");
        }
    }
    
    /**
     * 发送消息
     */
    private void handleSendMessage(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // 验证用户身份
        Long userId = getUserIdFromToken(request);
        if (userId == null) {
            sendErrorResponse(response, 401, "未授权访问");
            return;
        }
        
        // 解析请求体
        String requestBody = getRequestBody(request);
        if (requestBody == null || requestBody.trim().isEmpty()) {
            sendErrorResponse(response, 400, "请求体不能为空");
            return;
        }
        
        try {
            MessageSendDTO dto = JsonUtil.fromJson(requestBody, MessageSendDTO.class);
            if (dto == null) {
                sendErrorResponse(response, 400, "请求格式错误");
                return;
            }
            
            Result<MessageVO> result = messageService.sendMessage(userId, dto);
            sendJsonResponse(response, result);
            
        } catch (Exception e) {
            logger.error("发送消息时解析请求失败: userId={}", userId, e);
            sendErrorResponse(response, 400, "请求格式错误");
        }
    }
    
    /**
     * 获取会话列表
     */
    private void handleGetConversations(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // 验证用户身份
        Long userId = getUserIdFromToken(request);
        if (userId == null) {
            sendErrorResponse(response, 401, "未授权访问");
            return;
        }
        
        // 获取分页参数
        int page = getIntParameter(request, "page", 1);
        int size = getIntParameter(request, "size", 20);
        
        Result<List<ConversationVO>> result = messageService.getConversations(userId, page, size);
        sendJsonResponse(response, result);
    }
    
    /**
     * 获取消息历史
     */
    private void handleGetMessageHistory(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // 验证用户身份
        Long userId = getUserIdFromToken(request);
        if (userId == null) {
            sendErrorResponse(response, 401, "未授权访问");
            return;
        }
        
        // 提取会话ID
        String pathInfo = request.getPathInfo();
        String conversationId = extractConversationId(pathInfo, "/history/");
        if (conversationId == null) {
            sendErrorResponse(response, 400, "会话ID不能为空");
            return;
        }
        
        // 获取分页参数
        int page = getIntParameter(request, "page", 1);
        int size = getIntParameter(request, "size", 50);
        
        Result<List<MessageVO>> result = messageService.getMessageHistory(userId, conversationId, page, size);
        sendJsonResponse(response, result);
    }
    
    /**
     * 获取新消息（轮询接口）
     */
    private void handleGetNewMessages(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // 验证用户身份
        Long userId = getUserIdFromToken(request);
        if (userId == null) {
            sendErrorResponse(response, 401, "未授权访问");
            return;
        }
        
        // 获取上次消息时间
        Long lastMessageTime = getLongParameter(request, "lastMessageTime", 0L);
        
        Result<List<MessageVO>> result = messageService.getNewMessages(userId, lastMessageTime);
        sendJsonResponse(response, result);
    }
    
    /**
     * 获取未读消息数量
     */
    private void handleGetUnreadCount(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // 验证用户身份
        Long userId = getUserIdFromToken(request);
        if (userId == null) {
            sendErrorResponse(response, 401, "未授权访问");
            return;
        }
        
        Result<Integer> result = messageService.getUnreadMessageCount(userId);
        sendJsonResponse(response, result);
    }
    
    /**
     * 获取会话详情
     */
    private void handleGetConversationDetail(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // 验证用户身份
        Long userId = getUserIdFromToken(request);
        if (userId == null) {
            sendErrorResponse(response, 401, "未授权访问");
            return;
        }
        
        // 提取会话ID
        String pathInfo = request.getPathInfo();
        String conversationId = extractConversationId(pathInfo, "/conversation/");
        if (conversationId == null) {
            sendErrorResponse(response, 400, "会话ID不能为空");
            return;
        }
        
        Result<ConversationVO> result = messageService.getConversationDetail(userId, conversationId);
        sendJsonResponse(response, result);
    }
    
    /**
     * 创建会话
     */
    private void handleCreateConversation(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // 验证用户身份
        Long userId = getUserIdFromToken(request);
        if (userId == null) {
            sendErrorResponse(response, 401, "未授权访问");
            return;
        }
        
        // 获取参数
        Long otherUserId = getLongParameter(request, "otherUserId", null);
        Long productId = getLongParameter(request, "productId", null);
        
        if (otherUserId == null) {
            sendErrorResponse(response, 400, "对方用户ID不能为空");
            return;
        }
        
        Result<ConversationVO> result = messageService.getOrCreateConversation(userId, otherUserId, productId);
        sendJsonResponse(response, result);
    }
    
    /**
     * 标记消息为已读
     */
    private void handleMarkAsRead(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // 验证用户身份
        Long userId = getUserIdFromToken(request);
        if (userId == null) {
            sendErrorResponse(response, 401, "未授权访问");
            return;
        }
        
        // 提取会话ID
        String pathInfo = request.getPathInfo();
        String conversationId = extractConversationId(pathInfo, "/read/");
        if (conversationId == null) {
            sendErrorResponse(response, 400, "会话ID不能为空");
            return;
        }
        
        Result<Void> result = messageService.markMessagesAsRead(userId, conversationId);
        sendJsonResponse(response, result);
    }
    
    /**
     * 更新会话状态
     */
    private void handleUpdateConversationStatus(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // 验证用户身份
        Long userId = getUserIdFromToken(request);
        if (userId == null) {
            sendErrorResponse(response, 401, "未授权访问");
            return;
        }
        
        // 提取会话ID
        String pathInfo = request.getPathInfo();
        String conversationId = extractConversationId(pathInfo, "/conversation/");
        if (conversationId == null) {
            sendErrorResponse(response, 400, "会话ID不能为空");
            return;
        }
        
        // 获取状态参数
        String status = request.getParameter("status");
        if (status == null || status.trim().isEmpty()) {
            sendErrorResponse(response, 400, "状态不能为空");
            return;
        }
        
        Result<Void> result = messageService.updateConversationStatus(userId, conversationId, status);
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
     * 从路径中提取会话ID
     */
    private String extractConversationId(String pathInfo, String prefix) {
        if (pathInfo == null || !pathInfo.startsWith(prefix)) {
            return null;
        }
        
        String conversationId = pathInfo.substring(prefix.length());
        if (conversationId.isEmpty()) {
            return null;
        }
        
        // 移除可能的查询参数
        int queryIndex = conversationId.indexOf('?');
        if (queryIndex > 0) {
            conversationId = conversationId.substring(0, queryIndex);
        }
        
        return conversationId;
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
