package com.shiwu.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shiwu.admin.model.AdminUserManageDTO;
import com.shiwu.admin.model.AdminUserQueryDTO;
import com.shiwu.admin.service.AdminService;
import com.shiwu.admin.service.impl.AdminServiceImpl;
import com.shiwu.common.result.Result;
import com.shiwu.common.util.JwtUtil;
import com.shiwu.user.service.AdminUserService;
import com.shiwu.user.service.impl.AdminUserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 管理员用户管理控制器
 */
@WebServlet("/api/admin/users/*")
public class AdminUserController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);
    
    private final AdminUserService adminUserService;
    private final AdminService adminService;
    private final ObjectMapper objectMapper;

    public AdminUserController() {
        this.adminUserService = new AdminUserServiceImpl();
        this.adminService = new AdminServiceImpl();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    // 用于测试的构造函数
    public AdminUserController(AdminUserService adminUserService, AdminService adminService) {
        this.adminUserService = adminUserService;
        this.adminService = adminService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        // 验证管理员权限
        Long adminId = validateAdminPermission(req, resp, "ADMIN");
        if (adminId == null) {
            return;
        }
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // 查询用户列表
            handleGetUsers(req, resp, adminId);
        } else if (pathInfo.startsWith("/") && pathInfo.length() > 1) {
            try {
                // 获取用户详情
                Long userId = Long.parseLong(pathInfo.substring(1));
                handleGetUserDetail(req, resp, adminId, userId);
            } catch (NumberFormatException e) {
                sendErrorResponse(resp, "400", "无效的用户ID格式");
            }
        } else {
            sendErrorResponse(resp, "404", "请求路径不存在");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        // 验证管理员权限
        Long adminId = validateAdminPermission(req, resp, "ADMIN");
        if (adminId == null) {
            return;
        }
        
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                String[] segments = pathInfo.substring(1).split("/");
                Long userId = Long.parseLong(segments[0]);
                
                if (segments.length > 1) {
                    switch (segments[1]) {
                        case "ban":
                            // 封禁用户
                            handleBanUser(req, resp, adminId, userId);
                            break;
                        case "mute":
                            // 禁言用户
                            handleMuteUser(req, resp, adminId, userId);
                            break;
                        case "unban":
                            // 解封用户
                            handleUnbanUser(req, resp, adminId, userId);
                            break;
                        case "unmute":
                            // 解除禁言
                            handleUnmuteUser(req, resp, adminId, userId);
                            break;
                        default:
                            sendErrorResponse(resp, "404", "请求路径不存在");
                    }
                } else {
                    sendErrorResponse(resp, "404", "请求路径不存在");
                }
            } catch (NumberFormatException e) {
                sendErrorResponse(resp, "400", "无效的用户ID格式");
            }
        } else {
            sendErrorResponse(resp, "404", "请求路径不存在");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        // 验证管理员权限
        Long adminId = validateAdminPermission(req, resp, "ADMIN");
        if (adminId == null) {
            return;
        }
        
        if (pathInfo != null) {
            switch (pathInfo) {
                case "/batch-ban":
                    // 批量封禁用户
                    handleBatchBanUsers(req, resp, adminId);
                    break;
                case "/batch-mute":
                    // 批量禁言用户
                    handleBatchMuteUsers(req, resp, adminId);
                    break;
                default:
                    sendErrorResponse(resp, "404", "请求路径不存在");
            }
        } else {
            sendErrorResponse(resp, "404", "请求路径不存在");
        }
    }

    /**
     * 验证管理员权限
     */
    private Long validateAdminPermission(HttpServletRequest req, HttpServletResponse resp, String requiredRole) 
            throws IOException {
        String token = req.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        if (!JwtUtil.validateToken(token)) {
            sendErrorResponse(resp, "401", "未授权访问");
            return null;
        }
        
        Long adminId = JwtUtil.getUserIdFromToken(token);
        if (adminId == null) {
            sendErrorResponse(resp, "401", "无效的令牌");
            return null;
        }
        
        if (!adminService.hasPermission(adminId, requiredRole)) {
            sendErrorResponse(resp, "403", "权限不足");
            return null;
        }
        
        return adminId;
    }

    /**
     * 处理查询用户列表请求
     */
    private void handleGetUsers(HttpServletRequest req, HttpServletResponse resp, Long adminId) 
            throws IOException {
        try {
            // 解析查询参数
            AdminUserQueryDTO queryDTO = parseQueryParams(req);
            
            // 查询用户列表
            Map<String, Object> result = adminUserService.findUsers(queryDTO);
            
            sendSuccessResponse(resp, result);
        } catch (Exception e) {
            logger.error("查询用户列表失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "系统错误，请稍后再试");
        }
    }

    /**
     * 处理获取用户详情请求
     */
    private void handleGetUserDetail(HttpServletRequest req, HttpServletResponse resp, Long adminId, Long userId) 
            throws IOException {
        try {
            Map<String, Object> result = adminUserService.getUserDetail(userId, adminId);
            
            if (result != null) {
                sendSuccessResponse(resp, result);
            } else {
                sendErrorResponse(resp, "404", "用户不存在");
            }
        } catch (Exception e) {
            logger.error("获取用户详情失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "系统错误，请稍后再试");
        }
    }

    /**
     * 处理封禁用户请求
     */
    private void handleBanUser(HttpServletRequest req, HttpServletResponse resp, Long adminId, Long userId) 
            throws IOException {
        try {
            // 解析请求体
            AdminUserManageDTO manageDTO = parseRequestBody(req, AdminUserManageDTO.class);
            
            boolean success = adminUserService.banUser(userId, adminId, manageDTO.getReason());
            
            if (success) {
                sendSuccessResponse(resp, null, "用户封禁成功");
                logger.info("管理员 {} 封禁用户 {}", adminId, userId);
            } else {
                sendErrorResponse(resp, "400", "封禁失败，请检查用户状态");
            }
        } catch (Exception e) {
            logger.error("封禁用户失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "系统错误，请稍后再试");
        }
    }

    /**
     * 处理禁言用户请求
     */
    private void handleMuteUser(HttpServletRequest req, HttpServletResponse resp, Long adminId, Long userId) 
            throws IOException {
        try {
            // 解析请求体
            AdminUserManageDTO manageDTO = parseRequestBody(req, AdminUserManageDTO.class);
            
            boolean success = adminUserService.muteUser(userId, adminId, manageDTO.getReason());
            
            if (success) {
                sendSuccessResponse(resp, null, "用户禁言成功");
                logger.info("管理员 {} 禁言用户 {}", adminId, userId);
            } else {
                sendErrorResponse(resp, "400", "禁言失败，请检查用户状态");
            }
        } catch (Exception e) {
            logger.error("禁言用户失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "系统错误，请稍后再试");
        }
    }

    /**
     * 处理解封用户请求
     */
    private void handleUnbanUser(HttpServletRequest req, HttpServletResponse resp, Long adminId, Long userId) 
            throws IOException {
        try {
            boolean success = adminUserService.unbanUser(userId, adminId);
            
            if (success) {
                sendSuccessResponse(resp, null, "用户解封成功");
                logger.info("管理员 {} 解封用户 {}", adminId, userId);
            } else {
                sendErrorResponse(resp, "400", "解封失败，请检查用户状态");
            }
        } catch (Exception e) {
            logger.error("解封用户失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "系统错误，请稍后再试");
        }
    }

    /**
     * 处理解除禁言请求
     */
    private void handleUnmuteUser(HttpServletRequest req, HttpServletResponse resp, Long adminId, Long userId) 
            throws IOException {
        try {
            boolean success = adminUserService.unmuteUser(userId, adminId);
            
            if (success) {
                sendSuccessResponse(resp, null, "用户解除禁言成功");
                logger.info("管理员 {} 解除用户 {} 禁言", adminId, userId);
            } else {
                sendErrorResponse(resp, "400", "解除禁言失败，请检查用户状态");
            }
        } catch (Exception e) {
            logger.error("解除禁言失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "系统错误，请稍后再试");
        }
    }

    /**
     * 处理批量封禁用户请求
     */
    private void handleBatchBanUsers(HttpServletRequest req, HttpServletResponse resp, Long adminId) 
            throws IOException {
        try {
            // 解析请求体
            AdminUserManageDTO manageDTO = parseRequestBody(req, AdminUserManageDTO.class);
            
            if (manageDTO.getUserIds() == null || manageDTO.getUserIds().isEmpty()) {
                sendErrorResponse(resp, "400", "用户ID列表不能为空");
                return;
            }
            
            Map<String, Object> result = adminUserService.batchBanUsers(manageDTO.getUserIds(), adminId, manageDTO.getReason());
            
            sendSuccessResponse(resp, result, "批量封禁操作完成");
            logger.info("管理员 {} 批量封禁用户: {}", adminId, manageDTO.getUserIds());
        } catch (Exception e) {
            logger.error("批量封禁用户失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "系统错误，请稍后再试");
        }
    }

    /**
     * 处理批量禁言用户请求
     */
    private void handleBatchMuteUsers(HttpServletRequest req, HttpServletResponse resp, Long adminId) 
            throws IOException {
        try {
            // 解析请求体
            AdminUserManageDTO manageDTO = parseRequestBody(req, AdminUserManageDTO.class);
            
            if (manageDTO.getUserIds() == null || manageDTO.getUserIds().isEmpty()) {
                sendErrorResponse(resp, "400", "用户ID列表不能为空");
                return;
            }
            
            Map<String, Object> result = adminUserService.batchMuteUsers(manageDTO.getUserIds(), adminId, manageDTO.getReason());
            
            sendSuccessResponse(resp, result, "批量禁言操作完成");
            logger.info("管理员 {} 批量禁言用户: {}", adminId, manageDTO.getUserIds());
        } catch (Exception e) {
            logger.error("批量禁言用户失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "系统错误，请稍后再试");
        }
    }

    /**
     * 解析查询参数
     */
    private AdminUserQueryDTO parseQueryParams(HttpServletRequest req) {
        AdminUserQueryDTO queryDTO = new AdminUserQueryDTO();
        
        String keyword = req.getParameter("keyword");
        String status = req.getParameter("status");
        String pageNum = req.getParameter("pageNum");
        String pageSize = req.getParameter("pageSize");
        String sortBy = req.getParameter("sortBy");
        String sortDirection = req.getParameter("sortDirection");
        
        queryDTO.setKeyword(keyword);
        
        if (status != null && !status.trim().isEmpty()) {
            try {
                queryDTO.setStatus(Integer.parseInt(status));
            } catch (NumberFormatException e) {
                // 忽略无效的状态参数
            }
        }
        
        queryDTO.setPageNum(pageNum != null ? Integer.parseInt(pageNum) : 1);
        queryDTO.setPageSize(pageSize != null ? Integer.parseInt(pageSize) : 20);
        queryDTO.setSortBy(sortBy != null ? sortBy : "create_time");
        queryDTO.setSortDirection(sortDirection != null ? sortDirection : "DESC");
        
        return queryDTO;
    }

    /**
     * 解析请求体
     */
    private <T> T parseRequestBody(HttpServletRequest req, Class<T> clazz) throws IOException {
        return objectMapper.readValue(req.getInputStream(), clazz);
    }

    /**
     * 发送成功响应
     */
    private void sendSuccessResponse(HttpServletResponse resp, Object data) throws IOException {
        sendSuccessResponse(resp, data, "操作成功");
    }

    /**
     * 发送成功响应
     */
    private void sendSuccessResponse(HttpServletResponse resp, Object data, String message) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Result<Object> result = Result.success(data);
        result.setMessage(message);
        resp.getWriter().write(objectMapper.writeValueAsString(result));
    }

    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse resp, String code, String message) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Result<Object> result = Result.fail(code, message);
        resp.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
