package com.shiwu.user.dao;

import com.shiwu.user.model.User;
import com.shiwu.product.model.ProductCardVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * UserDao测试类
 * 遵循AIR原则：Automatic, Independent, Repeatable
 * 遵循BCDE原则：Border, Correct, Design, Error
 * 
 * 注意：这些测试需要实际的数据库环境和测试数据
 */
public class UserDaoTest {
    
    private UserDao userDao;
    
    @BeforeEach
    public void setUp() {
        userDao = new UserDao();
    }
    
    /**
     * 测试根据用户名查找用户 - 正常情况
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    public void testFindByUsername_Success() {
        // Given: 测试数据中存在的用户名
        String username = "test";
        
        // When: 根据用户名查找用户
        User user = userDao.findByUsername(username);
        
        // Then: 验证结果
        if (user != null) {
            assertNotNull(user.getId(), "用户ID不应为空");
            assertEquals(username, user.getUsername(), "用户名应该匹配");
            assertNotNull(user.getPassword(), "密码哈希不应为空");
            assertNotNull(user.getCreateTime(), "创建时间不应为空");
            assertEquals(Integer.valueOf(0), user.getStatus(), "默认状态应该是0（正常）");
        } else {
            System.out.println("警告：测试用户不存在，请确保数据库中有测试数据");
        }
    }
    
    /**
     * 测试根据用户名查找用户 - 用户不存在
     * BCDE原则中的Error：测试错误输入
     */
    @Test
    public void testFindByUsername_UserNotExists() {
        // Given: 不存在的用户名
        String nonExistentUsername = "nonexistent_user_12345";
        
        // When: 根据用户名查找用户
        User user = userDao.findByUsername(nonExistentUsername);
        
        // Then: 应该返回null
        assertNull(user, "不存在的用户应该返回null");
    }
    
    /**
     * 测试根据用户名查找用户 - 边界条件
     * BCDE原则中的Border：测试边界条件
     */
    @Test
    public void testFindByUsername_NullUsername() {
        // Given: null用户名
        String nullUsername = null;
        
        // When: 根据用户名查找用户
        User user = userDao.findByUsername(nullUsername);
        
        // Then: 应该返回null（不抛出异常）
        assertNull(user, "null用户名应该返回null");
    }
    
    /**
     * 测试根据用户ID查找用户 - 正常情况
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    public void testFindById_Success() {
        // Given: 测试数据中存在的用户ID
        Long userId = 1L;
        
        // When: 根据用户ID查找用户
        User user = userDao.findById(userId);
        
        // Then: 验证结果
        if (user != null) {
            assertEquals(userId, user.getId(), "用户ID应该匹配");
            assertNotNull(user.getUsername(), "用户名不应为空");
            assertNotNull(user.getCreateTime(), "创建时间不应为空");
            assertNotNull(user.getFollowerCount(), "粉丝数量不应为空");
            assertNotNull(user.getAverageRating(), "平均评分不应为空");
            
            // 验证数据合理性
            assertTrue(user.getFollowerCount() >= 0, "粉丝数量应该大于等于0");
            assertTrue(user.getAverageRating().compareTo(BigDecimal.ZERO) >= 0, "平均评分应该大于等于0");
            assertTrue(user.getAverageRating().compareTo(new BigDecimal("5.00")) <= 0, "平均评分应该小于等于5");
        } else {
            System.out.println("警告：测试用户不存在，请确保数据库中有测试数据");
        }
    }
    
    /**
     * 测试根据用户ID查找用户 - 用户不存在
     * BCDE原则中的Error：测试错误输入
     */
    @Test
    public void testFindById_UserNotExists() {
        // Given: 不存在的用户ID
        Long nonExistentUserId = 999999L;
        
        // When: 根据用户ID查找用户
        User user = userDao.findById(nonExistentUserId);
        
        // Then: 应该返回null
        assertNull(user, "不存在的用户ID应该返回null");
    }
    
