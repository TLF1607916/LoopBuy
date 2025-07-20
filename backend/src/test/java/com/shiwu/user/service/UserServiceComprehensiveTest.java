package com.shiwu.user.service;

import com.shiwu.common.result.Result;
import com.shiwu.common.test.TestConfig;
import com.shiwu.user.model.*;
import com.shiwu.user.service.impl.UserServiceImpl;
import com.shiwu.user.vo.FeedResponseVO;
import com.shiwu.test.TestBase;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserService 综合测试类
 * 测试用户服务的所有核心功能
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("UserService 综合测试")
public class UserServiceComprehensiveTest extends TestBase {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceComprehensiveTest.class);
    
    private UserService userService;
    
    // 测试数据
    private static final String TEST_USERNAME = "test_user_service_" + System.currentTimeMillis();
    private static final String TEST_PASSWORD = "TestPassword123";
    private static final String TEST_EMAIL = "test_service_" + System.currentTimeMillis() + "@example.com";
    private static final String TEST_PHONE = "138" + String.format("%08d", System.currentTimeMillis() % 100000000);
    private static final String TEST_NICKNAME = "测试用户Service";
    
    @BeforeEach
    public void setUp() {
        super.setUp(); // 调用父类的setUp方法
        userService = new UserServiceImpl();
        logger.info("UserService测试环境初始化完成");
    }

    @Test
    @Order(1)
    @DisplayName("1.1 用户注册功能测试")
    public void testUserRegister() {
        logger.info("开始测试用户注册功能");
        
        // 测试正常注册
        RegisterRequest request = new RegisterRequest();
        request.setUsername(TEST_USERNAME);
        request.setPassword(TEST_PASSWORD);
        request.setEmail(TEST_EMAIL);
        request.setPhone(TEST_PHONE);
        request.setNickname(TEST_NICKNAME);
        
        RegisterResult result = userService.register(request);
        assertNotNull(result, "注册结果不应为空");
        assertTrue(result.getSuccess(), "注册应该成功");
        assertNotNull(result.getUserVO(), "注册成功应返回用户信息");
        assertNotNull(result.getUserVO().getId(), "注册成功应返回用户ID");

        logger.info("用户注册测试通过: userId={}", result.getUserVO().getId());
    }

    @Test
    @Order(2)
    @DisplayName("1.2 用户注册参数验证测试")
    public void testUserRegisterValidation() {
        logger.info("开始测试用户注册参数验证");
        
        // 测试null请求
        RegisterResult result1 = userService.register(null);
        assertNotNull(result1, "注册结果不应为空");
        assertFalse(result1.getSuccess(), "null请求应该失败");

        // 测试空用户名
        RegisterRequest request2 = new RegisterRequest();
        request2.setUsername("");
        request2.setPassword(TEST_PASSWORD);
        request2.setEmail(TEST_EMAIL);

        RegisterResult result2 = userService.register(request2);
        assertFalse(result2.getSuccess(), "空用户名应该失败");

        // 测试空密码
        RegisterRequest request3 = new RegisterRequest();
        request3.setUsername("test_user_2");
        request3.setPassword("");
        request3.setEmail(TEST_EMAIL);

        RegisterResult result3 = userService.register(request3);
        assertFalse(result3.getSuccess(), "空密码应该失败");
        
        logger.info("用户注册参数验证测试通过");
    }

    @Test
    @Order(3)
    @DisplayName("1.3 用户登录功能测试")
    public void testUserLogin() {
        logger.info("开始测试用户登录功能");
        
        // 先注册一个用户
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("test_login_user_" + System.currentTimeMillis());
        registerRequest.setPassword(TEST_PASSWORD);
        registerRequest.setEmail("login_" + System.currentTimeMillis() + "@example.com");
        
        RegisterResult registerResult = userService.register(registerRequest);
        assertTrue(registerResult.getSuccess(), "注册应该成功");

        // 测试正确登录
        LoginResult loginResult = userService.login(registerRequest.getUsername(), TEST_PASSWORD);
        assertNotNull(loginResult, "登录结果不应为空");
        assertTrue(loginResult.getSuccess(), "登录应该成功");
        assertNotNull(loginResult.getUserVO(), "登录成功应返回用户信息");
        assertNotNull(loginResult.getUserVO().getToken(), "登录成功应返回token");
        assertNotNull(loginResult.getUserVO().getId(), "登录成功应返回用户ID");

        logger.info("用户登录测试通过: userId={}", loginResult.getUserVO().getId());
    }

    @Test
    @Order(4)
    @DisplayName("1.4 用户登录参数验证测试")
    public void testUserLoginValidation() {
        logger.info("开始测试用户登录参数验证");
        
        // 测试null用户名
        LoginResult result1 = userService.login(null, TEST_PASSWORD);
        assertNotNull(result1, "登录结果不应为空");
        assertFalse(result1.getSuccess(), "null用户名应该失败");

        // 测试null密码
        LoginResult result2 = userService.login(TEST_USERNAME, null);
        assertFalse(result2.getSuccess(), "null密码应该失败");

        // 测试错误密码
        LoginResult result3 = userService.login(TEST_USERNAME, "wrong_password");
        assertFalse(result3.getSuccess(), "错误密码应该失败");

        // 测试不存在的用户
        LoginResult result4 = userService.login("nonexistent_user", TEST_PASSWORD);
        assertFalse(result4.getSuccess(), "不存在的用户应该失败");
        
        logger.info("用户登录参数验证测试通过");
    }

    @Test
    @Order(5)
    @DisplayName("1.5 获取用户资料测试")
    public void testGetUserProfile() {
        logger.info("开始测试获取用户资料功能");
        
        // 先注册一个用户
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("test_profile_user_" + System.currentTimeMillis());
        registerRequest.setPassword(TEST_PASSWORD);
        registerRequest.setEmail("profile_" + System.currentTimeMillis() + "@example.com");
        registerRequest.setNickname("资料测试用户");
        
        RegisterResult registerResult = userService.register(registerRequest);
        assertTrue(registerResult.getSuccess(), "注册应该成功");
        Long userId = registerResult.getUserVO().getId();
        
        // 测试获取用户资料
        UserProfileVO profile = userService.getUserProfile(userId, null);
        assertNotNull(profile, "用户资料不应为空");
        assertNotNull(profile.getUser(), "用户信息不应为空");
        assertEquals(registerRequest.getUsername(), profile.getUser().getUsername(), "用户名应该匹配");
        assertEquals("资料测试用户", profile.getUser().getNickname(), "昵称应该匹配");
        
        logger.info("获取用户资料测试通过: userId={}", userId);
    }

    @Test
    @Order(6)
    @DisplayName("1.6 获取用户资料参数验证测试")
    public void testGetUserProfileValidation() {
        logger.info("开始测试获取用户资料参数验证");
        
        // 测试null用户ID
        UserProfileVO profile1 = userService.getUserProfile(null, null);
        assertNull(profile1, "null用户ID应该返回null");
        
        // 测试不存在的用户ID
        UserProfileVO profile2 = userService.getUserProfile(TestConfig.BOUNDARY_ID_NONEXISTENT, null);
        assertNull(profile2, "不存在的用户ID应该返回null");
        
        logger.info("获取用户资料参数验证测试通过");
    }

    @Test
    @Order(7)
    @DisplayName("1.7 获取用户资料增强版测试")
    public void testGetUserProfileWithErrorHandling() {
        logger.info("开始测试获取用户资料增强版功能");
        
        // 先注册一个用户
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("test_profile_enhanced_" + System.currentTimeMillis());
        registerRequest.setPassword(TEST_PASSWORD);
        registerRequest.setEmail("enhanced_" + System.currentTimeMillis() + "@example.com");
        
        RegisterResult registerResult = userService.register(registerRequest);
        assertTrue(registerResult.getSuccess(), "注册应该成功");
        Long userId = registerResult.getUserVO().getId();
        
        // 测试获取用户资料增强版
        UserProfileResult result = userService.getUserProfileWithErrorHandling(userId, null);
        assertNotNull(result, "用户资料结果不应为空");
        assertTrue(result.getSuccess(), "获取用户资料应该成功");
        assertNotNull(result.getUserProfile(), "用户资料不应为空");

        // 测试不存在的用户
        UserProfileResult result2 = userService.getUserProfileWithErrorHandling(TestConfig.BOUNDARY_ID_NONEXISTENT, null);
        assertNotNull(result2, "结果不应为空");
        assertFalse(result2.getSuccess(), "不存在的用户应该失败");
        
        logger.info("获取用户资料增强版测试通过");
    }

    @Test
    @Order(8)
    @DisplayName("1.8 关注功能测试")
    public void testFollowUser() {
        logger.info("开始测试关注功能");
        
        // 先注册两个用户
        long timestamp = System.currentTimeMillis();
        RegisterRequest request1 = new RegisterRequest();
        request1.setUsername("follower_user_" + timestamp);
        request1.setPassword(TEST_PASSWORD);
        request1.setEmail("follower_" + timestamp + "@example.com");

        RegisterRequest request2 = new RegisterRequest();
        request2.setUsername("followed_user_" + timestamp);
        request2.setPassword(TEST_PASSWORD);
        request2.setEmail("followed_" + timestamp + "@example.com");
        
        RegisterResult result1 = userService.register(request1);
        RegisterResult result2 = userService.register(request2);

        assertTrue(result1.getSuccess() && result2.getSuccess(), "注册应该成功");

        Long followerId = result1.getUserVO().getId();
        Long followedId = result2.getUserVO().getId();

        // 测试关注用户
        FollowResult followResult = userService.followUser(followerId, followedId);
        assertNotNull(followResult, "关注结果不应为空");
        assertTrue(followResult.getSuccess(), "关注应该成功");
        
        logger.info("关注功能测试通过: followerId={}, followedId={}", followerId, followedId);
    }

    @Test
    @Order(9)
    @DisplayName("1.9 关注功能参数验证测试")
    public void testFollowUserValidation() {
        logger.info("开始测试关注功能参数验证");
        
        // 测试null参数
        FollowResult result1 = userService.followUser(null, TestConfig.TEST_USER_ID);
        assertNotNull(result1, "关注结果不应为空");
        assertFalse(result1.getSuccess(), "null关注者ID应该失败");

        FollowResult result2 = userService.followUser(TestConfig.TEST_USER_ID, null);
        assertFalse(result2.getSuccess(), "null被关注者ID应该失败");

        // 测试自己关注自己
        FollowResult result3 = userService.followUser(TestConfig.TEST_USER_ID, TestConfig.TEST_USER_ID);
        assertFalse(result3.getSuccess(), "不能关注自己");
        
        logger.info("关注功能参数验证测试通过");
    }

    @Test
    @Order(10)
    @DisplayName("1.10 取关功能测试")
    public void testUnfollowUser() {
        logger.info("开始测试取关功能");
        
        // 先注册两个用户并建立关注关系
        long timestamp = System.currentTimeMillis();
        RegisterRequest request1 = new RegisterRequest();
        request1.setUsername("unfollower_user_" + timestamp);
        request1.setPassword(TEST_PASSWORD);
        request1.setEmail("unfollower_" + timestamp + "@example.com");

        RegisterRequest request2 = new RegisterRequest();
        request2.setUsername("unfollowed_user_" + timestamp);
        request2.setPassword(TEST_PASSWORD);
        request2.setEmail("unfollowed_" + timestamp + "@example.com");
        
        RegisterResult result1 = userService.register(request1);
        RegisterResult result2 = userService.register(request2);

        Long followerId = result1.getUserVO().getId();
        Long followedId = result2.getUserVO().getId();

        // 先关注
        FollowResult followResult = userService.followUser(followerId, followedId);
        assertTrue(followResult.getSuccess(), "关注应该成功");

        // 再取关
        FollowResult unfollowResult = userService.unfollowUser(followerId, followedId);
        assertNotNull(unfollowResult, "取关结果不应为空");
        assertTrue(unfollowResult.getSuccess(), "取关应该成功");
        
        logger.info("取关功能测试通过");
    }

    @Test
    @Order(11)
    @DisplayName("1.11 获取关注状态测试")
    public void testGetFollowStatus() {
        logger.info("开始测试获取关注状态功能");
        
        // 先注册两个用户
        long timestamp = System.currentTimeMillis();
        RegisterRequest request1 = new RegisterRequest();
        request1.setUsername("status_follower_" + timestamp);
        request1.setPassword(TEST_PASSWORD);
        request1.setEmail("status_follower_" + timestamp + "@example.com");

        RegisterRequest request2 = new RegisterRequest();
        request2.setUsername("status_followed_" + timestamp);
        request2.setPassword(TEST_PASSWORD);
        request2.setEmail("status_followed_" + timestamp + "@example.com");
        
        RegisterResult result1 = userService.register(request1);
        RegisterResult result2 = userService.register(request2);

        Long followerId = result1.getUserVO().getId();
        Long followedId = result2.getUserVO().getId();

        // 测试未关注状态
        FollowStatusVO status1 = userService.getFollowStatus(followerId, followedId);
        assertNotNull(status1, "关注状态不应为空");
        assertFalse(status1.getIsFollowing(), "初始状态应该是未关注");

        // 关注后测试状态
        userService.followUser(followerId, followedId);
        FollowStatusVO status2 = userService.getFollowStatus(followerId, followedId);
        assertTrue(status2.getIsFollowing(), "关注后状态应该是已关注");
        
        logger.info("获取关注状态测试通过");
    }

    @Test
    @Order(12)
    @DisplayName("1.12 获取关注动态测试")
    public void testGetFollowingFeed() {
        logger.info("开始测试获取关注动态功能");
        
        // 先注册一个用户
        RegisterRequest request = new RegisterRequest();
        request.setUsername("feed_user_" + System.currentTimeMillis());
        request.setPassword(TEST_PASSWORD);
        request.setEmail("feed_" + System.currentTimeMillis() + "@example.com");
        
        RegisterResult result = userService.register(request);
        assertTrue(result.getSuccess(), "注册应该成功");
        Long userId = result.getUserVO().getId();

        // 测试获取关注动态
        Result<FeedResponseVO> feedResult = userService.getFollowingFeed(userId, 1, 10, "ALL");
        assertNotNull(feedResult, "动态结果不应为空");
        assertTrue(feedResult.isSuccess(), "获取动态应该成功");
        assertNotNull(feedResult.getData(), "动态数据不应为空");
        
        logger.info("获取关注动态测试通过");
    }

    @Test
    @Order(13)
    @DisplayName("1.13 更新用户平均评分测试")
    public void testUpdateUserAverageRating() {
        logger.info("开始测试更新用户平均评分功能");
        
        // 先注册一个用户
        RegisterRequest request = new RegisterRequest();
        request.setUsername("rating_user_" + System.currentTimeMillis());
        request.setPassword(TEST_PASSWORD);
        request.setEmail("rating_" + System.currentTimeMillis() + "@example.com");
        
        RegisterResult result = userService.register(request);
        assertTrue(result.getSuccess(), "注册应该成功");
        Long userId = result.getUserVO().getId();
        
        // 测试更新平均评分
        boolean updateResult = userService.updateUserAverageRating(userId);
        assertTrue(updateResult, "更新平均评分应该成功");
        
        // 测试null用户ID
        boolean updateResult2 = userService.updateUserAverageRating(null);
        assertFalse(updateResult2, "null用户ID应该失败");
        
        logger.info("更新用户平均评分测试通过");
    }

    @AfterEach
    void tearDown() {
        logger.info("UserService测试清理完成");
    }
}
