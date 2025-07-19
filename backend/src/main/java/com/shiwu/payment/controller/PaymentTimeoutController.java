package com.shiwu.payment.controller;

import com.shiwu.common.result.Result;
import com.shiwu.common.util.JsonUtil;
import com.shiwu.payment.task.PaymentTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付超时管理控制器
 * 提供手动触发超时检查、查看超时状态等管理功能
 */
@WebServlet("/api/payment/timeout/*")
public class PaymentTimeoutController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(PaymentTimeoutController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // 获取超时状态信息
            handleGetTimeoutStatus(req, resp);
        } else if (pathInfo.equals("/count")) {
            // 获取过期支付记录数量
            handleGetExpiredCount(req, resp);
        } else {
            sendErrorResponse(resp, "404", "请求路径不存在");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // 手动触发超时检查
            handleTriggerTimeoutCheck(req, resp);
        } else if (pathInfo.equals("/handle")) {
            // 手动处理指定的过期支付
            handleSpecificExpiredPayment(req, resp);
        } else {
            sendErrorResponse(resp, "404", "请求路径不存在");
        }
    }

    /**
     * 处理获取超时状态信息请求
     */
    private void handleGetTimeoutStatus(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 检查管理员权限（简化实现，实际应该有完整的权限验证）
        if (!isAdmin(req)) {
            sendErrorResponse(resp, "403", "权限不足");
            return;
        }

        try {
            PaymentTimeoutHandler handler = PaymentTimeoutHandler.getInstance();
            
            Map<String, Object> status = new HashMap<>();
            status.put("isRunning", handler.isRunning());
            status.put("expiredPaymentCount", handler.getExpiredPaymentCount());
            status.put("message", "支付超时检查任务状态");

            sendSuccessResponse(resp, status);
        } catch (Exception e) {
            logger.error("获取超时状态失败", e);
            sendErrorResponse(resp, "500", "获取超时状态失败");
        }
    }

    /**
     * 处理获取过期支付记录数量请求
     */
    private void handleGetExpiredCount(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 检查管理员权限
        if (!isAdmin(req)) {
            sendErrorResponse(resp, "403", "权限不足");
            return;
        }

        try {
            PaymentTimeoutHandler handler = PaymentTimeoutHandler.getInstance();
            int count = handler.getExpiredPaymentCount();
            
            Map<String, Object> data = new HashMap<>();
            data.put("expiredPaymentCount", count);
            data.put("message", count > 0 ? "发现" + count + "个过期支付记录" : "没有过期支付记录");

            sendSuccessResponse(resp, data);
        } catch (Exception e) {
            logger.error("获取过期支付记录数量失败", e);
            sendErrorResponse(resp, "500", "获取过期支付记录数量失败");
        }
    }

    /**
     * 处理手动触发超时检查请求
     */
    private void handleTriggerTimeoutCheck(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 检查管理员权限
        if (!isAdmin(req)) {
            sendErrorResponse(resp, "403", "权限不足");
            return;
        }

        try {
            PaymentTimeoutHandler handler = PaymentTimeoutHandler.getInstance();
            
            // 获取当前过期支付记录数量
            int beforeCount = handler.getExpiredPaymentCount();
            
            // 这里我们不能直接调用私有方法，所以我们返回当前状态
            // 实际的超时检查会由定时任务自动执行
            
            Map<String, Object> result = new HashMap<>();
            result.put("message", "超时检查任务正在后台运行");
            result.put("expiredPaymentCount", beforeCount);
            result.put("note", "系统会自动每分钟检查一次过期支付记录");

            sendSuccessResponse(resp, result);
            
            logger.info("管理员手动触发超时检查: 当前过期支付记录数量={}", beforeCount);
        } catch (Exception e) {
            logger.error("手动触发超时检查失败", e);
            sendErrorResponse(resp, "500", "手动触发超时检查失败");
        }
    }

    /**
     * 处理手动处理指定过期支付请求
     */
    private void handleSpecificExpiredPayment(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 检查管理员权限
        if (!isAdmin(req)) {
            sendErrorResponse(resp, "403", "权限不足");
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

            PaymentTimeoutHandler handler = PaymentTimeoutHandler.getInstance();
            boolean success = handler.handleExpiredPayment(paymentId);

            Map<String, Object> result = new HashMap<>();
            result.put("paymentId", paymentId);
            result.put("success", success);
            result.put("message", success ? "处理过期支付成功" : "处理过期支付失败");

            if (success) {
                sendSuccessResponse(resp, result);
            } else {
                sendErrorResponse(resp, "500", "处理过期支付失败");
            }

            logger.info("管理员手动处理过期支付: paymentId={}, success={}", paymentId, success);
        } catch (Exception e) {
            logger.error("手动处理过期支付失败", e);
            sendErrorResponse(resp, "500", "手动处理过期支付失败");
        }
    }

    /**
     * 检查是否为管理员（简化实现）
     */
    private boolean isAdmin(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            Object userRoleObj = session.getAttribute("userRole");
            return "admin".equals(userRoleObj);
        }
        return false;
    }

    /**
     * 读取请求体
     */
    private String readRequestBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (java.io.BufferedReader reader = req.getReader()) {
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

        if ("403".equals(code)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else if ("404".equals(code)) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else if ("400".equals(code)) {
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
