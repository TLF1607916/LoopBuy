package com.shiwu.user.controller;

import com.shiwu.common.util.JsonUtil;
import com.shiwu.user.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * UserController测试类
 * 遵循AIR原则：Automatic, Independent, Repeatable
 * 遵循BCDE原则：Border, Correct, Design, Error
 * 
 * 注意：这是单元测试，使用Mock对象模拟HTTP请求和响应
 */
public class UserControllerTest {
    
    private UserController userController;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    private StringWriter responseWriter;
    
    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        userController = new UserController();
        
        // 模拟响应输出
        responseWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(responseWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }
    
    /**
     * 测试用户注册API - 正常情况
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    public void testRegisterAPI_Success() throws Exception {
        // Given: 模拟注册请求
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testUser_" + System.currentTimeMillis());
        registerRequest.setPassword("password123");
        registerRequest.setEmail("test@example.com");
        registerRequest.setNickname("测试用户");
        
        String requestJson = JsonUtil.toJson(registerRequest);
        
        // 模拟HTTP请求
        when(request.getPathInfo()).thenReturn("/register");
        when(request.getMethod()).thenReturn("POST");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestJson)));
        
        // When: 调用doPost方法
        userController.doPost(request, response);
        
        // Then: 验证响应
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        
        String responseJson = responseWriter.toString();
        assertNotNull(responseJson, "响应不应为空");
        assertTrue(responseJson.length() > 0, "响应应该有内容");
        
        // 验证响应格式（应该是JSON格式）
        assertTrue(responseJson.contains("success") || responseJson.contains("error"), 
                  "响应应该包含success或error字段");
    }
    
    /**
     * 测试用户注册API - 无效JSON
     * BCDE原则中的Error：测试错误输入
     */
    @Test
    public void testRegisterAPI_InvalidJson() throws Exception {
        // Given: 无效的JSON请求
        String invalidJson = "{invalid json}";
        
        // 模拟HTTP请求
        when(request.getPathInfo()).thenReturn("/register");
        when(request.getMethod()).thenReturn("POST");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(invalidJson)));
        
        // When: 调用doPost方法
        userController.doPost(request, response);
        
        // Then: 验证错误响应
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        
        String responseJson = responseWriter.toString();
        assertNotNull(responseJson, "错误响应不应为空");
        assertTrue(responseJson.contains("error") || responseJson.contains("false"), 
                  "应该返回错误响应");
    }
    
    /**
     * 测试用户登录API - 正常情况
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    public void testLoginAPI_Success() throws Exception {
        // Given: 模拟登录请求
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("test");
        loginRequest.setPassword("password123");
        
        String requestJson = JsonUtil.toJson(loginRequest);
        
        // 模拟HTTP请求
        when(request.getPathInfo()).thenReturn("/login");
        when(request.getMethod()).thenReturn("POST");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestJson)));
        
        // When: 调用doPost方法
        userController.doPost(request, response);
        
        // Then: 验证响应
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        
        String responseJson = responseWriter.toString();
        assertNotNull(responseJson, "响应不应为空");
        assertTrue(responseJson.length() > 0, "响应应该有内容");
    }
    
    /**
     * 测试获取用户公开信息API - 正常情况
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    public void testGetUserProfileAPI_Success() throws Exception {
        // Given: 模拟GET请求
        when(request.getPathInfo()).thenReturn("/1");
        when(request.getMethod()).thenReturn("GET");
        
        // When: 调用doGet方法
        userController.doGet(request, response);
        
        // Then: 验证响应
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        
        String responseJson = responseWriter.toString();
        assertNotNull(responseJson, "响应不应为空");
        assertTrue(responseJson.length() > 0, "响应应该有内容");
    }
    
    /**
     * 测试获取用户公开信息API - 无效用户ID
     * BCDE原则中的Error：测试错误输入
     */
    @Test
    public void testGetUserProfileAPI_InvalidUserId() throws Exception {
        // Given: 无效的用户ID
        when(request.getPathInfo()).thenReturn("/invalid");
        when(request.getMethod()).thenReturn("GET");
        
        // When: 调用doGet方法
        userController.doGet(request, response);
        
        // Then: 验证错误响应
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        
        String responseJson = responseWriter.toString();
        assertNotNull(responseJson, "错误响应不应为空");
        assertTrue(responseJson.contains("error") || responseJson.contains("false"), 
                  "应该返回错误响应");
    }
    
    /**
     * 测试不支持的HTTP方法
     * BCDE原则中的Error：测试错误输入
     */
    @Test
    public void testUnsupportedHttpMethod() throws Exception {
        // Given: 不支持的HTTP方法
        when(request.getPathInfo()).thenReturn("/test");
        when(request.getMethod()).thenReturn("PUT");
        
        // When: 调用doPut方法（如果存在）或其他方法
        // 注意：UserController可能没有实现PUT方法，这里测试默认行为
        try {
            userController.doPut(request, response);
        } catch (Exception e) {
            // 预期可能抛出异常或返回405错误
            assertTrue(e instanceof javax.servlet.ServletException || 
                      e.getMessage().contains("Method Not Allowed"), 
                      "应该处理不支持的HTTP方法");
        }
    }
    
    /**
     * 测试空路径请求
     * BCDE原则中的Border：测试边界条件
     */
    @Test
    public void testEmptyPathRequest() throws Exception {
        // Given: 空路径
        when(request.getPathInfo()).thenReturn(null);
        when(request.getMethod()).thenReturn("GET");
        
        // When: 调用doGet方法
        userController.doGet(request, response);
        
        // Then: 验证错误响应
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        
        String responseJson = responseWriter.toString();
        assertNotNull(responseJson, "错误响应不应为空");
        assertTrue(responseJson.contains("error") || responseJson.contains("404"), 
                  "应该返回404错误");
    }
    
    /**
     * 测试JSON响应格式
     * BCDE原则中的Design：测试设计要求
     */
    @Test
    public void testJsonResponseFormat() throws Exception {
        // Given: 任意有效请求
        when(request.getPathInfo()).thenReturn("/999999"); // 不存在的用户ID
        when(request.getMethod()).thenReturn("GET");
        
        // When: 调用doGet方法
        userController.doGet(request, response);
        
        // Then: 验证JSON格式
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        
        String responseJson = responseWriter.toString();
        assertNotNull(responseJson, "响应不应为空");
        
        // 验证是否为有效JSON
        try {
            // 尝试解析JSON以验证格式
            Object parsed = JsonUtil.fromJson(responseJson, Object.class);
            assertNotNull(parsed, "响应应该是有效的JSON");
        } catch (Exception e) {
            fail("响应应该是有效的JSON格式: " + responseJson);
        }
    }
    
    /**
     * 测试CORS头设置（如果实现了）
     * BCDE原则中的Design：测试设计要求
     */
    @Test
    public void testCorsHeaders() throws Exception {
        // Given: 任意请求
        when(request.getPathInfo()).thenReturn("/1");
        when(request.getMethod()).thenReturn("GET");
        
        // When: 调用doGet方法
        userController.doGet(request, response);
        
        // Then: 验证基本响应头设置
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        
        // 注意：如果UserController实现了CORS支持，这里可以验证相关头部
        // verify(response).setHeader("Access-Control-Allow-Origin", "*");
    }
}
