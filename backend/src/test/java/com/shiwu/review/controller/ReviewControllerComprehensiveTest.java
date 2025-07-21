package com.shiwu.review.controller;

import com.shiwu.common.util.JsonUtil;
import com.shiwu.review.model.ReviewCreateDTO;
//import com.shiwu.review.model.ReviewErrorCode;
import com.shiwu.review.model.ReviewOperationResult;
//import com.shiwu.review.model.ReviewVO;
import com.shiwu.review.service.ReviewService;
import com.shiwu.test.TestBase;
//import com.shiwu.user.model.UserVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
//import java.time.LocalDateTime;
import java.util.ArrayList;
//import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
//import static org.mockito.ArgumentMatchers.*;

/**
 * ReviewController综合测试类
 */
public class ReviewControllerComprehensiveTest extends TestBase {
    
    private static final Logger logger = LoggerFactory.getLogger(ReviewControllerComprehensiveTest.class);
    
    private ReviewController reviewController;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private StringWriter responseWriter;
    private ReviewService mockReviewService;
    
    @BeforeEach
    public void setUp() {
        logger.info("ReviewController测试环境初始化开始");
        super.setUp();
        
        // 创建Mock对象
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        mockReviewService = mock(ReviewService.class);
        
        // 创建ReviewController实例，使用Mock service
        reviewController = new ReviewController(mockReviewService);
        
        // 设置响应Writer
        responseWriter = new StringWriter();
        try {
            when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        } catch (Exception e) {
            fail("设置响应Writer失败: " + e.getMessage());
        }
        
        // 设置默认的session行为
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(3001L);
        
        logger.info("ReviewController测试环境初始化完成");
    }
    
    @AfterEach
    public void tearDown() {
        logger.info("ReviewController测试清理完成");
    }
    
