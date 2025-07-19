package com.shiwu.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shiwu.admin.model.*;
import com.shiwu.admin.service.AdminService;
import com.shiwu.common.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AdminController单元测试
 * 使用Mockito模拟HTTP请求和响应
 */
@DisplayName("管理员控制器测试")
public class AdminControllerTest {
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private AdminService adminService;
    
    private AdminController adminController;
    private ObjectMapper objectMapper;
    private StringWriter responseWriter;
    
    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        // 创建可测试的AdminController
        adminController = new AdminController(adminService);
        objectMapper = new ObjectMapper();
        
        // 设置响应Writer
        responseWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(responseWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }
    
    /**
     * 测试管理员登录 - 成功情况
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    @DisplayName("管理员登录 - 成功")
    public void testAdminLogin_Success() throws Exception {
        // Given: 准备登录请求数据
        String requestJson = "{\"username\":\"admin\",\"password\":\"admin123\"}";
        BufferedReader reader = new BufferedReader(new StringReader(requestJson));
        
        // 准备成功的登录结果
        AdminVO adminVO = createMockAdminVO();
        AdminLoginResult successResult = AdminLoginResult.success(adminVO);
        
        // Mock请求和服务
        when(request.getPathInfo()).thenReturn("/login");
        when(request.getReader()).thenReturn(reader);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
        
        when(adminService.login(eq("admin"), eq("admin123"), eq("127.0.0.1"), eq("Mozilla/5.0")))
            .thenReturn(successResult);
        
        // When: 执行登录请求
        adminController.doPost(request, response);
        
        // Then: 验证响应
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response, atLeastOnce()).setContentType("application/json;charset=UTF-8");
        
        String responseJson = responseWriter.toString();
        assertNotNull(responseJson, "响应不应为空");
        assertTrue(responseJson.contains("\"success\":true"), "响应应该表示成功");
        assertTrue(responseJson.contains("\"username\":\"admin\""), "响应应该包含用户名");
        
        // 验证服务方法被调用
        verify(adminService).login(eq("admin"), eq("admin123"), eq("127.0.0.1"), eq("Mozilla/5.0"));
        
        System.out.println("✅ 管理员登录成功测试通过");
    }
    
    /**
     * 测试管理员登录 - 用户名为空
     * BCDE原则中的Boundary：测试边界条件
     */
    @Test
    @DisplayName("管理员登录 - 用户名为空")
    public void testAdminLogin_EmptyUsername() throws Exception {
        // Given: 用户名为空的请求
        String requestJson = "{\"username\":\"\",\"password\":\"admin123\"}";
        BufferedReader reader = new BufferedReader(new StringReader(requestJson));
        
        when(request.getPathInfo()).thenReturn("/login");
        when(request.getReader()).thenReturn(reader);
        
        // When: 执行登录请求
        adminController.doPost(request, response);
        
        // Then: 验证响应
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response).setContentType("application/json;charset=UTF-8");
        
        String responseJson = responseWriter.toString();
        assertNotNull(responseJson, "响应不应为空");
        assertTrue(responseJson.contains("\"success\":false"), "响应应该表示失败");
        assertTrue(responseJson.contains("用户名不能为空"), "响应应该包含错误信息");
        
        // 验证服务方法没有被调用
        verify(adminService, never()).login(anyString(), anyString(), anyString(), anyString());
        
