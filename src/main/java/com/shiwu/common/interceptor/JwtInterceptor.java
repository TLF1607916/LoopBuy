package com.shiwu.common.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
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
 * JWT拦截器，用于验证请求中的JWT令牌
 * 以Filter形式实现，拦截所有API请求
 */
@WebFilter(urlPatterns = "/api/*")
public class JwtInterceptor implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(JwtInterceptor.class);
    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    // 不需要验证Token的路径
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/api/user/login",
            "/api/user/register"
    );
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("JWT拦截器初始化");
    }
    
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) 
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        
        String requestURI = request.getRequestURI();
        
        // 白名单路径不需要验证Token
        if (isWhiteListPath(requestURI)) {
            chain.doFilter(request, response);
            return;
        }
        
        // 获取Authorization头中的Token
        String authHeader = request.getHeader(AUTH_HEADER);
        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
            logger.warn("请求 {} 未提供有效的Authorization头", requestURI);
            sendUnauthorizedResponse(response, "未提供认证Token");
            return;
        }
        
        // 提取Token
        String token = authHeader.substring(TOKEN_PREFIX.length());
        
        // 验证Token
        if (!JwtUtil.validateToken(token)) {
            logger.warn("请求 {} 提供的Token无效", requestURI);
            sendUnauthorizedResponse(response, "无效的Token");
            return;
        }
        
        // 从Token中获取用户ID，并将其设置到请求属性中
        Long userId = JwtUtil.getUserIdFromToken(token);
        request.setAttribute("userId", userId);
        
        logger.info("用户 {} 的Token验证通过", userId);
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        logger.info("JWT拦截器销毁");
    }
    
    /**
     * 发送未授权响应
     * 
     * @param response 响应对象
     * @param message 错误信息
     */
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        Result<?> result = Result.fail("UNAUTHORIZED", message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
    
    /**
     * 判断请求路径是否在白名单中
     * 
     * @param path 请求路径
     * @return 如果在白名单中则返回true，否则返回false
     */
    private boolean isWhiteListPath(String path) {
        return WHITE_LIST.stream().anyMatch(path::startsWith);
    }
} 