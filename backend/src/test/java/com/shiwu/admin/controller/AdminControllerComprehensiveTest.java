package com.shiwu.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shiwu.admin.model.*;
import com.shiwu.admin.service.AdminService;
//import com.shiwu.common.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AdminController综合测试
 * 
 * 测试管理员控制器的所有核心功能，包括：
 * 1. 管理员登录接口
 * 2. 二次确认接口
 * 3. 客户端IP地址获取
 * 4. 确认令牌生成
 * 5. JSON请求解析
 * 6. 错误处理和异常情况
 * 7. 参数验证
 * 8. 响应格式处理
 * 
 * @author LoopBuy Team
 * @version 1.0
 * @since 2024-01-15
 */
public class AdminControllerComprehensiveTest {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminControllerComprehensiveTest.class);
    
    private AdminController adminController;
    
    @Mock
    private AdminService mockAdminService;
    
    @Mock
    private HttpServletRequest mockRequest;
    
    @Mock
    private HttpServletResponse mockResponse;
    
    private StringWriter responseWriter;
    private PrintWriter printWriter;
    private ObjectMapper objectMapper;
    
    // 测试数据常量
    private static final String TEST_ADMIN_USERNAME = "admin";
    private static final String TEST_ADMIN_PASSWORD = "admin123";
    private static final Long TEST_ADMIN_ID = 1L;
    private static final String TEST_IP_ADDRESS = "192.168.1.100";
    private static final String TEST_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)";
    private static final String TEST_OPERATION_CODE = "DELETE_USER";
    
    @BeforeEach
    void setUp() throws Exception {
        logger.info("AdminController测试环境初始化开始");
        
        MockitoAnnotations.openMocks(this);
        
        // 创建AdminController实例，注入Mock的AdminService
        adminController = new AdminController(mockAdminService);
        
        // 设置响应Writer
        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);
        when(mockResponse.getWriter()).thenReturn(printWriter);
        
        // 创建ObjectMapper
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        logger.info("AdminController测试环境初始化完成");
    }
    
    @AfterEach
    void tearDown() {
        logger.info("AdminController测试清理完成");
    }
    
    /**
     * 测试管理员登录接口
     */
    @Test
    void testHandleAdminLogin() throws Exception {
        logger.info("开始测试管理员登录接口");
        
        // 准备测试数据
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("username", TEST_ADMIN_USERNAME);
        requestData.put("password", TEST_ADMIN_PASSWORD);
        String requestBody = objectMapper.writeValueAsString(requestData);
        
        // 准备Mock返回数据
        AdminVO adminVO = new AdminVO();
        adminVO.setId(TEST_ADMIN_ID);
        adminVO.setUsername(TEST_ADMIN_USERNAME);
        adminVO.setToken("mock_admin_token");

        AdminLoginResult loginResult = AdminLoginResult.success(adminVO);
        
        // 设置Mock行为
        when(mockRequest.getPathInfo()).thenReturn("/login");
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
        when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
        when(mockAdminService.login(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD, TEST_IP_ADDRESS, TEST_USER_AGENT))
                .thenReturn(loginResult);
        
        // 执行测试
        adminController.doPost(mockRequest, mockResponse);
        
        // 验证结果
        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
        verify(mockResponse).setContentType("application/json;charset=UTF-8");
        verify(mockAdminService).login(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD, TEST_IP_ADDRESS, TEST_USER_AGENT);
        
        String responseJson = responseWriter.toString();
        assertNotNull(responseJson, "响应不应为空");
        assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
        assertTrue(responseJson.contains(TEST_ADMIN_USERNAME), "响应应包含管理员用户名");
        assertTrue(responseJson.contains("mock_admin_token"), "响应应包含访问令牌");
        
        logger.info("管理员登录接口测试通过: response={}", responseJson);
    }
    
    /**
     * 测试管理员登录接口参数验证
     */
    @Test
    void testHandleAdminLoginValidation() throws Exception {
        logger.info("开始测试管理员登录接口参数验证");
        
        // 测试空请求体
        when(mockRequest.getPathInfo()).thenReturn("/login");
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader("")));
        
        adminController.doPost(mockRequest, mockResponse);
        
        String responseJson = responseWriter.toString();
        assertTrue(responseJson.contains("\"success\":false"), "空请求体应返回失败");
        assertTrue(responseJson.contains("A0101"), "应返回参数错误码");
        
        // 重置响应Writer
        responseWriter.getBuffer().setLength(0);
        
        // 测试空用户名
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("username", "");
        requestData.put("password", TEST_ADMIN_PASSWORD);
        String requestBody = objectMapper.writeValueAsString(requestData);
        
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        
        adminController.doPost(mockRequest, mockResponse);
        
        responseJson = responseWriter.toString();
        assertTrue(responseJson.contains("\"success\":false"), "空用户名应返回失败");
        assertTrue(responseJson.contains("A0101"), "应返回参数错误码");
        
        logger.info("管理员登录接口参数验证测试通过");
    }
    
    /**
     * 测试管理员登录失败情况
     */
    @Test
    void testHandleAdminLoginFailure() throws Exception {
        logger.info("开始测试管理员登录失败情况");
        
        // 准备测试数据
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("username", TEST_ADMIN_USERNAME);
        requestData.put("password", "wrong_password");
        String requestBody = objectMapper.writeValueAsString(requestData);
        
        // 准备Mock返回数据
        AdminLoginResult loginResult = AdminLoginResult.fail(AdminLoginErrorEnum.WRONG_PASSWORD);
        
        // 设置Mock行为
        when(mockRequest.getPathInfo()).thenReturn("/login");
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
        when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
        when(mockAdminService.login(TEST_ADMIN_USERNAME, "wrong_password", TEST_IP_ADDRESS, TEST_USER_AGENT))
                .thenReturn(loginResult);
        
        // 执行测试
        adminController.doPost(mockRequest, mockResponse);
        
        // 验证结果
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockAdminService).login(TEST_ADMIN_USERNAME, "wrong_password", TEST_IP_ADDRESS, TEST_USER_AGENT);
        
        String responseJson = responseWriter.toString();
        assertTrue(responseJson.contains("\"success\":false"), "错误密码应返回失败");
        assertTrue(responseJson.contains(AdminLoginErrorEnum.WRONG_PASSWORD.getCode()), "应返回密码错误码");
        
        logger.info("管理员登录失败情况测试通过: response={}", responseJson);
    }
    
    /**
     * 测试二次确认接口
     */
    @Test
    void testHandleSecondaryConfirmation() throws Exception {
        logger.info("开始测试二次确认接口");
        
        // 准备测试数据
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("password", TEST_ADMIN_PASSWORD);
        requestData.put("operationCode", TEST_OPERATION_CODE);
        String requestBody = objectMapper.writeValueAsString(requestData);
        
        // 准备Mock返回数据
        SecondaryConfirmationResult confirmationResult = SecondaryConfirmationResult.success("删除用户操作");
        
        // 设置Mock行为
        when(mockRequest.getPathInfo()).thenReturn("/confirm");
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(mockRequest.getAttribute("userId")).thenReturn(TEST_ADMIN_ID);
        when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
        when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
        when(mockAdminService.verifySecondaryConfirmation(
                TEST_ADMIN_ID, TEST_ADMIN_PASSWORD, TEST_OPERATION_CODE, TEST_IP_ADDRESS, TEST_USER_AGENT))
                .thenReturn(confirmationResult);
        
        // 执行测试
        adminController.doPost(mockRequest, mockResponse);
        
        // 验证结果
        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
        verify(mockResponse).setContentType("application/json;charset=UTF-8");
        verify(mockAdminService).verifySecondaryConfirmation(
                TEST_ADMIN_ID, TEST_ADMIN_PASSWORD, TEST_OPERATION_CODE, TEST_IP_ADDRESS, TEST_USER_AGENT);
        
        String responseJson = responseWriter.toString();
        assertNotNull(responseJson, "响应不应为空");
        assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
        assertTrue(responseJson.contains("confirmationToken"), "响应应包含确认令牌");
        assertTrue(responseJson.contains(TEST_OPERATION_CODE), "响应应包含操作代码");
        
        logger.info("二次确认接口测试通过: response={}", responseJson);
    }
    
    /**
     * 测试二次确认接口参数验证
     */
    @Test
    void testHandleSecondaryConfirmationValidation() throws Exception {
        logger.info("开始测试二次确认接口参数验证");
        
        // 测试空请求体
        when(mockRequest.getPathInfo()).thenReturn("/confirm");
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader("")));
        
        adminController.doPost(mockRequest, mockResponse);
        
        String responseJson = responseWriter.toString();
        assertTrue(responseJson.contains("\"success\":false"), "空请求体应返回失败");
        assertTrue(responseJson.contains("SC0101"), "应返回参数错误码");
        
        // 重置响应Writer
        responseWriter.getBuffer().setLength(0);
        
        // 测试缺少管理员ID
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("password", TEST_ADMIN_PASSWORD);
        requestData.put("operationCode", TEST_OPERATION_CODE);
        String requestBody = objectMapper.writeValueAsString(requestData);
        
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(mockRequest.getAttribute("userId")).thenReturn(null);
        
        adminController.doPost(mockRequest, mockResponse);
        
        responseJson = responseWriter.toString();
        assertTrue(responseJson.contains("\"success\":false"), "缺少管理员ID应返回失败");
        assertTrue(responseJson.contains("SC0202"), "应返回管理员信息不存在错误码");
        
        logger.info("二次确认接口参数验证测试通过");
    }

    /**
     * 测试二次确认失败情况
     */
    @Test
    void testHandleSecondaryConfirmationFailure() throws Exception {
        logger.info("开始测试二次确认失败情况");

        // 准备测试数据
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("password", "wrong_password");
        requestData.put("operationCode", TEST_OPERATION_CODE);
        String requestBody = objectMapper.writeValueAsString(requestData);

        // 准备Mock返回数据
        SecondaryConfirmationResult confirmationResult = SecondaryConfirmationResult.fail(
                SecondaryConfirmationErrorEnum.WRONG_PASSWORD);

        // 设置Mock行为
        when(mockRequest.getPathInfo()).thenReturn("/confirm");
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(mockRequest.getAttribute("userId")).thenReturn(TEST_ADMIN_ID);
        when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
        when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
        when(mockAdminService.verifySecondaryConfirmation(
                TEST_ADMIN_ID, "wrong_password", TEST_OPERATION_CODE, TEST_IP_ADDRESS, TEST_USER_AGENT))
                .thenReturn(confirmationResult);

        // 执行测试
        adminController.doPost(mockRequest, mockResponse);

        // 验证结果
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        String responseJson = responseWriter.toString();
        assertTrue(responseJson.contains("\"success\":false"), "错误密码应返回失败");
        assertTrue(responseJson.contains(SecondaryConfirmationErrorEnum.WRONG_PASSWORD.getCode()),
                  "应返回密码错误码");

        logger.info("二次确认失败情况测试通过: response={}", responseJson);
    }

    /**
     * 测试客户端IP地址获取
     */
    @Test
    void testGetClientIpAddress() throws Exception {
        logger.info("开始测试客户端IP地址获取");

        // 测试X-Forwarded-For头
        when(mockRequest.getHeader("X-Forwarded-For")).thenReturn("203.0.113.1, 198.51.100.1");
        when(mockRequest.getHeader("X-Real-IP")).thenReturn(null);
        when(mockRequest.getRemoteAddr()).thenReturn("192.168.1.1");

        // 通过反射调用私有方法
        java.lang.reflect.Method method = AdminController.class.getDeclaredMethod("getClientIpAddress", HttpServletRequest.class);
        method.setAccessible(true);
        String ipAddress = (String) method.invoke(adminController, mockRequest);

        assertEquals("203.0.113.1", ipAddress, "应该返回X-Forwarded-For中的第一个IP");

        // 测试X-Real-IP头
        when(mockRequest.getHeader("X-Forwarded-For")).thenReturn(null);
        when(mockRequest.getHeader("X-Real-IP")).thenReturn("203.0.113.2");

        ipAddress = (String) method.invoke(adminController, mockRequest);
        assertEquals("203.0.113.2", ipAddress, "应该返回X-Real-IP中的IP");

        // 测试RemoteAddr
        when(mockRequest.getHeader("X-Forwarded-For")).thenReturn(null);
        when(mockRequest.getHeader("X-Real-IP")).thenReturn(null);
        when(mockRequest.getRemoteAddr()).thenReturn("192.168.1.1");

        ipAddress = (String) method.invoke(adminController, mockRequest);
        assertEquals("192.168.1.1", ipAddress, "应该返回RemoteAddr中的IP");

        logger.info("客户端IP地址获取测试通过");
    }

    /**
     * 测试确认令牌生成
     */
    @Test
    void testGenerateConfirmationToken() throws Exception {
        logger.info("开始测试确认令牌生成");

        // 通过反射调用私有方法
        java.lang.reflect.Method method = AdminController.class.getDeclaredMethod("generateConfirmationToken", Long.class, String.class);
        method.setAccessible(true);
        String token = (String) method.invoke(adminController, TEST_ADMIN_ID, TEST_OPERATION_CODE);

        assertNotNull(token, "确认令牌不应为空");
        assertTrue(token.contains(TEST_ADMIN_ID.toString()), "令牌应包含管理员ID");
        assertTrue(token.contains(TEST_OPERATION_CODE), "令牌应包含操作代码");
        assertTrue(token.matches("\\d+_[A-Z_]+_\\d+"), "令牌格式应正确");

        logger.info("确认令牌生成测试通过: token={}", token);
    }

    /**
     * 测试HTTP方法路由
     */
    @Test
    void testHttpMethodRouting() throws Exception {
        logger.info("开始测试HTTP方法路由");

        // 测试无效路径
        when(mockRequest.getPathInfo()).thenReturn("/invalid");

        adminController.doPost(mockRequest, mockResponse);

        String responseJson = responseWriter.toString();
        assertTrue(responseJson.contains("\"success\":false"), "无效路径应返回失败");
        assertTrue(responseJson.contains("404"), "应返回404错误");

        // 重置响应Writer
        responseWriter.getBuffer().setLength(0);

        // 测试null路径
        when(mockRequest.getPathInfo()).thenReturn(null);

        adminController.doPost(mockRequest, mockResponse);

        responseJson = responseWriter.toString();
        assertTrue(responseJson.contains("\"success\":false"), "null路径应返回失败");
        assertTrue(responseJson.contains("404"), "应返回404错误");

        logger.info("HTTP方法路由测试通过");
    }

    /**
     * 测试系统异常处理
     */
    @Test
    void testSystemExceptionHandling() throws Exception {
        logger.info("开始测试系统异常处理");

        // 设置Mock行为，模拟系统异常
        when(mockRequest.getPathInfo()).thenReturn("/login");
        when(mockRequest.getReader()).thenThrow(new RuntimeException("系统异常"));

        // 执行测试
        adminController.doPost(mockRequest, mockResponse);

        // 验证结果
        String responseJson = responseWriter.toString();
        assertTrue(responseJson.contains("\"success\":false"), "系统异常应返回失败");
        assertTrue(responseJson.contains("A0500"), "应返回系统错误码");

        logger.info("系统异常处理测试通过: response={}", responseJson);
    }

    /**
     * 测试JSON解析异常处理
     */
    @Test
    void testJsonParsingException() throws Exception {
        logger.info("开始测试JSON解析异常处理");

        // 准备无效的JSON数据
        String invalidJson = "{invalid json}";

        // 设置Mock行为
        when(mockRequest.getPathInfo()).thenReturn("/login");
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(invalidJson)));

        // 执行测试
        adminController.doPost(mockRequest, mockResponse);

        // 验证结果
        String responseJson = responseWriter.toString();
        assertTrue(responseJson.contains("\"success\":false"), "无效JSON应返回失败");
        assertTrue(responseJson.contains("A0500"), "应返回系统错误码");

        logger.info("JSON解析异常处理测试通过: response={}", responseJson);
    }

    /**
     * 测试完整的管理员操作流程
     */
    @Test
    void testCompleteAdminWorkflow() throws Exception {
        logger.info("开始测试完整的管理员操作流程");

        // 1. 管理员登录
        Map<String, Object> loginData = new HashMap<>();
        loginData.put("username", TEST_ADMIN_USERNAME);
        loginData.put("password", TEST_ADMIN_PASSWORD);
        String loginBody = objectMapper.writeValueAsString(loginData);

        AdminVO adminVO = new AdminVO();
        adminVO.setId(TEST_ADMIN_ID);
        adminVO.setUsername(TEST_ADMIN_USERNAME);
        adminVO.setToken("admin_token_123");

        AdminLoginResult loginResult = AdminLoginResult.success(adminVO);

        when(mockRequest.getPathInfo()).thenReturn("/login");
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(loginBody)));
        when(mockRequest.getRemoteAddr()).thenReturn(TEST_IP_ADDRESS);
        when(mockRequest.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
        when(mockAdminService.login(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD, TEST_IP_ADDRESS, TEST_USER_AGENT))
                .thenReturn(loginResult);

        adminController.doPost(mockRequest, mockResponse);

        String loginResponse = responseWriter.toString();
        assertTrue(loginResponse.contains("\"success\":true"), "登录应该成功");
        assertTrue(loginResponse.contains("admin_token_123"), "应包含访问令牌");
        logger.info("管理员登录成功: {}", loginResponse);

        // 重置响应Writer
        responseWriter.getBuffer().setLength(0);

        // 2. 二次确认
        Map<String, Object> confirmData = new HashMap<>();
        confirmData.put("password", TEST_ADMIN_PASSWORD);
        confirmData.put("operationCode", TEST_OPERATION_CODE);
        String confirmBody = objectMapper.writeValueAsString(confirmData);

        SecondaryConfirmationResult confirmationResult = SecondaryConfirmationResult.success("删除用户操作");

        when(mockRequest.getPathInfo()).thenReturn("/confirm");
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(confirmBody)));
        when(mockRequest.getAttribute("userId")).thenReturn(TEST_ADMIN_ID);
        when(mockAdminService.verifySecondaryConfirmation(
                TEST_ADMIN_ID, TEST_ADMIN_PASSWORD, TEST_OPERATION_CODE, TEST_IP_ADDRESS, TEST_USER_AGENT))
                .thenReturn(confirmationResult);

        adminController.doPost(mockRequest, mockResponse);

        String confirmResponse = responseWriter.toString();
        assertTrue(confirmResponse.contains("\"success\":true"), "二次确认应该成功");
        assertTrue(confirmResponse.contains("confirmationToken"), "应包含确认令牌");
        logger.info("二次确认成功: {}", confirmResponse);

        logger.info("完整的管理员操作流程测试通过");
    }
}
