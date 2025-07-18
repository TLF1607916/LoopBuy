package com.shiwu.admin.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shiwu.admin.model.HighRiskOperation;
import com.shiwu.admin.service.AdminService;
import com.shiwu.admin.service.impl.AdminServiceImpl;
import com.shiwu.common.result.Result;
import com.shiwu.common.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 二次确认拦截器
 * 自动识别高风险操作并要求二次确认
 */
@WebFilter(urlPatterns = "/api/admin/*")
public class SecondaryConfirmationInterceptor implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(SecondaryConfirmationInterceptor.class);

    protected AdminService adminService;
    private ObjectMapper objectMapper;
    
    // 不需要二次确认的路径
    private static final List<String> WHITELIST = Arrays.asList(
            "/api/admin/login",
            "/api/admin/confirm",
            "/api/admin/profile"
    );
    
    // 高风险操作路径映射
    private static final List<HighRiskOperationMapping> OPERATION_MAPPINGS = Arrays.asList(
            new HighRiskOperationMapping("/api/admin/users/delete", "DELETE_USER_PERMANENTLY"),
            new HighRiskOperationMapping("/api/admin/users/batch-ban", "BATCH_BAN_USERS"),
            new HighRiskOperationMapping("/api/admin/users/batch-mute", "BATCH_MUTE_USERS"),
            new HighRiskOperationMapping("/api/admin/users/reset-password", "RESET_USER_PASSWORD"),
            new HighRiskOperationMapping("/api/admin/products/delete", "DELETE_PRODUCT_PERMANENTLY"),
            new HighRiskOperationMapping("/api/admin/products/batch-remove", "BATCH_REMOVE_PRODUCTS"),
            new HighRiskOperationMapping("/api/admin/system/config", "MODIFY_SYSTEM_CONFIG"),
            new HighRiskOperationMapping("/api/admin/logs/clear", "CLEAR_AUDIT_LOGS"),
            new HighRiskOperationMapping("/api/admin/admins/create", "CREATE_ADMIN_ACCOUNT"),
            new HighRiskOperationMapping("/api/admin/admins/delete", "DELETE_ADMIN_ACCOUNT"),
            new HighRiskOperationMapping("/api/admin/admins/permissions", "MODIFY_ADMIN_PERMISSIONS"),
            new HighRiskOperationMapping("/api/admin/data/export", "EXPORT_USER_DATA"),
            new HighRiskOperationMapping("/api/admin/data/batch", "BATCH_DATA_OPERATION")
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (this.adminService == null) {
            this.adminService = new AdminServiceImpl();
        }
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        logger.info("二次确认拦截器初始化完成");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();
        
        // 只处理POST、PUT、DELETE请求
        if (!"POST".equals(method) && !"PUT".equals(method) && !"DELETE".equals(method)) {
            chain.doFilter(request, response);
            return;
        }
        
        // 检查是否在白名单中
        if (isInWhitelist(requestURI)) {
            chain.doFilter(request, response);
            return;
        }
        
        try {
            // 获取管理员信息
            Long adminId = (Long) httpRequest.getAttribute("userId");
            String adminRole = (String) httpRequest.getAttribute("userRole");
            
            if (adminId == null || adminRole == null) {
                logger.warn("二次确认拦截器: 无法获取管理员信息");
                chain.doFilter(request, response);
                return;
            }
            
            // 检查是否为高风险操作
            String operationCode = getOperationCode(requestURI, httpRequest.getMethod());
            if (operationCode == null) {
                // 不是高风险操作，直接放行
                chain.doFilter(request, response);
                return;
            }
            
            // 检查是否需要二次确认
            if (!adminService.requiresSecondaryConfirmation(operationCode, adminRole)) {
                // 不需要二次确认或权限不足，直接放行（权限检查由其他拦截器处理）
                chain.doFilter(request, response);
                return;
            }
            
            // 检查请求头中是否包含二次确认标识
            String confirmationToken = httpRequest.getHeader("X-Secondary-Confirmation");
            if (confirmationToken == null || confirmationToken.trim().isEmpty()) {
                // 需要二次确认但未提供确认令牌，返回需要确认的响应
                sendSecondaryConfirmationRequired(httpResponse, operationCode);
                return;
            }
            
            // 验证二次确认令牌（这里简化处理，实际应该验证令牌的有效性）
            // 在实际实现中，应该验证令牌是否对应当前操作
            logger.info("高风险操作已通过二次确认: adminId={}, operationCode={}", adminId, operationCode);
            
            // 记录高风险操作
            httpRequest.setAttribute("highRiskOperation", operationCode);
            
            // 继续处理请求
            chain.doFilter(request, response);
            
        } catch (Exception e) {
            logger.error("二次确认拦截器处理异常: {}", e.getMessage(), e);
            sendErrorResponse(httpResponse, "系统错误，请稍后重试");
        }
    }

    @Override
    public void destroy() {
        logger.info("二次确认拦截器销毁");
    }
    
    /**
     * 检查路径是否在白名单中
     */
    private boolean isInWhitelist(String requestURI) {
        return WHITELIST.stream().anyMatch(requestURI::startsWith);
    }
    
    /**
     * 根据请求路径和HTTP方法获取操作代码
     */
    private String getOperationCode(String requestURI, String httpMethod) {
        // 特殊处理商品删除路径（包含商品ID）
        if (requestURI.matches("/api/admin/products/\\d+") && "DELETE".equals(httpMethod)) {
            return "DELETE_PRODUCT_PERMANENTLY";
        }

        return OPERATION_MAPPINGS.stream()
                .filter(mapping -> requestURI.startsWith(mapping.getPath()))
                .map(HighRiskOperationMapping::getOperationCode)
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 发送需要二次确认的响应
     */
    private void sendSecondaryConfirmationRequired(HttpServletResponse response, String operationCode)
            throws IOException {
        response.setStatus(428); // 428 Precondition Required
        response.setContentType("application/json;charset=UTF-8");
        
        HighRiskOperation operation = HighRiskOperation.fromCode(operationCode);
        String description = operation != null ? operation.getDescription() : "高风险操作";
        
        Result<Object> result = Result.fail("SECONDARY_CONFIRMATION_REQUIRED", 
                                          "需要二次密码确认", 
                                          "此操作（" + description + "）需要输入管理员密码进行确认");
        result.setData(operationCode);
        
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
    
    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("application/json;charset=UTF-8");
        
        Result<Object> result = Result.fail("SYSTEM_ERROR", message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
    
    /**
     * 高风险操作路径映射类
     */
    private static class HighRiskOperationMapping {
        private final String path;
        private final String operationCode;
        
        public HighRiskOperationMapping(String path, String operationCode) {
            this.path = path;
            this.operationCode = operationCode;
        }
        
        public String getPath() {
            return path;
        }
        
        public String getOperationCode() {
            return operationCode;
        }
    }
}