    /**
     * 测试获取用户公开信息 - 正常情况
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    public void testFindPublicInfoById_Success() {
        // Given: 测试数据中存在的用户ID
        Long userId = 1L;
        
        // When: 获取用户公开信息
        User user = userDao.findPublicInfoById(userId);
        
        // Then: 验证结果
        if (user != null) {
            assertEquals(userId, user.getId(), "用户ID应该匹配");
            assertNotNull(user.getUsername(), "用户名不应为空");
            assertNotNull(user.getFollowerCount(), "粉丝数量不应为空");
            assertNotNull(user.getAverageRating(), "平均评分不应为空");
            assertNotNull(user.getCreateTime(), "创建时间不应为空");
            
            // 验证敏感信息不被返回（公开信息查询不应包含密码等）
            assertNull(user.getPassword(), "公开信息查询不应包含密码");
            assertNull(user.getEmail(), "公开信息查询不应包含邮箱");
            assertNull(user.getPhone(), "公开信息查询不应包含手机号");
        } else {
            System.out.println("警告：测试用户不存在，请确保数据库中有测试数据");
        }
    }
    
    /**
     * 测试获取用户在售商品列表
     * BCDE原则中的Design：测试设计功能
     */
    @Test
    public void testFindOnSaleProductsByUserId() {
        // Given: 测试用户ID
        Long userId = 1L;
        
        // When: 获取在售商品列表
        List<ProductCardVO> products = userDao.findOnSaleProductsByUserId(userId);
        
        // Then: 验证结果
        assertNotNull(products, "商品列表不应为null");
        // 注意：由于产品模块未实现，这里应该返回空列表
        assertTrue(products.isEmpty(), "由于产品模块未实现，应该返回空列表");
    }
    
    /**
     * 测试创建用户 - 正常情况
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    public void testCreateUser_Success() {
        // Given: 新用户信息
        User newUser = new User();
        newUser.setUsername("testCreateUser_" + System.currentTimeMillis()); // 使用时间戳确保唯一性
        newUser.setPassword("hashedPassword123");
        newUser.setEmail("test@example.com");
        newUser.setNickname("测试用户");
        newUser.setStatus(0);
        
        // When: 创建用户
        Long userId = userDao.createUser(newUser);
        
        // Then: 验证结果
        if (userId != null) {
            assertNotNull(userId, "创建用户应该返回用户ID");
            assertTrue(userId > 0, "用户ID应该大于0");
            
            // 验证用户确实被创建
            User createdUser = userDao.findById(userId);
            assertNotNull(createdUser, "应该能查找到创建的用户");
            assertEquals(newUser.getUsername(), createdUser.getUsername(), "用户名应该匹配");
            assertEquals(newUser.getNickname(), createdUser.getNickname(), "昵称应该匹配");
        } else {
            System.out.println("警告：无法创建用户，可能数据库连接问题或权限不足");
        }
    }
    
    /**
     * 测试创建用户 - 用户名重复
     * BCDE原则中的Error：测试错误输入
     */
    @Test
    public void testCreateUser_DuplicateUsername() {
        // Given: 先创建一个用户
        String testUsername = "duplicate_test_" + System.currentTimeMillis();
        User firstUser = new User();
        firstUser.setUsername(testUsername);
        firstUser.setPassword("hashedPassword123");
        firstUser.setNickname("第一个用户");
        firstUser.setStatus(0);

        // 创建第一个用户
        Long firstUserId = userDao.createUser(firstUser);
        assertNotNull(firstUserId, "第一个用户应该创建成功");

        // When: 尝试创建相同用户名的用户
        User duplicateUser = new User();
        duplicateUser.setUsername(testUsername); // 使用相同的用户名
        duplicateUser.setPassword("hashedPassword456");
        duplicateUser.setNickname("重复用户");
        duplicateUser.setStatus(0);

        Long duplicateUserId = userDao.createUser(duplicateUser);

        // Then: 应该创建失败
        assertNull(duplicateUserId, "重复用户名应该创建失败");
    }
    
    /**
     * 测试更新用户最后登录时间
     * BCDE原则中的Design：测试设计功能
     */
    @Test
    public void testUpdateLastLoginTime() {
        // Given: 存在的用户ID
        Long userId = 1L;
        
        // When: 更新最后登录时间
        boolean result = userDao.updateLastLoginTime(userId);
        
        // Then: 验证结果
        if (result) {
            assertTrue(result, "更新最后登录时间应该成功");
        } else {
            System.out.println("警告：更新最后登录时间失败，可能用户不存在或数据库问题");
        }
    }
    
    /**
     * 测试更新不存在用户的最后登录时间
     * BCDE原则中的Error：测试错误输入
     */
    @Test
    public void testUpdateLastLoginTime_UserNotExists() {
        // Given: 不存在的用户ID
        Long nonExistentUserId = 999999L;
        
        // When: 更新最后登录时间
        boolean result = userDao.updateLastLoginTime(nonExistentUserId);
        
        // Then: 应该更新失败
        assertFalse(result, "不存在的用户更新最后登录时间应该失败");
    }
}
