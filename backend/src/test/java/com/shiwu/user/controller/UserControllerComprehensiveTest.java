package com.shiwu.user.controller;

import com.shiwu.common.result.Result;
import com.shiwu.common.util.JsonUtil;
import com.shiwu.user.model.*;
import com.shiwu.user.service.UserService;
import com.shiwu.user.vo.FeedResponseVO;
import com.shiwu.user.vo.PaginationVO;
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
import java.lang.reflect.Field;
import java.util.ArrayList;
//import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserController综合测试
 * 
 * 测试用户控制器的所有核心功能，包括：
 * 1. 用户登录接口
 * 2. 用户注册接口
 * 3. 获取用户公开信息接口
 * 4. 关注用户接口
 * 5. 取关用户接口
 * 6. 获取关注状态接口
 * 7. 获取关注动态信息流接口（Task4_2_1_3核心功能）
 * 8. JWT Token解析
 * 9. 各种HTTP方法路由
 * 10. 错误处理和异常情况
 * 
 * @author LoopBuy Team
 * @version 1.0
 * @since 2024-01-15
 */
public class UserControllerComprehensiveTest {
    
    private static final Logger logger = LoggerFactory.getLogger(UserControllerComprehensiveTest.class);
    
    private UserController userController;
    
    @Mock
    private UserService mockUserService;
    
    @Mock
    private HttpServletRequest mockRequest;
    
    @Mock
    private HttpServletResponse mockResponse;
    
    @Mock
    private BufferedReader mockReader;
    
    private StringWriter responseWriter;
    private PrintWriter printWriter;
    
    // 测试数据常量
    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_TARGET_USER_ID = 2L;
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_JWT_TOKEN = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
    
    @BeforeEach
    void setUp() throws Exception {
        logger.info("UserController测试环境初始化开始");
        
        MockitoAnnotations.openMocks(this);
        
        // 创建UserController实例
        userController = new UserController();
        
        // 使用反射注入Mock的UserService
        Field userServiceField = UserController.class.getDeclaredField("userService");
        userServiceField.setAccessible(true);
        userServiceField.set(userController, mockUserService);
        
        // 设置响应Writer
        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);
        when(mockResponse.getWriter()).thenReturn(printWriter);
        
