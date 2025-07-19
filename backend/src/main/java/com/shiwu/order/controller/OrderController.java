package com.shiwu.order.controller;

import com.shiwu.common.result.Result;
import com.shiwu.common.util.JsonUtil;
import com.shiwu.order.model.OrderCreateDTO;
import com.shiwu.order.model.OrderErrorCode;
import com.shiwu.order.model.OrderOperationResult;
import com.shiwu.order.model.ProcessReturnRequestDTO;
import com.shiwu.order.model.ReturnRequestDTO;
import com.shiwu.order.service.OrderService;
import com.shiwu.order.service.impl.OrderServiceImpl;
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

/**
 * 订单控制器
 */
@WebServlet("/api/orders/*")
public class OrderController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    public OrderController() {
        this.orderService = new OrderServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // 获取订单列表（根据查询参数决定是买家还是卖家订单）
            handleGetOrders(req, resp);
        } else if (pathInfo.equals("/buyer")) {
            // 获取买家订单列表
            handleGetBuyerOrders(req, resp);
        } else if (pathInfo.equals("/seller")) {
            // 获取卖家订单列表
            handleGetSellerOrders(req, resp);
        } else if (pathInfo.startsWith("/") && pathInfo.length() > 1) {
            try {
                // 获取订单详情
                Long orderId = Long.parseLong(pathInfo.substring(1));
                handleGetOrderDetail(req, resp, orderId);
            } catch (NumberFormatException e) {
                sendErrorResponse(resp, "400", "无效的订单ID格式");
            }
        } else {
            sendErrorResponse(resp, "404", "请求路径不存在");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // 创建订单
            handleCreateOrder(req, resp);
        } else if (pathInfo.startsWith("/") && pathInfo.length() > 1) {
            try {
                String[] segments = pathInfo.substring(1).split("/");
                Long orderId = Long.parseLong(segments[0]);
                
                if (segments.length > 1 && segments[1].equals("status")) {
                    // 更新订单状态
                    handleUpdateOrderStatus(req, resp, orderId);
                } else if (segments.length > 1 && segments[1].equals("ship")) {
                    // 卖家发货
                    handleShipOrder(req, resp, orderId);
                } else if (segments.length > 1 && segments[1].equals("confirm")) {
                    // 买家确认收货
                    handleConfirmReceipt(req, resp, orderId);
                } else if (segments.length > 1 && segments[1].equals("return")) {
                    // 买家申请退货
                    handleApplyForReturn(req, resp, orderId);
                } else if (segments.length > 1 && segments[1].equals("process-return")) {
                    // 卖家处理退货申请
                    handleProcessReturnRequest(req, resp, orderId);
                } else {
                    sendErrorResponse(resp, "404", "请求路径不存在");
                }
            } catch (NumberFormatException e) {
                sendErrorResponse(resp, "400", "无效的订单ID格式");
            }
        } else {
            sendErrorResponse(resp, "404", "请求路径不存在");
        }
    }

    /**
     * 处理创建订单请求
     */
    private void handleCreateOrder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 检查用户是否登录
        Long userId = getCurrentUserId(req);
        if (userId == null) {
            sendErrorResponse(resp, "401", "用户未登录");
            return;
        }

        try {
            // 读取请求体
            String requestBody = readRequestBody(req);
            OrderCreateDTO dto = JsonUtil.fromJson(requestBody, OrderCreateDTO.class);

            if (dto == null) {
                sendErrorResponse(resp, "400", "请求参数不能为空");
                return;
            }

            // 创建订单
            OrderOperationResult result = orderService.createOrder(dto, userId);
            if (result.isSuccess()) {
                sendSuccessResponse(resp, result.getData());
            } else {
                sendErrorResponse(resp, result.getErrorCode(), result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("创建订单失败", e);
            sendErrorResponse(resp, OrderErrorCode.SYSTEM_ERROR, OrderErrorCode.MSG_SYSTEM_ERROR);
        }
    }

    /**
     * 处理获取订单列表请求（通用）
     */
    private void handleGetOrders(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 检查用户是否登录
        Long userId = getCurrentUserId(req);
        if (userId == null) {
            sendErrorResponse(resp, "401", "用户未登录");
            return;
        }

        try {
            String type = req.getParameter("type");
            OrderOperationResult result;
            
            if ("seller".equals(type)) {
                result = orderService.getSellerOrders(userId);
            } else {
                // 默认获取买家订单
                result = orderService.getBuyerOrders(userId);
            }
            
            if (result.isSuccess()) {
                sendSuccessResponse(resp, result.getData());
            } else {
                sendErrorResponse(resp, result.getErrorCode(), result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("获取订单列表失败", e);
            sendErrorResponse(resp, OrderErrorCode.SYSTEM_ERROR, OrderErrorCode.MSG_SYSTEM_ERROR);
        }
    }

    /**
     * 处理获取买家订单列表请求
     */
    private void handleGetBuyerOrders(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 检查用户是否登录
        Long userId = getCurrentUserId(req);
        if (userId == null) {
            sendErrorResponse(resp, "401", "用户未登录");
            return;
        }

        try {
            OrderOperationResult result = orderService.getBuyerOrders(userId);
            if (result.isSuccess()) {
                sendSuccessResponse(resp, result.getData());
            } else {
                sendErrorResponse(resp, result.getErrorCode(), result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("获取买家订单列表失败", e);
            sendErrorResponse(resp, OrderErrorCode.SYSTEM_ERROR, OrderErrorCode.MSG_SYSTEM_ERROR);
        }
    }

    /**
     * 处理获取卖家订单列表请求
     */
    private void handleGetSellerOrders(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 检查用户是否登录
        Long userId = getCurrentUserId(req);
        if (userId == null) {
            sendErrorResponse(resp, "401", "用户未登录");
            return;
        }

        try {
            OrderOperationResult result = orderService.getSellerOrders(userId);
            if (result.isSuccess()) {
                sendSuccessResponse(resp, result.getData());
            } else {
                sendErrorResponse(resp, result.getErrorCode(), result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("获取卖家订单列表失败", e);
            sendErrorResponse(resp, OrderErrorCode.SYSTEM_ERROR, OrderErrorCode.MSG_SYSTEM_ERROR);
        }
    }

    /**
     * 处理获取订单详情请求
     */
    private void handleGetOrderDetail(HttpServletRequest req, HttpServletResponse resp, Long orderId) throws IOException {
        // 检查用户是否登录
        Long userId = getCurrentUserId(req);
        if (userId == null) {
            sendErrorResponse(resp, "401", "用户未登录");
            return;
        }

        try {
            OrderOperationResult result = orderService.getOrderById(orderId, userId);
            if (result.isSuccess()) {
                sendSuccessResponse(resp, result.getData());
            } else {
                sendErrorResponse(resp, result.getErrorCode(), result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("获取订单详情失败", e);
            sendErrorResponse(resp, OrderErrorCode.SYSTEM_ERROR, OrderErrorCode.MSG_SYSTEM_ERROR);
        }
    }

    /**
     * 处理更新订单状态请求
     */
    private void handleUpdateOrderStatus(HttpServletRequest req, HttpServletResponse resp, Long orderId) throws IOException {
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
            java.util.Map<String, Object> requestMap = JsonUtil.fromJson(requestBody, java.util.Map.class);

            if (requestMap == null || !requestMap.containsKey("status")) {
                sendErrorResponse(resp, "400", "无效的请求格式，缺少status字段");
                return;
            }

            // 获取状态值
            Integer status;
            try {
                status = (Integer) requestMap.get("status");
            } catch (ClassCastException e) {
                sendErrorResponse(resp, "400", "无效的状态值格式");
                return;
            }

            // 更新订单状态
            OrderOperationResult result = orderService.updateOrderStatus(orderId, status, userId);
            if (result.isSuccess()) {
                sendSuccessResponse(resp, result.getData());
            } else {
                sendErrorResponse(resp, result.getErrorCode(), result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("更新订单状态失败", e);
            sendErrorResponse(resp, OrderErrorCode.SYSTEM_ERROR, OrderErrorCode.MSG_SYSTEM_ERROR);
        }
    }

    /**
     * 处理卖家发货请求
     */
    private void handleShipOrder(HttpServletRequest req, HttpServletResponse resp, Long orderId) throws IOException {
        // 检查用户是否登录
        Long userId = getCurrentUserId(req);
        if (userId == null) {
            sendErrorResponse(resp, "401", "用户未登录");
            return;
        }

        try {
            // 调用订单服务的发货方法
            OrderOperationResult result = orderService.shipOrder(orderId, userId);
            if (result.isSuccess()) {
                sendSuccessResponse(resp, result.getData());
            } else {
                sendErrorResponse(resp, result.getErrorCode(), result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("发货失败: orderId={}, userId={}", orderId, userId, e);
            sendErrorResponse(resp, OrderErrorCode.SYSTEM_ERROR, OrderErrorCode.MSG_SYSTEM_ERROR);
        }
    }

    /**
     * 处理买家确认收货请求
     */
    private void handleConfirmReceipt(HttpServletRequest req, HttpServletResponse resp, Long orderId) throws IOException {
        // 检查用户是否登录
        Long userId = getCurrentUserId(req);
        if (userId == null) {
            sendErrorResponse(resp, "401", "用户未登录");
            return;
        }

        try {
            // 调用订单服务的确认收货方法
            OrderOperationResult result = orderService.confirmReceipt(orderId, userId);
            if (result.isSuccess()) {
                sendSuccessResponse(resp, result.getData());
            } else {
                sendErrorResponse(resp, result.getErrorCode(), result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("确认收货失败: orderId={}, userId={}", orderId, userId, e);
            sendErrorResponse(resp, OrderErrorCode.SYSTEM_ERROR, OrderErrorCode.MSG_SYSTEM_ERROR);
        }
    }

    /**
     * 处理买家申请退货请求
     */
    private void handleApplyForReturn(HttpServletRequest req, HttpServletResponse resp, Long orderId) throws IOException {
        // 检查用户是否登录
        Long userId = getCurrentUserId(req);
        if (userId == null) {
            sendErrorResponse(resp, "401", "用户未登录");
            return;
        }

        try {
            // 读取请求体
            String requestBody = readRequestBody(req);
            ReturnRequestDTO dto = JsonUtil.fromJson(requestBody, ReturnRequestDTO.class);

            if (dto == null) {
                sendErrorResponse(resp, "400", "请求参数不能为空");
                return;
            }

            // 调用订单服务的申请退货方法
            OrderOperationResult result = orderService.applyForReturn(orderId, dto, userId);
            if (result.isSuccess()) {
                sendSuccessResponse(resp, result.getData());
            } else {
                sendErrorResponse(resp, result.getErrorCode(), result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("申请退货失败: orderId={}, userId={}", orderId, userId, e);
            sendErrorResponse(resp, OrderErrorCode.SYSTEM_ERROR, OrderErrorCode.MSG_SYSTEM_ERROR);
        }
    }

    /**
     * 处理卖家处理退货申请请求
     */
    private void handleProcessReturnRequest(HttpServletRequest req, HttpServletResponse resp, Long orderId) throws IOException {
        // 检查用户是否登录
        Long userId = getCurrentUserId(req);
        if (userId == null) {
            sendErrorResponse(resp, "401", "用户未登录");
            return;
        }

        try {
            // 读取请求体
            String requestBody = readRequestBody(req);
            ProcessReturnRequestDTO dto = JsonUtil.fromJson(requestBody, ProcessReturnRequestDTO.class);

            if (dto == null) {
                sendErrorResponse(resp, "400", "请求参数不能为空");
                return;
            }

            // 调用订单服务的处理退货申请方法
            OrderOperationResult result = orderService.processReturnRequest(orderId, dto, userId);
            if (result.isSuccess()) {
                sendSuccessResponse(resp, result.getData());
            } else {
                sendErrorResponse(resp, result.getErrorCode(), result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("处理退货申请失败: orderId={}, userId={}", orderId, userId, e);
            sendErrorResponse(resp, OrderErrorCode.SYSTEM_ERROR, OrderErrorCode.MSG_SYSTEM_ERROR);
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
        } else if ("400".equals(code) || code.startsWith("ORDER_")) {
            // 订单相关错误都返回400 Bad Request
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
