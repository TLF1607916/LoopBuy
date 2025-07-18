package com.shiwu.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shiwu.admin.model.*;
import com.shiwu.admin.service.AdminService;
import com.shiwu.admin.service.impl.AdminServiceImpl;
import com.shiwu.common.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理员控制器
 */
@WebServlet("/api/admin/*")
public class AdminController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final AdminService adminService;
    private final ObjectMapper objectMapper;

    public AdminController() {
        this.adminService = new AdminServiceImpl();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    // 用于测试的构造函数，支持依赖注入
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null) {
            sendErrorResponse(resp, "404", "请求路径不存在");
            return;
        }

        switch (pathInfo) {
            case "/login":
                handleAdminLogin(req, resp);
                break;
            case "/confirm":
                handleSecondaryConfirmation(req, resp);
                break;
            default:
                sendErrorResponse(resp, "404", "请求路径不存在");
        }
    }

    /**
     * 处理管理员登录请求
     * API: POST /api/admin/login
     */
    private void handleAdminLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // 读取请求体
            StringBuilder requestBody = new StringBuilder();
            String line;
            try (BufferedReader reader = req.getReader()) {
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
            }

            if (requestBody.length() == 0) {
                sendErrorResponse(resp, "A0101", "请求体不能为空");
                return;
            }

            // 解析JSON请求
            Map<String, Object> requestMap = objectMapper.readValue(requestBody.toString(), Map.class);
            String username = (String) requestMap.get("username");
            String password = (String) requestMap.get("password");

            // 参数校验
            if (username == null || username.trim().isEmpty()) {
                sendErrorResponse(resp, "A0101", "用户名不能为空");
                return;
            }

            if (password == null || password.trim().isEmpty()) {
                sendErrorResponse(resp, "A0101", "密码不能为空");
                return;
            }

            // 获取客户端信息
            String ipAddress = getClientIpAddress(req);
            String userAgent = req.getHeader("User-Agent");

            // 调用服务进行登录
            AdminLoginResult loginResult = adminService.login(username.trim(), password, ipAddress, userAgent);

            if (loginResult.getSuccess()) {
                // 登录成功
                sendSuccessResponse(resp, loginResult.getData());
                logger.info("管理员登录成功: {}", username);
            } else {
                // 登录失败
                sendErrorResponse(resp, 
                                loginResult.getError().getCode(), 
                                loginResult.getError().getMessage(),
                                loginResult.getError().getUserTip());
                logger.warn("管理员登录失败: {}, 错误: {}", username, loginResult.getError().getMessage());
            }
        } catch (Exception e) {
            logger.error("处理管理员登录请求时发生异常: {}", e.getMessage(), e);
            sendErrorResponse(resp, "A0500", "系统错误，请稍后再试");
        }
    }

    /**
     * 处理二次确认请求
     * API: POST /api/admin/confirm
     */
    private void handleSecondaryConfirmation(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // 读取请求体
            StringBuilder requestBody = new StringBuilder();
            String line;
            try (BufferedReader reader = req.getReader()) {
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
            }

            if (requestBody.length() == 0) {
                sendErrorResponse(resp, "SC0101", "请求体不能为空");
                return;
            }

            // 解析JSON请求
            Map<String, Object> requestMap = objectMapper.readValue(requestBody.toString(), Map.class);
            String password = (String) requestMap.get("password");
            String operationCode = (String) requestMap.get("operationCode");

            // 参数校验
            if (password == null || password.trim().isEmpty()) {
                sendErrorResponse(resp, "SC0102", "密码不能为空");
                return;
            }

            if (operationCode == null || operationCode.trim().isEmpty()) {
                sendErrorResponse(resp, "SC0103", "操作代码不能为空");
                return;
            }

            // 获取管理员信息
            Long adminId = (Long) req.getAttribute("userId");
            if (adminId == null) {
                sendErrorResponse(resp, "SC0202", "管理员信息不存在，请重新登录");
                return;
            }

            // 获取客户端信息
            String ipAddress = getClientIpAddress(req);
            String userAgent = req.getHeader("User-Agent");

            // 调用服务进行二次确认
            SecondaryConfirmationResult confirmationResult = adminService.verifySecondaryConfirmation(
                    adminId, password.trim(), operationCode.trim(), ipAddress, userAgent);

            if (confirmationResult.getSuccess()) {
                // 确认成功，生成确认令牌
                String confirmationToken = generateConfirmationToken(adminId, operationCode);

                // 返回成功响应，包含确认令牌
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setContentType("application/json;charset=UTF-8");

                Result<Object> result = Result.success("二次确认成功");
                Map<String, Object> data = new HashMap<>();
                data.put("confirmationToken", confirmationToken);
                data.put("operationCode", operationCode);
                data.put("operationDescription", confirmationResult.getData());
                result.setData(data);
                resp.getWriter().write(objectMapper.writeValueAsString(result));

                logger.info("管理员二次确认成功: adminId={}, operationCode={}", adminId, operationCode);
            } else {
                // 确认失败
                sendErrorResponse(resp,
                                confirmationResult.getError().getCode(),
                                confirmationResult.getError().getMessage(),
                                confirmationResult.getError().getUserTip());
                logger.warn("管理员二次确认失败: adminId={}, operationCode={}, 错误: {}",
                          adminId, operationCode, confirmationResult.getError().getMessage());
            }
        } catch (Exception e) {
            logger.error("处理二次确认请求时发生异常: {}", e.getMessage(), e);
            sendErrorResponse(resp, "SC0500", "系统错误，请稍后再试");
        }
    }

    /**
     * 生成确认令牌
     */
    private String generateConfirmationToken(Long adminId, String operationCode) {
        // 简化实现：使用时间戳和操作码生成令牌
        // 实际应用中应该使用更安全的方式，如JWT或Redis存储
        long timestamp = System.currentTimeMillis();
        return String.format("%d_%s_%d", adminId, operationCode, timestamp);
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    /**
     * 发送成功响应
     */
    private void sendSuccessResponse(HttpServletResponse resp, Object data) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json;charset=UTF-8");
        
        Result<Object> result = Result.success(data);
        resp.getWriter().write(objectMapper.writeValueAsString(result));
    }

    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse resp, String code, String message) throws IOException {
        sendErrorResponse(resp, code, message, message);
    }

    /**
     * 发送错误响应（带用户提示）
     */
    private void sendErrorResponse(HttpServletResponse resp, String code, String message, String userTip) throws IOException {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        resp.setContentType("application/json;charset=UTF-8");
        
        Result<Object> result = Result.fail(code, message, userTip);
        resp.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