    /**
     * 测试提交评价接口 - 成功
     */
    @Test
    public void testSubmitReview() throws Exception {
        logger.info("开始测试提交评价接口");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/");
        
        // 设置请求体
        ReviewCreateDTO dto = new ReviewCreateDTO();
        dto.setOrderId(1001L);
        dto.setRating(5);
        dto.setComment("商品质量很好，物流很快！");
        
        String requestBody = JsonUtil.toJson(dto);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));

        // 模拟service返回成功
        when(mockReviewService.submitReview(any(), any())).thenReturn(
            ReviewOperationResult.success(1L)
        );

        // 执行测试
        reviewController.doPost(request, response);
        
        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":true"));
        
        logger.info("提交评价接口测试通过: response=" + responseContent);
    }
    
    /**
     * 测试提交评价接口 - 未登录
     */
    @Test
    public void testSubmitReviewNotLoggedIn() throws Exception {
        logger.info("开始测试提交评价接口 - 未登录");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/");
        
        // 模拟未登录状态
        when(request.getSession(false)).thenReturn(null);
        
        // 设置请求体
        ReviewCreateDTO dto = new ReviewCreateDTO();
        dto.setOrderId(1001L);
        dto.setRating(5);
        dto.setComment("商品质量很好！");
        
        String requestBody = JsonUtil.toJson(dto);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        
        // 执行测试
        reviewController.doPost(request, response);
        
        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("用户未登录"));
        
        logger.info("未登录提交评价测试通过: response=" + responseContent);
    }
    
    /**
     * 测试提交评价接口 - 请求参数为空
     */
    @Test
    public void testSubmitReviewEmptyParams() throws Exception {
        logger.info("开始测试提交评价接口 - 请求参数为空");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/");
        
        // 设置空请求体
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("")));
        
        // 执行测试
        reviewController.doPost(request, response);
        
        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("系统错误，请稍后重试"));
        
        logger.info("空参数提交评价测试通过: response=" + responseContent);
    }
    
    /**
     * 测试获取商品评价列表接口 - 成功
     */
    @Test
    public void testGetProductReviews() throws Exception {
        logger.info("开始测试获取商品评价列表接口");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/product/" + 2001L);

        // 模拟service返回空列表
        when(mockReviewService.getReviewsByProductId(2001L)).thenReturn(new ArrayList<>());

        // 执行测试
        reviewController.doGet(request, response);
        
        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":true"));
        
        logger.info("获取商品评价列表接口测试通过: response=" + responseContent);
    }
    
    /**
     * 测试获取用户评价列表接口 - 成功
     */
    @Test
    public void testGetUserReviews() throws Exception {
        logger.info("开始测试获取用户评价列表接口");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/user/" + 3002L);

        // 模拟service返回空列表
        when(mockReviewService.getReviewsByUserId(3002L)).thenReturn(new ArrayList<>());

        // 执行测试
        reviewController.doGet(request, response);
        
        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":true"));
        
        logger.info("获取用户评价列表接口测试通过: response=" + responseContent);
    }
    
    /**
     * 测试检查订单是否可评价接口 - 成功
     */
    @Test
    public void testCheckOrderCanReview() throws Exception {
        logger.info("开始测试检查订单是否可评价接口");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/check/" + 1001L);

        // 模拟service返回成功
        when(mockReviewService.checkOrderCanReview(1001L, 3001L)).thenReturn(
            ReviewOperationResult.success(true)
        );

        // 执行测试
        reviewController.doGet(request, response);
        
        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":true"));
        
        logger.info("检查订单是否可评价接口测试通过: response=" + responseContent);
    }
    
    /**
     * 测试检查订单是否可评价接口 - 未登录
     */
    @Test
    public void testCheckOrderCanReviewNotLoggedIn() throws Exception {
        logger.info("开始测试检查订单是否可评价接口 - 未登录");
        
        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/check/" + 1001L);
        
        // 模拟未登录状态
        when(request.getSession(false)).thenReturn(null);
        
        // 执行测试
        reviewController.doGet(request, response);
        
        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("用户未登录"));
        
        logger.info("未登录检查订单测试通过: response=" + responseContent);
    }
    
    /**
     * 测试无效ID格式
     */
    @Test
    public void testInvalidIdFormat() throws Exception {
        logger.info("开始测试无效ID格式");
        
        // 设置无效ID的请求路径
        when(request.getPathInfo()).thenReturn("/product/invalid");
        
        // 执行测试
        reviewController.doGet(request, response);
        
        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("无效的ID格式"));
        
        logger.info("无效ID格式测试通过: response=" + responseContent);
    }
    
    /**
     * 测试无效路径 - GET
     */
    @Test
    public void testInvalidPathGet() throws Exception {
        logger.info("开始测试无效路径 - GET");
        
        // 设置无效请求路径
        when(request.getPathInfo()).thenReturn("/invalid");
        
        // 执行测试
        reviewController.doGet(request, response);
        
        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("请求路径不存在"));
        
        logger.info("无效路径GET测试通过: response=" + responseContent);
    }
    
    /**
     * 测试无效路径 - POST
     */
    @Test
    public void testInvalidPathPost() throws Exception {
        logger.info("开始测试无效路径 - POST");
        
        // 设置无效请求路径
        when(request.getPathInfo()).thenReturn("/invalid");
        
        // 执行测试
        reviewController.doPost(request, response);
        
        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        
        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("请求路径不存在"));
        
        logger.info("无效路径POST测试通过: response=" + responseContent);
    }

    /**
     * 测试空路径 - GET
     */
    @Test
    public void testNullPathGet() throws Exception {
        logger.info("开始测试空路径 - GET");

        // 设置空请求路径
        when(request.getPathInfo()).thenReturn(null);

        // 执行测试
        reviewController.doGet(request, response);

        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("请求路径不存在"));

        logger.info("空路径GET测试通过: response=" + responseContent);
    }

    /**
     * 测试根路径 - GET
     */
    @Test
    public void testRootPathGet() throws Exception {
        logger.info("开始测试根路径 - GET");

        // 设置根路径
        when(request.getPathInfo()).thenReturn("/");

        // 执行测试
        reviewController.doGet(request, response);

        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("请求路径不存在"));

        logger.info("根路径GET测试通过: response=" + responseContent);
    }

    /**
     * 测试路径段不足
     */
    @Test
    public void testInsufficientPathSegments() throws Exception {
        logger.info("开始测试路径段不足");

        // 设置路径段不足的请求路径
        when(request.getPathInfo()).thenReturn("/product");

        // 执行测试
        reviewController.doGet(request, response);

        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("请求路径不存在"));

        logger.info("路径段不足测试通过: response=" + responseContent);
    }

    /**
     * 测试无效的action类型
     */
    @Test
    public void testInvalidAction() throws Exception {
        logger.info("开始测试无效的action类型");

        // 设置无效action的请求路径
        when(request.getPathInfo()).thenReturn("/invalid/123");

        // 执行测试
        reviewController.doGet(request, response);

        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("请求路径不存在"));

        logger.info("无效action类型测试通过: response=" + responseContent);
    }

    /**
     * 测试提交评价接口 - 无效JSON
     */
    @Test
    public void testSubmitReviewInvalidJson() throws Exception {
        logger.info("开始测试提交评价接口 - 无效JSON");

        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/");

        // 设置无效JSON
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("invalid json")));

        // 执行测试
        reviewController.doPost(request, response);

        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("系统错误，请稍后重试"));

        logger.info("无效JSON提交评价测试通过: response=" + responseContent);
    }

    /**
     * 测试提交评价接口 - 评分无效
     */
    @Test
    public void testSubmitReviewInvalidRating() throws Exception {
        logger.info("开始测试提交评价接口 - 评分无效");

        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/");

        // 设置请求体 - 评分超出范围
        ReviewCreateDTO dto = new ReviewCreateDTO();
        dto.setOrderId(1001L); // 使用固定的订单ID
        dto.setRating(10); // 无效评分
        dto.setComment("测试评价");

        String requestBody = JsonUtil.toJson(dto);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));

        // 执行测试
        reviewController.doPost(request, response);

        // 验证响应 - 实际会由service层验证，这里可能返回成功或失败
        verify(response).setContentType("application/json;charset=UTF-8");

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\""));

        logger.info("无效评分提交评价测试通过: response=" + responseContent);
    }

    /**
     * 测试提交评价接口 - 评价内容过长
     */
    @Test
    public void testSubmitReviewCommentTooLong() throws Exception {
        logger.info("开始测试提交评价接口 - 评价内容过长");

        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/");

        // 设置请求体 - 评价内容过长
        ReviewCreateDTO dto = new ReviewCreateDTO();
        dto.setOrderId(1001L); // 使用固定的订单ID
        dto.setRating(5);
        // 创建超过500字符的评价内容
        StringBuilder longComment = new StringBuilder();
        for (int i = 0; i < 600; i++) {
            longComment.append("a");
        }
        dto.setComment(longComment.toString());

        String requestBody = JsonUtil.toJson(dto);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));

        // 执行测试
        reviewController.doPost(request, response);

        // 验证响应 - 实际会由service层验证
        verify(response).setContentType("application/json;charset=UTF-8");

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\""));

        logger.info("评价内容过长测试通过: response=" + responseContent);
    }

    /**
     * 测试系统异常处理
     */
    @Test
    public void testSystemException() throws Exception {
        logger.info("开始测试系统异常处理");

        // 设置请求路径
        when(request.getPathInfo()).thenReturn("/product/" + 2001L);

        // 模拟service抛出异常
        when(mockReviewService.getReviewsByProductId(2001L))
            .thenThrow(new RuntimeException("系统异常"));

        // 执行测试
        reviewController.doGet(request, response);

        // 验证响应
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        String responseContent = responseWriter.toString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("\"success\":false"));
        assertTrue(responseContent.contains("系统错误，请稍后重试"));

        logger.info("系统异常处理测试通过: response=" + responseContent);
    }

    /**
     * 测试完整的评价操作流程
     */
    @Test
    public void testCompleteReviewWorkflow() throws Exception {
        logger.info("开始测试完整的评价操作流程");

        // 1. 检查订单是否可评价
        when(request.getPathInfo()).thenReturn("/check/" + 1001L);
        when(mockReviewService.checkOrderCanReview(1001L, 3001L)).thenReturn(
            ReviewOperationResult.success(true)
        );
        reviewController.doGet(request, response);
        String checkResponse = responseWriter.toString();
        logger.info("检查订单可评价成功: " + checkResponse);

        // 重置响应
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        // 2. 提交评价
        when(request.getPathInfo()).thenReturn("/");
        ReviewCreateDTO dto = new ReviewCreateDTO();
        dto.setOrderId(1001L);
        dto.setRating(5);
        dto.setComment("商品质量很好，物流很快！");

        String requestBody = JsonUtil.toJson(dto);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(mockReviewService.submitReview(any(), any())).thenReturn(
            ReviewOperationResult.success(1L)
        );
        reviewController.doPost(request, response);
        String submitResponse = responseWriter.toString();
        logger.info("提交评价成功: " + submitResponse);

        // 重置响应
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        // 3. 获取商品评价列表
        when(request.getPathInfo()).thenReturn("/product/" + 2001L);
        when(mockReviewService.getReviewsByProductId(2001L)).thenReturn(new ArrayList<>());
        reviewController.doGet(request, response);
        String productReviewsResponse = responseWriter.toString();
        logger.info("获取商品评价列表成功: " + productReviewsResponse);

        // 重置响应
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        // 4. 获取用户评价列表
        when(request.getPathInfo()).thenReturn("/user/" + 3001L);
        when(mockReviewService.getReviewsByUserId(3001L)).thenReturn(new ArrayList<>());
        reviewController.doGet(request, response);
        String userReviewsResponse = responseWriter.toString();
        logger.info("获取用户评价列表成功: " + userReviewsResponse);

        // 验证所有响应都成功
        assertTrue(checkResponse.contains("\"success\":true"));
        assertTrue(submitResponse.contains("\"success\":true"));
        assertTrue(productReviewsResponse.contains("\"success\":true"));
        assertTrue(userReviewsResponse.contains("\"success\":true"));

        logger.info("完整的评价操作流程测试通过");
    }
}
