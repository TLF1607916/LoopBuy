package com.shiwu.admin.interceptor;

import com.shiwu.admin.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 二次确认拦截器单元测试
 * 使用Mockito模拟HTTP请求和响应
 */
@DisplayName("二次确认拦截器测试")
public class SecondaryConfirmationInterceptorTest {
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private FilterChain filterChain;
    
    @Mock
    private FilterConfig filterConfig;
    
    @Mock
    private AdminService adminService;
    
    private SecondaryConfirmationInterceptor interceptor;
    private StringWriter responseWriter;
    
    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        interceptor = new TestableSecondaryConfirmationInterceptor(adminService);
        
        // 设置响应Writer
        responseWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(responseWriter);
        when(response.getWriter()).thenReturn(printWriter);
        
        // 初始化拦截器
        interceptor.init(filterConfig);
    }
    
    /**
     * 测试GET请求直接放行
     * BCDE原则中的Boundary：测试边界条件
     */
    @Test
    @DisplayName("GET请求直接放行")
    public void testGetRequestPassThrough() throws Exception {
        // Given: GET请求
        when(request.getRequestURI()).thenReturn("/api/admin/users");
        when(request.getMethod()).thenReturn("GET");
        
        // When: 执行过滤
        interceptor.doFilter(request, response, filterChain);
        
        // Then: 应该直接放行
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
        
        System.out.println("✅ GET请求放行测试通过");
    }
    
    /**
     * 测试白名单路径直接放行
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    @DisplayName("白名单路径直接放行")
    public void testWhitelistPathPassThrough() throws Exception {
        // Given: 白名单路径
        when(request.getRequestURI()).thenReturn("/api/admin/login");
        when(request.getMethod()).thenReturn("POST");
        
        // When: 执行过滤
        interceptor.doFilter(request, response, filterChain);
        
        // Then: 应该直接放行
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
        
        System.out.println("✅ 白名单路径放行测试通过");
    }
    
    /**
     * 测试非高风险操作直接放行
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    @DisplayName("非高风险操作直接放行")
    public void testNonHighRiskOperationPassThrough() throws Exception {
        // Given: 非高风险操作路径
        when(request.getRequestURI()).thenReturn("/api/admin/users/list");
        when(request.getMethod()).thenReturn("POST");
        when(request.getAttribute("userId")).thenReturn(1L);
        when(request.getAttribute("userRole")).thenReturn("ADMIN");
        
        // When: 执行过滤
        interceptor.doFilter(request, response, filterChain);
        
        // Then: 应该直接放行
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
        
        System.out.println("✅ 非高风险操作放行测试通过");
    }
    
    /**
     * 测试高风险操作需要二次确认
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    @DisplayName("高风险操作需要二次确认")
    public void testHighRiskOperationRequiresConfirmation() throws Exception {
        // Given: 高风险操作路径，无确认令牌
        when(request.getRequestURI()).thenReturn("/api/admin/users/delete");
        when(request.getMethod()).thenReturn("POST");
        when(request.getAttribute("userId")).thenReturn(1L);
        when(request.getAttribute("userRole")).thenReturn("SUPER_ADMIN");
        when(request.getHeader("X-Secondary-Confirmation")).thenReturn(null);
        when(adminService.requiresSecondaryConfirmation("DELETE_USER_PERMANENTLY", "SUPER_ADMIN"))
            .thenReturn(true);
        
        // When: 执行过滤
        interceptor.doFilter(request, response, filterChain);
        
        // Then: 应该返回需要确认的响应
        verify(response).setStatus(428); // 428 Precondition Required
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(filterChain, never()).doFilter(request, response);
        
        String responseJson = responseWriter.toString();
        assertTrue(responseJson.contains("SECONDARY_CONFIRMATION_REQUIRED"), "响应应该包含确认要求");
        assertTrue(responseJson.contains("需要二次密码确认"), "响应应该包含确认消息");
        
        System.out.println("✅ 高风险操作确认要求测试通过");
    }
    
    /**
     * 测试有确认令牌的高风险操作放行
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    @DisplayName("有确认令牌的高风险操作放行")
    public void testHighRiskOperationWithTokenPassThrough() throws Exception {
        // Given: 高风险操作路径，有确认令牌
        when(request.getRequestURI()).thenReturn("/api/admin/users/delete");
        when(request.getMethod()).thenReturn("POST");
        when(request.getAttribute("userId")).thenReturn(1L);
        when(request.getAttribute("userRole")).thenReturn("SUPER_ADMIN");
        when(request.getHeader("X-Secondary-Confirmation")).thenReturn("valid_token_123");
        when(adminService.requiresSecondaryConfirmation("DELETE_USER_PERMANENTLY", "SUPER_ADMIN"))
            .thenReturn(true);
        
        // When: 执行过滤
        interceptor.doFilter(request, response, filterChain);
        
        // Then: 应该放行
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
        verify(request).setAttribute("highRiskOperation", "DELETE_USER_PERMANENTLY");
        
        System.out.println("✅ 有确认令牌的高风险操作放行测试通过");
    }
    
    /**
     * 测试无管理员信息的请求放行
     * BCDE原则中的Error：测试错误情况
     */
    @Test
    @DisplayName("无管理员信息的请求放行")
    public void testRequestWithoutAdminInfoPassThrough() throws Exception {
        // Given: 无管理员信息的请求
        when(request.getRequestURI()).thenReturn("/api/admin/users/delete");
        when(request.getMethod()).thenReturn("POST");
        when(request.getAttribute("userId")).thenReturn(null);
        when(request.getAttribute("userRole")).thenReturn(null);
        
        // When: 执行过滤
        interceptor.doFilter(request, response, filterChain);
        
        // Then: 应该放行（由其他拦截器处理认证）
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
        
        System.out.println("✅ 无管理员信息请求放行测试通过");
    }
    
    /**
     * 测试权限不足的操作放行
     * BCDE原则中的Error：测试错误情况
     */
    @Test
    @DisplayName("权限不足的操作放行")
    public void testInsufficientPermissionPassThrough() throws Exception {
        // Given: 权限不足的操作
        when(request.getRequestURI()).thenReturn("/api/admin/admins/create");
        when(request.getMethod()).thenReturn("POST");
        when(request.getAttribute("userId")).thenReturn(2L);
        when(request.getAttribute("userRole")).thenReturn("ADMIN");
        when(adminService.requiresSecondaryConfirmation("CREATE_ADMIN_ACCOUNT", "ADMIN"))
            .thenReturn(false);
        
        // When: 执行过滤
        interceptor.doFilter(request, response, filterChain);
        
        // Then: 应该放行（由其他拦截器处理权限检查）
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
        
        System.out.println("✅ 权限不足操作放行测试通过");
    }
    
    /**
     * 测试多种HTTP方法
     * BCDE原则中的Design：测试设计逻辑
     */
    @Test
    @DisplayName("测试不同HTTP方法")
    public void testDifferentHttpMethods() throws Exception {
        // 测试POST方法
        when(request.getRequestURI()).thenReturn("/api/admin/users/delete");
        when(request.getMethod()).thenReturn("POST");
        when(request.getAttribute("userId")).thenReturn(1L);
        when(request.getAttribute("userRole")).thenReturn("SUPER_ADMIN");
        when(request.getHeader("X-Secondary-Confirmation")).thenReturn(null);
        when(adminService.requiresSecondaryConfirmation("DELETE_USER_PERMANENTLY", "SUPER_ADMIN"))
            .thenReturn(true);
        
        interceptor.doFilter(request, response, filterChain);
        verify(response).setStatus(428); // 428 Precondition Required

        // 重置mock
        reset(response, filterChain);
        responseWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(responseWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // 测试PUT方法
        when(request.getMethod()).thenReturn("PUT");
        interceptor.doFilter(request, response, filterChain);
        verify(response).setStatus(428); // 428 Precondition Required

        // 重置mock
        reset(response, filterChain);
        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // 测试DELETE方法
        when(request.getMethod()).thenReturn("DELETE");
        interceptor.doFilter(request, response, filterChain);
        verify(response).setStatus(428); // 428 Precondition Required
        
        System.out.println("✅ 不同HTTP方法测试通过");
    }
    
    /**
     * 可测试的SecondaryConfirmationInterceptor，支持依赖注入
     */
    private static class TestableSecondaryConfirmationInterceptor extends SecondaryConfirmationInterceptor {

        public TestableSecondaryConfirmationInterceptor(AdminService adminService) {
            this.adminService = adminService;
        }

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
            // 调用父类初始化，但adminService已经在构造函数中设置
            super.init(filterConfig);
        }
    }
}
