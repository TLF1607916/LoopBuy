package com.shiwu.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shiwu.admin.dto.AuditLogQueryDTO;
import com.shiwu.admin.enums.AuditActionEnum;
import com.shiwu.admin.enums.AuditTargetTypeEnum;
import com.shiwu.admin.service.AdminService;
import com.shiwu.admin.service.AuditLogService;
import com.shiwu.admin.service.impl.AdminServiceImpl;
import com.shiwu.admin.service.impl.AuditLogServiceImpl;
import com.shiwu.admin.vo.AuditLogVO;
import com.shiwu.common.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * 审计日志控制器
 * 实现NFR-SEC-03要求的审计日志查看功能
 */
@WebServlet("/api/admin/audit-logs/*")
public class AuditLogController extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditLogController.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AuditLogService auditLogService;
    private final AdminService adminService;
    private final ObjectMapper objectMapper;

    public AuditLogController() {
        this.auditLogService = new AuditLogServiceImpl();
        this.adminService = new AdminServiceImpl();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    // 用于测试的构造函数
    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
        this.adminService = new AdminServiceImpl();
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
        
        // 记录查看审计日志的操作
        String ipAddress = getClientIpAddress(req);
        String userAgent = req.getHeader("User-Agent");
        auditLogService.logAction(adminId, AuditActionEnum.AUDIT_LOG_VIEW,
                                 AuditTargetTypeEnum.AUDIT_LOG, null, "查看审计日志",
                                 ipAddress, userAgent, true);
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // 查询审计日志列表
            handleGetAuditLogs(req, resp, adminId);
        } else if (pathInfo.equals("/actions")) {
            // 获取可用的操作类型
            handleGetAvailableActions(req, resp);
        } else if (pathInfo.equals("/target-types")) {
            // 获取可用的目标类型
            handleGetAvailableTargetTypes(req, resp);
        } else if (pathInfo.equals("/stats")) {
            // 获取统计数据
            handleGetStats(req, resp);
        } else if (pathInfo.equals("/trend")) {
            // 获取趋势数据
            handleGetTrend(req, resp);
        } else if (pathInfo.startsWith("/") && pathInfo.length() > 1) {
            try {
                // 获取审计日志详情
                Long logId = Long.parseLong(pathInfo.substring(1));
                handleGetAuditLogDetail(req, resp, logId);
            } catch (NumberFormatException e) {
                sendErrorResponse(resp, "400", "无效的日志ID格式");
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
        
        if (pathInfo != null && pathInfo.equals("/export")) {
            // 导出审计日志
            handleExportAuditLogs(req, resp, adminId);
        } else {
            sendErrorResponse(resp, "404", "请求路径不存在");
        }
    }
    
    /**
     * 处理查询审计日志列表请求
     */
    private void handleGetAuditLogs(HttpServletRequest req, HttpServletResponse resp, Long adminId) 
            throws IOException {
        try {
            AuditLogQueryDTO queryDTO = buildQueryDTO(req);
            Map<String, Object> result = auditLogService.getAuditLogs(queryDTO);
            sendSuccessResponse(resp, result);
            
            logger.info("管理员 {} 查询审计日志成功", adminId);
        } catch (Exception e) {
            logger.error("查询审计日志失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "查询审计日志失败");
        }
    }
    
    /**
     * 处理获取审计日志详情请求
     */
    private void handleGetAuditLogDetail(HttpServletRequest req, HttpServletResponse resp, Long logId) 
            throws IOException {
        try {
            AuditLogVO auditLogVO = auditLogService.getAuditLogDetail(logId);
            if (auditLogVO != null) {
                sendSuccessResponse(resp, auditLogVO);
                logger.info("获取审计日志详情成功: ID={}", logId);
            } else {
                sendErrorResponse(resp, "404", "审计日志不存在");
            }
        } catch (Exception e) {
            logger.error("获取审计日志详情失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "获取审计日志详情失败");
        }
    }
    
    /**
     * 处理获取可用操作类型请求
     */
    private void handleGetAvailableActions(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        try {
            sendSuccessResponse(resp, auditLogService.getAvailableActions());
        } catch (Exception e) {
            logger.error("获取可用操作类型失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "获取可用操作类型失败");
        }
    }
    
    /**
     * 处理获取可用目标类型请求
     */
    private void handleGetAvailableTargetTypes(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        try {
            sendSuccessResponse(resp, auditLogService.getAvailableTargetTypes());
        } catch (Exception e) {
            logger.error("获取可用目标类型失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "获取可用目标类型失败");
        }
    }
    
    /**
     * 处理获取统计数据请求
     */
    private void handleGetStats(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String daysParam = req.getParameter("days");
            int days = 7; // 默认7天
            if (daysParam != null && !daysParam.trim().isEmpty()) {
                try {
                    days = Integer.parseInt(daysParam);
                    if (days <= 0 || days > 365) {
                        days = 7;
                    }
                } catch (NumberFormatException e) {
                    days = 7;
                }
            }
            
            Map<String, Object> stats = auditLogService.getOperationStats(days);
            sendSuccessResponse(resp, stats);
        } catch (Exception e) {
            logger.error("获取统计数据失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "获取统计数据失败");
        }
    }
    
    /**
     * 处理获取趋势数据请求
     */
    private void handleGetTrend(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String daysParam = req.getParameter("days");
            int days = 7; // 默认7天
            if (daysParam != null && !daysParam.trim().isEmpty()) {
                try {
                    days = Integer.parseInt(daysParam);
                    if (days <= 0 || days > 365) {
                        days = 7;
                    }
                } catch (NumberFormatException e) {
                    days = 7;
                }
            }
            
            sendSuccessResponse(resp, auditLogService.getActivityTrend(days));
        } catch (Exception e) {
            logger.error("获取趋势数据失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "获取趋势数据失败");
        }
    }
    
    /**
     * 处理导出审计日志请求
     */
    private void handleExportAuditLogs(HttpServletRequest req, HttpServletResponse resp, Long adminId) 
            throws IOException {
        try {
            AuditLogQueryDTO queryDTO = parseRequestBody(req, AuditLogQueryDTO.class);
            if (queryDTO == null) {
                queryDTO = new AuditLogQueryDTO();
            }
            
            // 记录导出操作
            String ipAddress = getClientIpAddress(req);
            String userAgent = req.getHeader("User-Agent");
            auditLogService.logAction(adminId, AuditActionEnum.AUDIT_LOG_EXPORT,
                                     AuditTargetTypeEnum.AUDIT_LOG, null, "导出审计日志",
                                     ipAddress, userAgent, true);
            
            sendSuccessResponse(resp, auditLogService.exportAuditLogs(queryDTO));
            logger.info("管理员 {} 导出审计日志成功", adminId);
        } catch (Exception e) {
            logger.error("导出审计日志失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "导出审计日志失败");
        }
    }
    
    /**
     * 构建查询DTO
     */
    private AuditLogQueryDTO buildQueryDTO(HttpServletRequest req) {
        AuditLogQueryDTO queryDTO = new AuditLogQueryDTO();
        
        // 管理员ID
        String adminIdParam = req.getParameter("adminId");
        if (adminIdParam != null && !adminIdParam.trim().isEmpty()) {
            try {
                queryDTO.setAdminId(Long.parseLong(adminIdParam));
            } catch (NumberFormatException e) {
                // 忽略无效参数
            }
        }
        
        // 操作类型
        String action = req.getParameter("action");
        if (action != null && !action.trim().isEmpty()) {
            queryDTO.setAction(action.trim());
        }
        
        // 目标类型
        String targetType = req.getParameter("targetType");
        if (targetType != null && !targetType.trim().isEmpty()) {
            queryDTO.setTargetType(targetType.trim());
        }
        
        // 目标ID
        String targetIdParam = req.getParameter("targetId");
        if (targetIdParam != null && !targetIdParam.trim().isEmpty()) {
            try {
                queryDTO.setTargetId(Long.parseLong(targetIdParam));
            } catch (NumberFormatException e) {
                // 忽略无效参数
            }
        }
        
        // IP地址
        String ipAddress = req.getParameter("ipAddress");
        if (ipAddress != null && !ipAddress.trim().isEmpty()) {
            queryDTO.setIpAddress(ipAddress.trim());
        }
        
        // 操作结果
        String resultParam = req.getParameter("result");
        if (resultParam != null && !resultParam.trim().isEmpty()) {
            try {
                int result = Integer.parseInt(resultParam);
                if (result == 0 || result == 1) {
                    queryDTO.setResult(result);
                }
            } catch (NumberFormatException e) {
                // 忽略无效参数
            }
        }
        
        // 时间范围
        String startTimeParam = req.getParameter("startTime");
        if (startTimeParam != null && !startTimeParam.trim().isEmpty()) {
            try {
                queryDTO.setStartTime(LocalDateTime.parse(startTimeParam, DATE_TIME_FORMATTER));
            } catch (DateTimeParseException e) {
                // 忽略无效参数
            }
        }
        
        String endTimeParam = req.getParameter("endTime");
        if (endTimeParam != null && !endTimeParam.trim().isEmpty()) {
            try {
                queryDTO.setEndTime(LocalDateTime.parse(endTimeParam, DATE_TIME_FORMATTER));
            } catch (DateTimeParseException e) {
                // 忽略无效参数
            }
        }
        
        // 关键词
        String keyword = req.getParameter("keyword");
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryDTO.setKeyword(keyword.trim());
        }
        
        // 分页参数
        String pageParam = req.getParameter("page");
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                queryDTO.setPage(Integer.parseInt(pageParam));
            } catch (NumberFormatException e) {
                // 使用默认值
            }
        }
        
        String pageSizeParam = req.getParameter("pageSize");
        if (pageSizeParam != null && !pageSizeParam.trim().isEmpty()) {
            try {
                queryDTO.setPageSize(Integer.parseInt(pageSizeParam));
            } catch (NumberFormatException e) {
                // 使用默认值
            }
        }
        
        // 排序参数
        String sortBy = req.getParameter("sortBy");
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            queryDTO.setSortBy(sortBy.trim());
        }
        
        String sortOrder = req.getParameter("sortOrder");
        if (sortOrder != null && !sortOrder.trim().isEmpty()) {
            queryDTO.setSortOrder(sortOrder.trim());
        }
        
        return queryDTO;
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
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", message);
        result.put("data", data);
        resp.getWriter().write(objectMapper.writeValueAsString(result));
    }

    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse resp, String code, String message) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("code", code);
        result.put("message", message);
        resp.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
