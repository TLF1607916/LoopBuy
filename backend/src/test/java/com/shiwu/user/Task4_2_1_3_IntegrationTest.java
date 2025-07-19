package com.shiwu.user;

import com.shiwu.common.result.Result;
import com.shiwu.user.dao.FeedDao;
import com.shiwu.user.service.UserService;
import com.shiwu.user.service.impl.UserServiceImpl;
import com.shiwu.user.vo.FeedItemVO;
import com.shiwu.user.vo.FeedResponseVO;
import com.shiwu.user.vo.PaginationVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Task4_2_1_3 集成测试：获取关注动态信息流API
 * 
 * 测试用户关注的卖家发布商品时的动态信息流功能
 * 
 * @author LoopBuy Team
 * @version 1.0
 * @since 2024-01-15
 */
@DisplayName("Task4_2_1_3: 获取关注动态信息流API集成测试")
public class Task4_2_1_3_IntegrationTest {
    
    private UserService userService;
    private FeedDao feedDao;
    
    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl();
        feedDao = new FeedDao();
    }
    
    @Test
    @DisplayName("核心功能1: 获取关注动态信息流成功")
    void testGetFollowingFeed_Success() {
        // Given: 准备测试数据
        Long userId = 1L; // 假设用户1关注了一些卖家
        int page = 1;
        int size = 10;
        String type = "ALL";
        
        // When: 调用获取关注动态API
        Result<FeedResponseVO> result = userService.getFollowingFeed(userId, page, size, type);
        
        // Then: 验证结果
        assertNotNull(result, "结果不应为空");
        assertTrue(result.isSuccess(), "应该获取成功");
        
        FeedResponseVO response = result.getData();
        assertNotNull(response, "响应数据不应为空");
        assertNotNull(response.getFeeds(), "动态列表不应为空");
        assertNotNull(response.getPagination(), "分页信息不应为空");
        
        // 验证分页信息
        PaginationVO pagination = response.getPagination();
        assertEquals(page, pagination.getPage(), "页码应该匹配");
        assertEquals(size, pagination.getSize(), "每页大小应该匹配");
        assertTrue(pagination.getTotal() >= 0, "总数应该大于等于0");
        
        System.out.println("✅ 获取关注动态成功，总数: " + pagination.getTotal());
    }
    
    @Test
    @DisplayName("核心功能2: 按动态类型过滤")
    void testGetFollowingFeed_FilterByType() {
        // Given: 准备测试数据
        Long userId = 1L;
        int page = 1;
        int size = 10;
        
        // When & Then: 测试不同的动态类型过滤
        String[] types = {"ALL", "PRODUCT_APPROVED", "PRODUCT_PUBLISHED"};
        
        for (String type : types) {
            Result<FeedResponseVO> result = userService.getFollowingFeed(userId, page, size, type);
            
            assertNotNull(result, "结果不应为空 - type: " + type);
            assertTrue(result.isSuccess(), "应该获取成功 - type: " + type);
            
            FeedResponseVO response = result.getData();
            assertNotNull(response, "响应数据不应为空 - type: " + type);
            
            // 验证动态类型过滤
            List<FeedItemVO> feeds = response.getFeeds();
            if (!feeds.isEmpty() && !"ALL".equals(type)) {
                for (FeedItemVO feed : feeds) {
                    assertEquals(type, feed.getType(), "动态类型应该匹配过滤条件");
                }
            }
            
            System.out.println("✅ 动态类型过滤测试通过 - type: " + type + ", count: " + feeds.size());
        }
    }
    
    @Test
    @DisplayName("核心功能3: 分页功能测试")
    void testGetFollowingFeed_Pagination() {
        // Given: 准备测试数据
        Long userId = 1L;
        String type = "ALL";
        
        // When: 测试第一页
        Result<FeedResponseVO> page1Result = userService.getFollowingFeed(userId, 1, 5, type);
        
        // Then: 验证第一页结果
        assertTrue(page1Result.isSuccess(), "第一页应该获取成功");
        FeedResponseVO page1Response = page1Result.getData();
        PaginationVO page1Pagination = page1Response.getPagination();
        
        assertEquals(1, page1Pagination.getPage(), "第一页页码应该为1");
        assertEquals(5, page1Pagination.getSize(), "每页大小应该为5");
        
        // 如果有数据，测试第二页
        if (page1Pagination.getTotal() > 5) {
            Result<FeedResponseVO> page2Result = userService.getFollowingFeed(userId, 2, 5, type);
            assertTrue(page2Result.isSuccess(), "第二页应该获取成功");
            
            FeedResponseVO page2Response = page2Result.getData();
            PaginationVO page2Pagination = page2Response.getPagination();
            
            assertEquals(2, page2Pagination.getPage(), "第二页页码应该为2");
            assertEquals(5, page2Pagination.getSize(), "每页大小应该为5");
            assertEquals(page1Pagination.getTotal(), page2Pagination.getTotal(), "总数应该一致");
            
            System.out.println("✅ 分页功能测试通过");
        } else {
            System.out.println("✅ 分页功能测试通过（数据不足，跳过第二页测试）");
        }
    }
    
    @Test
    @DisplayName("边界测试1: 参数验证")
    void testGetFollowingFeed_ParameterValidation() {
        // Test 1: 用户ID为空
        Result<FeedResponseVO> result1 = userService.getFollowingFeed(null, 1, 10, "ALL");
        assertFalse(result1.isSuccess(), "用户ID为空应该失败");
        assertEquals("用户ID不能为空", result1.getMessage());
        
        // Test 2: 页码无效
        Result<FeedResponseVO> result2 = userService.getFollowingFeed(1L, 0, 10, "ALL");
        assertFalse(result2.isSuccess(), "页码为0应该失败");
        assertEquals("页码必须大于0", result2.getMessage());
        
        // Test 3: 每页大小无效
        Result<FeedResponseVO> result3 = userService.getFollowingFeed(1L, 1, 0, "ALL");
        assertFalse(result3.isSuccess(), "每页大小为0应该失败");
        assertEquals("每页大小必须在1-100之间", result3.getMessage());
        
        Result<FeedResponseVO> result4 = userService.getFollowingFeed(1L, 1, 101, "ALL");
        assertFalse(result4.isSuccess(), "每页大小超过100应该失败");
        assertEquals("每页大小必须在1-100之间", result4.getMessage());
        
        // Test 4: 动态类型无效
        Result<FeedResponseVO> result5 = userService.getFollowingFeed(1L, 1, 10, "INVALID_TYPE");
        assertFalse(result5.isSuccess(), "无效动态类型应该失败");
        assertEquals("动态类型无效", result5.getMessage());
        
        System.out.println("✅ 参数验证测试通过");
    }
    
    @Test
    @DisplayName("边界测试2: 用户无关注动态")
    void testGetFollowingFeed_NoFollowing() {
        // Given: 一个没有关注任何人的用户ID
        Long userId = 999L; // 假设这个用户不存在或没有关注任何人
        
        // When: 获取关注动态
        Result<FeedResponseVO> result = userService.getFollowingFeed(userId, 1, 10, "ALL");
        
        // Then: 应该成功但返回空列表
        assertTrue(result.isSuccess(), "应该获取成功");
        
        FeedResponseVO response = result.getData();
        assertNotNull(response, "响应数据不应为空");
        assertNotNull(response.getFeeds(), "动态列表不应为空");
        assertTrue(response.getFeeds().isEmpty(), "动态列表应该为空");
        
        PaginationVO pagination = response.getPagination();
        assertEquals(0, pagination.getTotal(), "总数应该为0");
        
        System.out.println("✅ 无关注动态测试通过");
    }
    
    @Test
    @DisplayName("数据格式验证: 动态项字段完整性")
    void testGetFollowingFeed_DataFormat() {
        // Given: 准备测试数据
        Long userId = 1L;
        
        // When: 获取关注动态
        Result<FeedResponseVO> result = userService.getFollowingFeed(userId, 1, 5, "ALL");
        
        // Then: 验证数据格式
        assertTrue(result.isSuccess(), "应该获取成功");
        
        FeedResponseVO response = result.getData();
        List<FeedItemVO> feeds = response.getFeeds();
        
        for (FeedItemVO feed : feeds) {
            // 验证必需字段
            assertNotNull(feed.getId(), "动态ID不应为空");
            assertNotNull(feed.getType(), "动态类型不应为空");
            assertNotNull(feed.getTitle(), "动态标题不应为空");
            assertNotNull(feed.getContent(), "动态内容不应为空");
            assertNotNull(feed.getSellerId(), "卖家ID不应为空");
            assertNotNull(feed.getSellerName(), "卖家名称不应为空");
            assertNotNull(feed.getProductId(), "商品ID不应为空");
            assertNotNull(feed.getProductTitle(), "商品标题不应为空");
            assertNotNull(feed.getCreateTime(), "创建时间不应为空");
            
            // 验证动态类型
            assertTrue(feed.getType().equals("PRODUCT_APPROVED") || 
                      feed.getType().equals("PRODUCT_PUBLISHED"), 
                      "动态类型应该是有效值");
            
            System.out.println("✅ 动态项验证通过: " + feed.getTitle());
        }
        
        System.out.println("✅ 数据格式验证测试通过，验证了 " + feeds.size() + " 个动态项");
    }
}