        System.out.println("✅ 用户名为空登录测试通过");
    }
    
    /**
     * 测试管理员登录 - 密码为空
     * BCDE原则中的Boundary：测试边界条件
     */
    @Test
    @DisplayName("管理员登录 - 密码为空")
    public void testAdminLogin_EmptyPassword() throws Exception {
        // Given: 密码为空的请求
        String requestJson = "{\"username\":\"admin\",\"password\":\"\"}";
        BufferedReader reader = new BufferedReader(new StringReader(requestJson));
        
        when(request.getPathInfo()).thenReturn("/login");
        when(request.getReader()).thenReturn(reader);
        
        // When: 执行登录请求
        adminController.doPost(request, response);
        
        // Then: 验证响应
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response).setContentType("application/json;charset=UTF-8");
        
        String responseJson = responseWriter.toString();
        assertNotNull(responseJson, "响应不应为空");
        assertTrue(responseJson.contains("\"success\":false"), "响应应该表示失败");
        assertTrue(responseJson.contains("密码不能为空"), "响应应该包含错误信息");
        
        // 验证服务方法没有被调用
        verify(adminService, never()).login(anyString(), anyString(), anyString(), anyString());
        
        System.out.println("✅ 密码为空登录测试通过");
    }
    
    /**
     * 测试管理员登录 - 请求体为空
     * BCDE原则中的Error：测试错误情况
     */
    @Test
    @DisplayName("管理员登录 - 请求体为空")
    public void testAdminLogin_EmptyRequestBody() throws Exception {
        // Given: 空请求体
        BufferedReader reader = new BufferedReader(new StringReader(""));
        
        when(request.getPathInfo()).thenReturn("/login");
        when(request.getReader()).thenReturn(reader);
        
        // When: 执行登录请求
        adminController.doPost(request, response);
        
        // Then: 验证响应
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response).setContentType("application/json;charset=UTF-8");
        
        String responseJson = responseWriter.toString();
        assertNotNull(responseJson, "响应不应为空");
        assertTrue(responseJson.contains("\"success\":false"), "响应应该表示失败");
        assertTrue(responseJson.contains("请求体不能为空"), "响应应该包含错误信息");
        
        // 验证服务方法没有被调用
        verify(adminService, never()).login(anyString(), anyString(), anyString(), anyString());
        
        System.out.println("✅ 空请求体登录测试通过");
    }
    
    /**
     * 测试管理员登录 - 登录失败
     * BCDE原则中的Error：测试错误情况
     */
    @Test
    @DisplayName("管理员登录 - 登录失败")
    public void testAdminLogin_LoginFailed() throws Exception {
        // Given: 准备登录请求数据
        String requestJson = "{\"username\":\"admin\",\"password\":\"wrongpassword\"}";
        BufferedReader reader = new BufferedReader(new StringReader(requestJson));
        
        // 准备失败的登录结果
        AdminLoginResult failResult = AdminLoginResult.fail(AdminLoginErrorEnum.WRONG_PASSWORD);
        
        // Mock请求和服务
        when(request.getPathInfo()).thenReturn("/login");
        when(request.getReader()).thenReturn(reader);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
        
        when(adminService.login(eq("admin"), eq("wrongpassword"), eq("127.0.0.1"), eq("Mozilla/5.0")))
            .thenReturn(failResult);
        
        // When: 执行登录请求
        adminController.doPost(request, response);
        
        // Then: 验证响应
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response).setContentType("application/json;charset=UTF-8");
        
        String responseJson = responseWriter.toString();
        assertNotNull(responseJson, "响应不应为空");
        assertTrue(responseJson.contains("\"success\":false"), "响应应该表示失败");
        assertTrue(responseJson.contains("A0103"), "响应应该包含错误代码");
        assertTrue(responseJson.contains("密码错误"), "响应应该包含错误信息");
        
        // 验证服务方法被调用
        verify(adminService).login(eq("admin"), eq("wrongpassword"), eq("127.0.0.1"), eq("Mozilla/5.0"));
        
        System.out.println("✅ 登录失败测试通过");
    }
    
    /**
     * 测试二次确认 - 成功情况
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    @DisplayName("二次确认 - 成功")
    public void testSecondaryConfirmation_Success() throws Exception {
        // Given: 准备二次确认请求数据
        String requestJson = "{\"password\":\"admin123\",\"operationCode\":\"DELETE_USER_PERMANENTLY\"}";
        BufferedReader reader = new BufferedReader(new StringReader(requestJson));

        // 准备成功的确认结果
        SecondaryConfirmationResult successResult = SecondaryConfirmationResult.success("二次确认成功", "永久删除用户账户");

        // Mock请求和服务
        when(request.getPathInfo()).thenReturn("/confirm");
        when(request.getReader()).thenReturn(reader);
        when(request.getAttribute("userId")).thenReturn(1L);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");

        when(adminService.verifySecondaryConfirmation(eq(1L), eq("admin123"), eq("DELETE_USER_PERMANENTLY"),
                                                     eq("127.0.0.1"), eq("Mozilla/5.0")))
            .thenReturn(successResult);

        // When: 执行二次确认请求
        adminController.doPost(request, response);

        // Then: 验证响应
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response, atLeastOnce()).setContentType("application/json;charset=UTF-8");

        String responseJson = responseWriter.toString();
        assertNotNull(responseJson, "响应不应为空");
        assertTrue(responseJson.contains("\"success\":true"), "响应应该表示成功");
        assertTrue(responseJson.contains("confirmationToken"), "响应应该包含确认令牌");
        assertTrue(responseJson.contains("DELETE_USER_PERMANENTLY"), "响应应该包含操作代码");

        // 验证服务方法被调用
        verify(adminService).verifySecondaryConfirmation(eq(1L), eq("admin123"), eq("DELETE_USER_PERMANENTLY"),
                                                        eq("127.0.0.1"), eq("Mozilla/5.0"));

        System.out.println("✅ 二次确认成功测试通过");
    }

    /**
     * 测试二次确认 - 密码为空
     * BCDE原则中的Boundary：测试边界条件
     */
    @Test
    @DisplayName("二次确认 - 密码为空")
    public void testSecondaryConfirmation_EmptyPassword() throws Exception {
        // Given: 密码为空的请求
        String requestJson = "{\"password\":\"\",\"operationCode\":\"DELETE_USER_PERMANENTLY\"}";
        BufferedReader reader = new BufferedReader(new StringReader(requestJson));

        when(request.getPathInfo()).thenReturn("/confirm");
        when(request.getReader()).thenReturn(reader);
        when(request.getAttribute("userId")).thenReturn(1L);

        // When: 执行二次确认请求
        adminController.doPost(request, response);

        // Then: 验证响应
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response, atLeastOnce()).setContentType("application/json;charset=UTF-8");

        String responseJson = responseWriter.toString();
        assertNotNull(responseJson, "响应不应为空");
        assertTrue(responseJson.contains("\"success\":false"), "响应应该表示失败");
        assertTrue(responseJson.contains("密码不能为空"), "响应应该包含错误信息");

        // 验证服务方法没有被调用
        verify(adminService, never()).verifySecondaryConfirmation(anyLong(), anyString(), anyString(), anyString(), anyString());

        System.out.println("✅ 密码为空二次确认测试通过");
    }

    /**
     * 测试二次确认 - 操作代码为空
     * BCDE原则中的Boundary：测试边界条件
     */
    @Test
    @DisplayName("二次确认 - 操作代码为空")
    public void testSecondaryConfirmation_EmptyOperationCode() throws Exception {
        // Given: 操作代码为空的请求
        String requestJson = "{\"password\":\"admin123\",\"operationCode\":\"\"}";
        BufferedReader reader = new BufferedReader(new StringReader(requestJson));

        when(request.getPathInfo()).thenReturn("/confirm");
        when(request.getReader()).thenReturn(reader);
        when(request.getAttribute("userId")).thenReturn(1L);

        // When: 执行二次确认请求
        adminController.doPost(request, response);

        // Then: 验证响应
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response, atLeastOnce()).setContentType("application/json;charset=UTF-8");

        String responseJson = responseWriter.toString();
        assertNotNull(responseJson, "响应不应为空");
        assertTrue(responseJson.contains("\"success\":false"), "响应应该表示失败");
        assertTrue(responseJson.contains("操作代码不能为空"), "响应应该包含错误信息");

        // 验证服务方法没有被调用
        verify(adminService, never()).verifySecondaryConfirmation(anyLong(), anyString(), anyString(), anyString(), anyString());

        System.out.println("✅ 操作代码为空二次确认测试通过");
    }

    /**
     * 测试二次确认 - 管理员信息不存在
     * BCDE原则中的Error：测试错误情况
     */
    @Test
    @DisplayName("二次确认 - 管理员信息不存在")
    public void testSecondaryConfirmation_NoAdminInfo() throws Exception {
        // Given: 管理员信息不存在的请求
        String requestJson = "{\"password\":\"admin123\",\"operationCode\":\"DELETE_USER_PERMANENTLY\"}";
        BufferedReader reader = new BufferedReader(new StringReader(requestJson));

        when(request.getPathInfo()).thenReturn("/confirm");
        when(request.getReader()).thenReturn(reader);
        when(request.getAttribute("userId")).thenReturn(null); // 无管理员信息

        // When: 执行二次确认请求
        adminController.doPost(request, response);

        // Then: 验证响应
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response, atLeastOnce()).setContentType("application/json;charset=UTF-8");

        String responseJson = responseWriter.toString();
        assertNotNull(responseJson, "响应不应为空");
        assertTrue(responseJson.contains("\"success\":false"), "响应应该表示失败");
        assertTrue(responseJson.contains("管理员信息不存在"), "响应应该包含错误信息");

        // 验证服务方法没有被调用
        verify(adminService, never()).verifySecondaryConfirmation(anyLong(), anyString(), anyString(), anyString(), anyString());

        System.out.println("✅ 管理员信息不存在二次确认测试通过");
    }

    /**
     * 测试二次确认 - 确认失败
     * BCDE原则中的Error：测试错误情况
     */
    @Test
    @DisplayName("二次确认 - 确认失败")
    public void testSecondaryConfirmation_Failed() throws Exception {
        // Given: 准备二次确认请求数据
        String requestJson = "{\"password\":\"wrongpassword\",\"operationCode\":\"DELETE_USER_PERMANENTLY\"}";
        BufferedReader reader = new BufferedReader(new StringReader(requestJson));

        // 准备失败的确认结果
        SecondaryConfirmationResult failResult = SecondaryConfirmationResult.fail(SecondaryConfirmationErrorEnum.WRONG_PASSWORD);

        // Mock请求和服务
        when(request.getPathInfo()).thenReturn("/confirm");
        when(request.getReader()).thenReturn(reader);
        when(request.getAttribute("userId")).thenReturn(1L);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");

        when(adminService.verifySecondaryConfirmation(eq(1L), eq("wrongpassword"), eq("DELETE_USER_PERMANENTLY"),
                                                     eq("127.0.0.1"), eq("Mozilla/5.0")))
            .thenReturn(failResult);

        // When: 执行二次确认请求
        adminController.doPost(request, response);

        // Then: 验证响应
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response, atLeastOnce()).setContentType("application/json;charset=UTF-8");

        String responseJson = responseWriter.toString();
        assertNotNull(responseJson, "响应不应为空");
        assertTrue(responseJson.contains("\"success\":false"), "响应应该表示失败");
        assertTrue(responseJson.contains("SC0201"), "响应应该包含错误代码");
        assertTrue(responseJson.contains("密码错误"), "响应应该包含错误信息");

        // 验证服务方法被调用
        verify(adminService).verifySecondaryConfirmation(eq(1L), eq("wrongpassword"), eq("DELETE_USER_PERMANENTLY"),
                                                        eq("127.0.0.1"), eq("Mozilla/5.0"));

        System.out.println("✅ 二次确认失败测试通过");
    }

    /**
     * 测试不存在的路径
     * BCDE原则中的Error：测试错误情况
     */
    @Test
    @DisplayName("访问不存在的路径")
    public void testNonExistentPath() throws Exception {
        // Given: 不存在的路径
        when(request.getPathInfo()).thenReturn("/nonexistent");
        
        // When: 执行请求
        adminController.doPost(request, response);
        
        // Then: 验证响应
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response).setContentType("application/json;charset=UTF-8");
        
        String responseJson = responseWriter.toString();
        assertNotNull(responseJson, "响应不应为空");
        assertTrue(responseJson.contains("\"success\":false"), "响应应该表示失败");
        assertTrue(responseJson.contains("请求路径不存在"), "响应应该包含错误信息");
        
        System.out.println("✅ 不存在路径测试通过");
    }
    
    /**
     * 创建模拟AdminVO对象
     */
    private AdminVO createMockAdminVO() {
        AdminVO adminVO = new AdminVO();
        adminVO.setId(1L);
        adminVO.setUsername("admin");
        adminVO.setEmail("admin@shiwu.com");
        adminVO.setRealName("系统管理员");
        adminVO.setRole("SUPER_ADMIN");
        adminVO.setRoleDescription("超级管理员");
        adminVO.setStatus(1);
        adminVO.setLoginCount(1);
        adminVO.setCreateTime(LocalDateTime.now());
        adminVO.setToken("mock.jwt.token");
        return adminVO;
    }
    

}
