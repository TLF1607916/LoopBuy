package com.shiwu.review.controller;

import com.shiwu.common.result.Result;
import com.shiwu.common.util.JsonUtil;
import com.shiwu.review.model.ReviewCreateDTO;
import com.shiwu.review.model.ReviewErrorCode;
import com.shiwu.review.model.ReviewOperationResult;
import com.shiwu.review.model.ReviewVO;
import com.shiwu.review.service.ReviewService;
import com.shiwu.review.service.impl.ReviewServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * 评价控制器
 * 处理评价相关的HTTP请求
 * 
 * API路径设计：
 * POST /review - 提交评价
 * GET /review/product/{productId} - 获取商品评价列表
 * GET /review/user/{userId} - 获取用户评价列表
 * GET /review/check/{orderId} - 检查订单是否可评价
 */
@WebServlet("/review/*")
public class ReviewController extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);
    private final ReviewService reviewService;

    public ReviewController() {
        this.reviewService = new ReviewServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            sendErrorResponse(resp, "404", "请求路径不存在");
            return;
        }

        String[] segments = pathInfo.substring(1).split("/");
        
        if (segments.length >= 2) {
            String action = segments[0];
            String id = segments[1];
            
            try {
                Long idValue = Long.parseLong(id);
                
                switch (action) {
                    case "product":
                        handleGetProductReviews(req, resp, idValue);
                        break;
                    case "user":
                        handleGetUserReviews(req, resp, idValue);
                        break;
                    case "check":
                        handleCheckOrderCanReview(req, resp, idValue);
                        break;
                    default:
                        sendErrorResponse(resp, "404", "请求路径不存在");
                        break;
                }
            } catch (NumberFormatException e) {
                sendErrorResponse(resp, "400", "无效的ID格式");
            }
        } else {
            sendErrorResponse(resp, "404", "请求路径不存在");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // 提交评价
            handleSubmitReview(req, resp);
        } else {
            sendErrorResponse(resp, "404", "请求路径不存在");
        }
    }

    /**
     * 处理提交评价请求
     */
    private void handleSubmitReview(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 检查用户是否登录
        Long userId = getCurrentUserId(req);
        if (userId == null) {
            sendErrorResponse(resp, "401", "用户未登录");
            return;
        }

        try {
            // 读取请求体
            String requestBody = readRequestBody(req);
            ReviewCreateDTO dto = JsonUtil.fromJson(requestBody, ReviewCreateDTO.class);

            if (dto == null) {
                sendErrorResponse(resp, "400", "请求参数不能为空");
                return;
            }

            // 提交评价
            ReviewOperationResult result = reviewService.submitReview(dto, userId);
            if (result.isSuccess()) {
                sendSuccessResponse(resp, result.getData());
            } else {
                sendErrorResponse(resp, result.getErrorCode(), result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("提交评价失败", e);
            sendErrorResponse(resp, ReviewErrorCode.SYSTEM_ERROR, ReviewErrorCode.MSG_SYSTEM_ERROR);
        }
    }

    /**
     * 处理获取商品评价列表请求
     */
    private void handleGetProductReviews(HttpServletRequest req, HttpServletResponse resp, Long productId) throws IOException {
        try {
            List<ReviewVO> reviews = reviewService.getReviewsByProductId(productId);
            sendSuccessResponse(resp, reviews);
        } catch (Exception e) {
            logger.error("获取商品评价列表失败", e);
            sendErrorResponse(resp, ReviewErrorCode.SYSTEM_ERROR, ReviewErrorCode.MSG_SYSTEM_ERROR);
        }
    }

    /**
     * 处理获取用户评价列表请求
     */
    private void handleGetUserReviews(HttpServletRequest req, HttpServletResponse resp, Long userId) throws IOException {
        try {
            List<ReviewVO> reviews = reviewService.getReviewsByUserId(userId);
            sendSuccessResponse(resp, reviews);
        } catch (Exception e) {
            logger.error("获取用户评价列表失败", e);
            sendErrorResponse(resp, ReviewErrorCode.SYSTEM_ERROR, ReviewErrorCode.MSG_SYSTEM_ERROR);
        }
    }

    /**
     * 处理检查订单是否可评价请求
     */
    private void handleCheckOrderCanReview(HttpServletRequest req, HttpServletResponse resp, Long orderId) throws IOException {
        // 检查用户是否登录
        Long userId = getCurrentUserId(req);
        if (userId == null) {
            sendErrorResponse(resp, "401", "用户未登录");
            return;
        }

        try {
            ReviewOperationResult result = reviewService.checkOrderCanReview(orderId, userId);
            if (result.isSuccess()) {
                sendSuccessResponse(resp, true);
            } else {
                sendErrorResponse(resp, result.getErrorCode(), result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("检查订单是否可评价失败", e);
            sendErrorResponse(resp, ReviewErrorCode.SYSTEM_ERROR, ReviewErrorCode.MSG_SYSTEM_ERROR);
        }
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            Object userIdObj = session.getAttribute("userId");
            if (userIdObj instanceof Long) {
                return (Long) userIdObj;
            }
        }
        return null;
    }

    /**
     * 读取请求体内容
     */
    private String readRequestBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    /**
     * 发送成功响应
     */
    private void sendSuccessResponse(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        
        Result<Object> result = Result.success(data);
        String jsonResponse = JsonUtil.toJson(result);
        
        try (PrintWriter writer = resp.getWriter()) {
            writer.write(jsonResponse);
        }
    }

    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse resp, String code, String message) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        // 根据错误码设置HTTP状态码
        if ("401".equals(code)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else if ("404".equals(code)) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else if ("400".equals(code) || code.startsWith("REVIEW_")) {
            // 评价相关错误都返回400 Bad Request
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        Result<Object> result = Result.fail(code, message);
        String jsonResponse = JsonUtil.toJson(result);

        try (PrintWriter writer = resp.getWriter()) {
            writer.write(jsonResponse);
        }
    }
}
