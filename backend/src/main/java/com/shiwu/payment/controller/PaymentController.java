package com.shiwu.payment.controller;

import com.shiwu.common.result.Result;
import com.shiwu.common.util.JsonUtil;
import com.shiwu.payment.model.*;
import com.shiwu.payment.service.PaymentService;
import com.shiwu.payment.service.impl.PaymentServiceImpl;
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
import java.util.Map;

/**
 * 支付控制器
 */
@WebServlet("/api/payment/*")
public class PaymentController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;

    public PaymentController() {
        this.paymentService = new PaymentServiceImpl();
    }

    // 用于测试的构造函数
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // 获取用户支付记录列表
            handleGetUserPayments(req, resp);
        } else if (pathInfo.equals("/status")) {
            // 查询支付状态
            handleGetPaymentStatus(req, resp);
        } else if (pathInfo.equals("/page")) {
            // 跳转到模拟支付页面
            handlePaymentPage(req, resp);
        } else if (pathInfo.equals("/by-orders")) {
            // 根据订单ID获取支付信息
            handleGetPaymentByOrderIds(req, resp);
        } else {
            sendErrorResponse(resp, "404", "请求路径不存在");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // 创建支付
            handleCreatePayment(req, resp);
        } else if (pathInfo.equals("/process")) {
            // 处理支付（用户确认支付）
            handleProcessPayment(req, resp);
        } else if (pathInfo.equals("/callback")) {
            // 支付回调接口已移除，不再需要第三方支付回调
            sendErrorResponse(resp, "404", "该接口已不再支持");
        } else if (pathInfo.equals("/cancel")) {
            // 取消支付
            handleCancelPayment(req, resp);
        } else {
            sendErrorResponse(resp, "404", "请求路径不存在");
        }
    }

    /**
     * 处理创建支付请求
     */
    private void handleCreatePayment(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 检查用户是否登录
        Long userId = getCurrentUserId(req);
        if (userId == null) {
            sendErrorResponse(resp, "401", "用户未登录");
            return;
        }

        try {
            // 读取请求体
            String requestBody = readRequestBody(req);
            PaymentDTO dto = JsonUtil.fromJson(requestBody, PaymentDTO.class);

            if (dto == null) {
                sendErrorResponse(resp, "400", "请求参数不能为空");
                return;
            }

            // 创建支付
            PaymentOperationResult result = paymentService.createPayment(dto, userId);
            if (result.isSuccess()) {
                sendSuccessResponse(resp, result.getData());
            } else {
                sendErrorResponse(resp, result.getErrorCode(), result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("创建支付失败", e);
            sendErrorResponse(resp, PaymentErrorCode.SYSTEM_ERROR, PaymentErrorCode.MSG_SYSTEM_ERROR);
        }
    }

    /**
     * 处理支付处理请求（用户确认支付）
     */
    private void handleProcessPayment(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 检查用户是否登录
        Long userId = getCurrentUserId(req);
        if (userId == null) {
            sendErrorResponse(resp, "401", "用户未登录");
            return;
        }

        try {
            // 读取请求体
            String requestBody = readRequestBody(req);
            @SuppressWarnings("unchecked")
            Map<String, Object> requestMap = JsonUtil.fromJson(requestBody, Map.class);

            if (requestMap == null || !requestMap.containsKey("paymentId") || !requestMap.containsKey("paymentPassword")) {
                sendErrorResponse(resp, "400", "请求参数不完整");
                return;
            }

            String paymentId = (String) requestMap.get("paymentId");
            String paymentPassword = (String) requestMap.get("paymentPassword");

            // 处理支付
            PaymentOperationResult result = paymentService.processPayment(paymentId, paymentPassword, userId);
            if (result.isSuccess()) {
                sendSuccessResponse(resp, result.getData());
            } else {
                sendErrorResponse(resp, result.getErrorCode(), result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("处理支付失败", e);
            sendErrorResponse(resp, PaymentErrorCode.SYSTEM_ERROR, PaymentErrorCode.MSG_SYSTEM_ERROR);
        }
    }



    /**
     * 处理查询支付状态请求
     */
    private void handleGetPaymentStatus(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 检查用户是否登录
        Long userId = getCurrentUserId(req);
        if (userId == null) {
            sendErrorResponse(resp, "401", "用户未登录");
            return;
        }

        try {
            String paymentId = req.getParameter("paymentId");
            if (paymentId == null || paymentId.trim().isEmpty()) {
                sendErrorResponse(resp, "400", "支付ID不能为空");
                return;
            }

            PaymentOperationResult result = paymentService.getPaymentStatus(paymentId, userId);
            if (result.isSuccess()) {
                sendSuccessResponse(resp, result.getData());
            } else {
                sendErrorResponse(resp, result.getErrorCode(), result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("查询支付状态失败", e);
            sendErrorResponse(resp, PaymentErrorCode.SYSTEM_ERROR, PaymentErrorCode.MSG_SYSTEM_ERROR);
        }
    }

    /**
     * 处理取消支付请求
     */
    private void handleCancelPayment(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 检查用户是否登录
        Long userId = getCurrentUserId(req);
        if (userId == null) {
            sendErrorResponse(resp, "401", "用户未登录");
            return;
        }

        try {
            // 读取请求体
            String requestBody = readRequestBody(req);
            @SuppressWarnings("unchecked")
            Map<String, Object> requestMap = JsonUtil.fromJson(requestBody, Map.class);

            if (requestMap == null || !requestMap.containsKey("paymentId")) {
                sendErrorResponse(resp, "400", "支付ID不能为空");
                return;
            }

            String paymentId = (String) requestMap.get("paymentId");

            PaymentOperationResult result = paymentService.cancelPayment(paymentId, userId);
            if (result.isSuccess()) {
                sendSuccessResponse(resp, result.getData());
            } else {
                sendErrorResponse(resp, result.getErrorCode(), result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("取消支付失败", e);
            sendErrorResponse(resp, PaymentErrorCode.SYSTEM_ERROR, PaymentErrorCode.MSG_SYSTEM_ERROR);
        }
    }

    /**
     * 处理获取用户支付记录请求
     */
    private void handleGetUserPayments(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 检查用户是否登录
        Long userId = getCurrentUserId(req);
        if (userId == null) {
            sendErrorResponse(resp, "401", "用户未登录");
            return;
        }

        try {
            PaymentOperationResult result = paymentService.getUserPayments(userId);
            if (result.isSuccess()) {
                sendSuccessResponse(resp, result.getData());
            } else {
                sendErrorResponse(resp, result.getErrorCode(), result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("获取用户支付记录失败", e);
            sendErrorResponse(resp, PaymentErrorCode.SYSTEM_ERROR, PaymentErrorCode.MSG_SYSTEM_ERROR);
        }
    }

    /**
     * 处理根据订单ID获取支付信息请求
     */
    private void handleGetPaymentByOrderIds(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 检查用户是否登录
        Long userId = getCurrentUserId(req);
        if (userId == null) {
            sendErrorResponse(resp, "401", "用户未登录");
            return;
        }

        try {
            String orderIdsParam = req.getParameter("orderIds");
            if (orderIdsParam == null || orderIdsParam.trim().isEmpty()) {
                sendErrorResponse(resp, "400", "订单ID列表不能为空");
                return;
            }

            // 解析订单ID列表
            @SuppressWarnings("unchecked")
            List<Long> orderIds = JsonUtil.fromJson(orderIdsParam, List.class);

            PaymentOperationResult result = paymentService.getPaymentByOrderIds(orderIds, userId);
            if (result.isSuccess()) {
                sendSuccessResponse(resp, result.getData());
            } else {
                sendErrorResponse(resp, result.getErrorCode(), result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("根据订单ID获取支付信息失败", e);
            sendErrorResponse(resp, PaymentErrorCode.SYSTEM_ERROR, PaymentErrorCode.MSG_SYSTEM_ERROR);
        }
    }

    /**
     * 处理支付页面请求（模拟支付页面）
     */
    private void handlePaymentPage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String paymentId = req.getParameter("paymentId");
        if (paymentId == null || paymentId.trim().isEmpty()) {
            sendErrorResponse(resp, "400", "支付ID不能为空");
            return;
        }

        // 这里应该返回支付页面的HTML，为了简化，我们返回JSON格式的页面信息
        try {
            Map<String, Object> pageData = new java.util.HashMap<>();
            pageData.put("paymentId", paymentId);
            pageData.put("pageTitle", "支付页面");
            pageData.put("message", "请输入支付密码完成支付");
            pageData.put("defaultPassword", "123456");
            pageData.put("note", "输入正确的支付密码后将立即完成支付，默认密码为123456");

            sendSuccessResponse(resp, pageData);
        } catch (Exception e) {
            logger.error("获取支付页面失败", e);
            sendErrorResponse(resp, PaymentErrorCode.SYSTEM_ERROR, PaymentErrorCode.MSG_SYSTEM_ERROR);
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
     * 读取请求体
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
        } else if ("400".equals(code) || code.startsWith("PAYMENT_")) {
            // 支付相关错误都返回400 Bad Request
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