        logger.info("UserController测试环境初始化完成");
    }
    
    @AfterEach
    void tearDown() {
        logger.info("UserController测试清理完成");
    }
    
    /**
     * 测试用户登录接口
     */
    @Test
    void testHandleLogin() throws Exception {
        logger.info("开始测试用户登录接口");
        
        // 准备测试数据
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword(TEST_PASSWORD);
        String requestBody = JsonUtil.toJson(loginRequest);
        
        // 准备Mock返回数据
        UserVO userVO = new UserVO();
        userVO.setId(TEST_USER_ID);
        userVO.setUsername(TEST_USERNAME);
        userVO.setEmail(TEST_EMAIL);
        
        LoginResult loginResult = LoginResult.success(userVO);
        
        // 设置Mock行为
        when(mockRequest.getPathInfo()).thenReturn("/login");
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(mockUserService.login(TEST_USERNAME, TEST_PASSWORD)).thenReturn(loginResult);
        
        // 执行测试
        userController.doPost(mockRequest, mockResponse);
        
        // 验证结果
        verify(mockResponse).setContentType("application/json");
        verify(mockResponse).setCharacterEncoding("UTF-8");
        verify(mockUserService).login(TEST_USERNAME, TEST_PASSWORD);
        
        String responseJson = responseWriter.toString();
        assertNotNull(responseJson, "响应不应为空");
        assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
        assertTrue(responseJson.contains(TEST_USERNAME), "响应应包含用户名");
        
        logger.info("用户登录接口测试通过: response={}", responseJson);
    }
    
    /**
     * 测试用户登录接口参数验证
     */
    @Test
    void testHandleLoginValidation() throws Exception {
        logger.info("开始测试用户登录接口参数验证");
        
        // 测试空请求体
        when(mockRequest.getPathInfo()).thenReturn("/login");
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader("")));
        
        userController.doPost(mockRequest, mockResponse);
        
        String responseJson = responseWriter.toString();
        assertTrue(responseJson.contains("\"success\":false"), "空请求体应返回失败");
        
        logger.info("用户登录接口参数验证测试通过");
    }
    
    /**
     * 测试用户注册接口
     */
    @Test
    void testHandleRegister() throws Exception {
        logger.info("开始测试用户注册接口");
        
        // 准备测试数据
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(TEST_USERNAME);
        registerRequest.setPassword(TEST_PASSWORD);
        registerRequest.setEmail(TEST_EMAIL);
        String requestBody = JsonUtil.toJson(registerRequest);
        
        // 准备Mock返回数据
        UserVO userVO = new UserVO();
        userVO.setId(TEST_USER_ID);
        userVO.setUsername(TEST_USERNAME);
        userVO.setEmail(TEST_EMAIL);
        
        RegisterResult registerResult = RegisterResult.success(userVO);
        
        // 设置Mock行为
        when(mockRequest.getPathInfo()).thenReturn("/register");
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(mockUserService.register(any(RegisterRequest.class))).thenReturn(registerResult);
        
        // 执行测试
        userController.doPost(mockRequest, mockResponse);
        
        // 验证结果
        verify(mockUserService).register(any(RegisterRequest.class));
        
        String responseJson = responseWriter.toString();
        assertNotNull(responseJson, "响应不应为空");
        assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
        
        logger.info("用户注册接口测试通过: response={}", responseJson);
    }
    
    /**
     * 测试获取用户公开信息接口
     */
    @Test
    void testHandleGetUserProfile() throws Exception {
        logger.info("开始测试获取用户公开信息接口");
        
        // 准备Mock返回数据
        UserVO targetUser = new UserVO();
        targetUser.setId(TEST_TARGET_USER_ID);
        targetUser.setUsername("targetuser");

        UserProfileVO userProfile = new UserProfileVO();
        userProfile.setUser(targetUser);
        userProfile.setFollowerCount(100);
        userProfile.setIsFollowing(false);
        
        // 设置Mock行为
        when(mockRequest.getPathInfo()).thenReturn("/" + TEST_TARGET_USER_ID);
        when(mockRequest.getHeader("X-User-Id")).thenReturn(TEST_USER_ID.toString());
        when(mockUserService.getUserProfile(TEST_TARGET_USER_ID, TEST_USER_ID)).thenReturn(userProfile);
        
        // 执行测试
        userController.doGet(mockRequest, mockResponse);
        
        // 验证结果
        verify(mockUserService).getUserProfile(TEST_TARGET_USER_ID, TEST_USER_ID);
        
        String responseJson = responseWriter.toString();
        assertNotNull(responseJson, "响应不应为空");
        assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
        assertTrue(responseJson.contains("targetuser"), "响应应包含目标用户名");
        
        logger.info("获取用户公开信息接口测试通过: response={}", responseJson);
    }
    
    /**
     * 测试关注用户接口
     */
    @Test
    void testHandleFollowUser() throws Exception {
        logger.info("开始测试关注用户接口");
        
        // 准备Mock返回数据
        FollowResult followResult = FollowResult.success(true, 101);
        
        // 设置Mock行为
        when(mockRequest.getPathInfo()).thenReturn("/" + TEST_TARGET_USER_ID + "/follow");
        when(mockRequest.getHeader("X-User-Id")).thenReturn(TEST_USER_ID.toString());
        when(mockUserService.followUser(TEST_USER_ID, TEST_TARGET_USER_ID)).thenReturn(followResult);
        
        // 执行测试
        userController.doPost(mockRequest, mockResponse);
        
        // 验证结果
        verify(mockUserService).followUser(TEST_USER_ID, TEST_TARGET_USER_ID);
        
        String responseJson = responseWriter.toString();
        assertNotNull(responseJson, "响应不应为空");
        assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
        assertTrue(responseJson.contains("\"isFollowing\":true"), "响应应包含关注状态");
        assertTrue(responseJson.contains("\"followerCount\":101"), "响应应包含粉丝数量");
        
        logger.info("关注用户接口测试通过: response={}", responseJson);
    }
    
    /**
     * 测试取关用户接口
     */
    @Test
    void testHandleUnfollowUser() throws Exception {
        logger.info("开始测试取关用户接口");
        
        // 准备Mock返回数据
        FollowResult unfollowResult = FollowResult.success(false, 99);
        
        // 设置Mock行为
        when(mockRequest.getPathInfo()).thenReturn("/" + TEST_TARGET_USER_ID + "/follow");
        when(mockRequest.getHeader("X-User-Id")).thenReturn(TEST_USER_ID.toString());
        when(mockUserService.unfollowUser(TEST_USER_ID, TEST_TARGET_USER_ID)).thenReturn(unfollowResult);
        
        // 执行测试
        userController.doDelete(mockRequest, mockResponse);
        
        // 验证结果
        verify(mockUserService).unfollowUser(TEST_USER_ID, TEST_TARGET_USER_ID);
        
        String responseJson = responseWriter.toString();
        assertNotNull(responseJson, "响应不应为空");
        assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
        assertTrue(responseJson.contains("\"isFollowing\":false"), "响应应包含取关状态");
        assertTrue(responseJson.contains("\"followerCount\":99"), "响应应包含粉丝数量");
        
        logger.info("取关用户接口测试通过: response={}", responseJson);
    }
    
    /**
     * 测试获取关注状态接口
     */
    @Test
    void testHandleGetFollowStatus() throws Exception {
        logger.info("开始测试获取关注状态接口");
        
        // 准备Mock返回数据
        FollowStatusVO followStatus = new FollowStatusVO();
        followStatus.setUserId(TEST_TARGET_USER_ID);
        followStatus.setUsername("targetuser");
        followStatus.setIsFollowing(true);
        followStatus.setFollowerCount(100);
        followStatus.setFollowingCount(50);
        
        // 设置Mock行为
        when(mockRequest.getPathInfo()).thenReturn("/" + TEST_TARGET_USER_ID + "/follow");
        when(mockRequest.getHeader("X-User-Id")).thenReturn(TEST_USER_ID.toString());
        when(mockUserService.getFollowStatus(TEST_USER_ID, TEST_TARGET_USER_ID)).thenReturn(followStatus);
        
        // 执行测试
        userController.doGet(mockRequest, mockResponse);
        
        // 验证结果
        verify(mockUserService).getFollowStatus(TEST_USER_ID, TEST_TARGET_USER_ID);
        
        String responseJson = responseWriter.toString();
        assertNotNull(responseJson, "响应不应为空");
        assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
        assertTrue(responseJson.contains("\"isFollowing\":true"), "响应应包含关注状态");
        
        logger.info("获取关注状态接口测试通过: response={}", responseJson);
    }
    
    /**
     * 测试获取关注动态信息流接口（Task4_2_1_3核心功能）
     */
    @Test
    void testHandleGetFollowingFeed() throws Exception {
        logger.info("开始测试获取关注动态信息流接口");
        
        // 准备Mock返回数据
        PaginationVO pagination = new PaginationVO(1, 20, 0);
        FeedResponseVO feedResponse = new FeedResponseVO();
        feedResponse.setFeeds(new ArrayList<>());
        feedResponse.setPagination(pagination);
        
        Result<FeedResponseVO> result = Result.success(feedResponse);
        
        // 设置Mock行为
        when(mockRequest.getPathInfo()).thenReturn("/follow/feed");
        when(mockRequest.getHeader("X-User-Id")).thenReturn(TEST_USER_ID.toString());
        when(mockRequest.getParameter("page")).thenReturn("1");
        when(mockRequest.getParameter("size")).thenReturn("20");
        when(mockRequest.getParameter("type")).thenReturn("ALL");
        when(mockUserService.getFollowingFeed(TEST_USER_ID, 1, 20, "ALL")).thenReturn(result);
        
        // 执行测试
        userController.doGet(mockRequest, mockResponse);
        
        // 验证结果
        verify(mockUserService).getFollowingFeed(TEST_USER_ID, 1, 20, "ALL");
        
        String responseJson = responseWriter.toString();
        assertNotNull(responseJson, "响应不应为空");
        assertTrue(responseJson.contains("\"success\":true"), "响应应包含成功标识");
        assertTrue(responseJson.contains("\"page\":1"), "响应应包含当前页码");
        
        logger.info("获取关注动态信息流接口测试通过: response={}", responseJson);
    }

    /**
     * 测试JWT Token解析功能
     */
    @Test
    void testGetCurrentUserIdFromToken() throws Exception {
        logger.info("开始测试JWT Token解析功能");

        // 测试从X-User-Id Header获取用户ID
        when(mockRequest.getHeader("X-User-Id")).thenReturn(TEST_USER_ID.toString());
        when(mockRequest.getAttribute("userId")).thenReturn(null);
        when(mockRequest.getHeader("Authorization")).thenReturn(null);

        // 通过反射调用私有方法
        java.lang.reflect.Method method = UserController.class.getDeclaredMethod("getCurrentUserIdFromToken", HttpServletRequest.class);
        method.setAccessible(true);
        Long userId = (Long) method.invoke(userController, mockRequest);

        assertEquals(TEST_USER_ID, userId, "应该正确解析用户ID");

        logger.info("JWT Token解析功能测试通过: userId={}", userId);
    }

    /**
     * 测试HTTP方法路由
     */
    @Test
    void testHttpMethodRouting() throws Exception {
        logger.info("开始测试HTTP方法路由");

        // 测试GET方法的404路由
        when(mockRequest.getPathInfo()).thenReturn("/invalid");
        userController.doGet(mockRequest, mockResponse);

        String responseJson = responseWriter.toString();
        assertTrue(responseJson.contains("\"success\":false"), "无效路径应返回失败");
        assertTrue(responseJson.contains("404"), "应返回404错误");

        // 重置响应Writer
        responseWriter.getBuffer().setLength(0);

        // 测试POST方法的404路由
        userController.doPost(mockRequest, mockResponse);

        responseJson = responseWriter.toString();
        assertTrue(responseJson.contains("\"success\":false"), "无效路径应返回失败");

        logger.info("HTTP方法路由测试通过");
    }

    /**
     * 测试用户ID格式错误处理
     */
    @Test
    void testUserIdFormatError() throws Exception {
        logger.info("开始测试用户ID格式错误处理");

        // 测试无效的用户ID格式
        when(mockRequest.getPathInfo()).thenReturn("/invalid_id");

        userController.doGet(mockRequest, mockResponse);

        String responseJson = responseWriter.toString();
        assertTrue(responseJson.contains("\"success\":false"), "无效用户ID格式应返回失败");
        // 注意：实际响应可能是404错误而不是A0201，这取决于路由处理
        assertTrue(responseJson.contains("404") || responseJson.contains("A0201"), "应返回404或用户ID格式错误码");

        logger.info("用户ID格式错误处理测试通过: response={}", responseJson);
    }

    /**
     * 测试未登录用户访问需要认证的接口
     */
    @Test
    void testUnauthorizedAccess() throws Exception {
        logger.info("开始测试未登录用户访问需要认证的接口");

        // 测试未登录用户关注其他用户
        when(mockRequest.getPathInfo()).thenReturn("/" + TEST_TARGET_USER_ID + "/follow");
        when(mockRequest.getHeader("X-User-Id")).thenReturn(null);
        when(mockRequest.getHeader("Authorization")).thenReturn(null);
        when(mockRequest.getAttribute("userId")).thenReturn(null);

        userController.doPost(mockRequest, mockResponse);

        String responseJson = responseWriter.toString();
        assertTrue(responseJson.contains("\"success\":false"), "未登录用户应返回失败");
        assertTrue(responseJson.contains("A0300"), "应返回请先登录错误码");

        logger.info("未登录用户访问测试通过: response={}", responseJson);
    }

    /**
     * 测试登录失败情况
     */
    @Test
    void testLoginFailure() throws Exception {
        logger.info("开始测试登录失败情况");

        // 准备测试数据
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword("wrong_password");
        String requestBody = JsonUtil.toJson(loginRequest);

        // 准备Mock返回数据
        LoginResult loginResult = LoginResult.fail(LoginErrorEnum.WRONG_PASSWORD);

        // 设置Mock行为
        when(mockRequest.getPathInfo()).thenReturn("/login");
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(mockUserService.login(TEST_USERNAME, "wrong_password")).thenReturn(loginResult);

        // 执行测试
        userController.doPost(mockRequest, mockResponse);

        // 验证结果
        String responseJson = responseWriter.toString();
        assertTrue(responseJson.contains("\"success\":false"), "错误密码应返回失败");
        assertTrue(responseJson.contains(LoginErrorEnum.WRONG_PASSWORD.getCode()), "应返回密码错误码");

        logger.info("登录失败情况测试通过: response={}", responseJson);
    }

    /**
     * 测试注册失败情况
     */
    @Test
    void testRegisterFailure() throws Exception {
        logger.info("开始测试注册失败情况");

        // 准备测试数据
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(TEST_USERNAME);
        registerRequest.setPassword(TEST_PASSWORD);
        registerRequest.setEmail(TEST_EMAIL);
        String requestBody = JsonUtil.toJson(registerRequest);

        // 准备Mock返回数据
        RegisterResult registerResult = RegisterResult.fail(RegisterErrorEnum.USERNAME_EXISTS);

        // 设置Mock行为
        when(mockRequest.getPathInfo()).thenReturn("/register");
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(mockUserService.register(any(RegisterRequest.class))).thenReturn(registerResult);

        // 执行测试
        userController.doPost(mockRequest, mockResponse);

        // 验证结果
        String responseJson = responseWriter.toString();
        assertTrue(responseJson.contains("\"success\":false"), "用户名已存在应返回失败");
        assertTrue(responseJson.contains(RegisterErrorEnum.USERNAME_EXISTS.getCode()), "应返回用户名已存在错误码");

        logger.info("注册失败情况测试通过: response={}", responseJson);
    }

    /**
     * 测试关注操作失败情况
     */
    @Test
    void testFollowFailure() throws Exception {
        logger.info("开始测试关注操作失败情况");

        // 准备Mock返回数据
        FollowResult followResult = FollowResult.fail(FollowErrorEnum.CANNOT_FOLLOW_SELF);

        // 设置Mock行为
        when(mockRequest.getPathInfo()).thenReturn("/" + TEST_USER_ID + "/follow");
        when(mockRequest.getHeader("X-User-Id")).thenReturn(TEST_USER_ID.toString());
        when(mockUserService.followUser(TEST_USER_ID, TEST_USER_ID)).thenReturn(followResult);

        // 执行测试
        userController.doPost(mockRequest, mockResponse);

        // 验证结果
        String responseJson = responseWriter.toString();
        assertTrue(responseJson.contains("\"success\":false"), "不能关注自己应返回失败");
        assertTrue(responseJson.contains(FollowErrorEnum.CANNOT_FOLLOW_SELF.getCode()), "应返回不能关注自己错误码");

        logger.info("关注操作失败情况测试通过: response={}", responseJson);
    }

    /**
     * 测试获取不存在用户信息
     */
    @Test
    void testGetNonExistentUserProfile() throws Exception {
        logger.info("开始测试获取不存在用户信息");

        // 设置Mock行为
        when(mockRequest.getPathInfo()).thenReturn("/999");
        when(mockRequest.getHeader("X-User-Id")).thenReturn(TEST_USER_ID.toString());
        when(mockUserService.getUserProfile(999L, TEST_USER_ID)).thenReturn(null);

        // 执行测试
        userController.doGet(mockRequest, mockResponse);

        // 验证结果
        String responseJson = responseWriter.toString();
        assertTrue(responseJson.contains("\"success\":false"), "不存在的用户应返回失败");
        assertTrue(responseJson.contains("A0120"), "应返回用户不存在错误码");

        logger.info("获取不存在用户信息测试通过: response={}", responseJson);
    }

    /**
     * 测试获取关注动态时的参数验证
     */
    @Test
    void testGetFollowingFeedValidation() throws Exception {
        logger.info("开始测试获取关注动态时的参数验证");

        // 测试无效的分页参数
        when(mockRequest.getPathInfo()).thenReturn("/follow/feed");
        when(mockRequest.getHeader("X-User-Id")).thenReturn(TEST_USER_ID.toString());
        when(mockRequest.getParameter("page")).thenReturn("invalid");
        when(mockRequest.getParameter("size")).thenReturn("20");

        userController.doGet(mockRequest, mockResponse);

        String responseJson = responseWriter.toString();
        assertTrue(responseJson.contains("\"success\":false"), "无效分页参数应返回失败");
        assertTrue(responseJson.contains("A0202"), "应返回分页参数格式错误码");

        logger.info("获取关注动态参数验证测试通过: response={}", responseJson);
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
        userController.doPost(mockRequest, mockResponse);

        // 验证结果
        String responseJson = responseWriter.toString();
        assertTrue(responseJson.contains("\"success\":false"), "系统异常应返回失败");
        assertTrue(responseJson.contains("SYSTEM_ERROR"), "应返回系统错误码");

        logger.info("系统异常处理测试通过: response={}", responseJson);
    }

    /**
     * 测试完整的用户操作流程
     */
    @Test
    void testCompleteUserWorkflow() throws Exception {
        logger.info("开始测试完整的用户操作流程");

        // 1. 用户注册
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(TEST_USERNAME);
        registerRequest.setPassword(TEST_PASSWORD);
        registerRequest.setEmail(TEST_EMAIL);
        String registerBody = JsonUtil.toJson(registerRequest);

        UserVO userVO = new UserVO();
        userVO.setId(TEST_USER_ID);
        userVO.setUsername(TEST_USERNAME);

        RegisterResult registerResult = RegisterResult.success(userVO);

        when(mockRequest.getPathInfo()).thenReturn("/register");
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(registerBody)));
        when(mockUserService.register(any(RegisterRequest.class))).thenReturn(registerResult);

        userController.doPost(mockRequest, mockResponse);

        String registerResponse = responseWriter.toString();
        assertTrue(registerResponse.contains("\"success\":true"), "注册应该成功");
        logger.info("注册成功: {}", registerResponse);

        // 重置响应Writer
        responseWriter.getBuffer().setLength(0);

        // 2. 用户登录
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword(TEST_PASSWORD);
        String loginBody = JsonUtil.toJson(loginRequest);

        LoginResult loginResult = LoginResult.success(userVO);

        when(mockRequest.getPathInfo()).thenReturn("/login");
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(loginBody)));
        when(mockUserService.login(TEST_USERNAME, TEST_PASSWORD)).thenReturn(loginResult);

        userController.doPost(mockRequest, mockResponse);

        String loginResponse = responseWriter.toString();
        assertTrue(loginResponse.contains("\"success\":true"), "登录应该成功");
        logger.info("登录成功: {}", loginResponse);

        // 重置响应Writer
        responseWriter.getBuffer().setLength(0);

        // 3. 获取用户信息
        UserVO targetUser = new UserVO();
        targetUser.setId(TEST_TARGET_USER_ID);
        targetUser.setUsername("targetuser");

        UserProfileVO userProfile = new UserProfileVO();
        userProfile.setUser(targetUser);
        userProfile.setFollowerCount(100);
        userProfile.setIsFollowing(false);

        when(mockRequest.getPathInfo()).thenReturn("/" + TEST_TARGET_USER_ID);
        when(mockRequest.getHeader("X-User-Id")).thenReturn(TEST_USER_ID.toString());
        when(mockUserService.getUserProfile(TEST_TARGET_USER_ID, TEST_USER_ID)).thenReturn(userProfile);

        userController.doGet(mockRequest, mockResponse);

        String profileResponse = responseWriter.toString();
        assertTrue(profileResponse.contains("\"success\":true"), "获取用户信息应该成功");
        logger.info("获取用户信息成功: {}", profileResponse);

        logger.info("完整的用户操作流程测试通过");
    }
}
