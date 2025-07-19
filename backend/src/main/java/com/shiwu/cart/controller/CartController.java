package com.shiwu.cart.controller;

import com.shiwu.cart.model.CartAddDTO;
import com.shiwu.cart.model.CartErrorCode;
import com.shiwu.cart.model.CartOperationResult;
import com.shiwu.cart.service.CartService;
import com.shiwu.cart.service.impl.CartServiceImpl;
import com.shiwu.common.result.Result;
import com.shiwu.common.util.JsonUtil;
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
import java.util.stream.Collectors;

/**
 * 购物车控制器
 */
@WebServlet("/api/cart/*")
public class CartController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    private final CartService cartService;

    public CartController() {
        this.cartService = new CartServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/list")) {
            handleGetCart(req, resp);
        } else {
            sendErrorResponse(resp, "404", "请求路径不存在");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null) {
            sendErrorResponse(resp, "404", "请求路径不存在");
            return;
        }

        switch (pathInfo) {
            case "/add":
                handleAddToCart(req, resp);
                break;
            case "/batch-remove":
                handleBatchRemoveFromCart(req, resp);
                break;
            case "/clear":
                handleClearCart(req, resp);
                break;
            default:
                sendErrorResponse(resp, "404", "请求路径不存在");
                break;
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null) {
            sendErrorResponse(resp, "404", "请求路径不存在");
            return;
        }

        // 处理 /api/cart/remove/{productId} 格式的请求
        if (pathInfo.matches("^/remove/\\d+$")) {
            handleRemoveFromCart(req, resp);
        } else {
            sendErrorResponse(resp, "404", "请求路径不存在");
        }
    }

    /**
     * 获取购物车
     */
    private void handleGetCart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 检查用户是否登录
        Long userId = getCurrentUserId(req);
        if (userId == null) {
            sendErrorResponse(resp, "401", "用户未登录");
            return;
        }

        try {
            CartOperationResult result = cartService.getCart(userId);
            if (result.isSuccess()) {
                sendSuccessResponse(resp, result.getData());
            } else {
                sendErrorResponse(resp, result.getErrorCode(), result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("获取购物车失败", e);
            sendErrorResponse(resp, CartErrorCode.SYSTEM_ERROR, CartErrorCode.MSG_SYSTEM_ERROR);
        }
    }

    /**
     * 添加商品到购物车
     */
    private void handleAddToCart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 检查用户是否登录
        Long userId = getCurrentUserId(req);
        if (userId == null) {
            sendErrorResponse(resp, "401", "用户未登录");
            return;
        }

        try {
            // 读取请求体
            String requestBody = readRequestBody(req);
            CartAddDTO dto = JsonUtil.fromJson(requestBody, CartAddDTO.class);

            if (dto == null || dto.getProductId() == null) {
                sendErrorResponse(resp, "400", "请求参数不能为空");
                return;
            }

            // 如果数量为空，默认设置为1
            if (dto.getQuantity() == null) {
                dto.setQuantity(1);
            }

            CartOperationResult result = cartService.addToCart(dto, userId);
            if (result.isSuccess()) {
                sendSuccessResponse(resp, result.getData());
            } else {
                sendErrorResponse(resp, result.getErrorCode(), result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("添加商品到购物车失败", e);
            sendErrorResponse(resp, "500", "系统错误");
        }
    }

    /**
     * 从购物车中移除商品
     */
    private void handleRemoveFromCart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 检查用户是否登录
        Long userId = getCurrentUserId(req);
        if (userId == null) {
            sendErrorResponse(resp, "401", "用户未登录");
            return;
        }

        try {
            // 从路径中提取商品ID
            String pathInfo = req.getPathInfo();
            String productIdStr = pathInfo.substring(pathInfo.lastIndexOf('/') + 1);
            Long productId = Long.parseLong(productIdStr);

            CartOperationResult result = cartService.removeFromCart(productId, userId);
            if (result.isSuccess()) {
                sendSuccessResponse(resp, result.getData());
            } else {
                sendErrorResponse(resp, result.getErrorCode(), result.getErrorMessage());
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(resp, "400", "商品ID格式错误");
        } catch (Exception e) {
            logger.error("从购物车移除商品失败", e);
            sendErrorResponse(resp, "500", "系统错误");
        }
    }

    /**
     * 批量从购物车中移除商品
     */
    private void handleBatchRemoveFromCart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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

            if (requestMap == null || !requestMap.containsKey("productIds")) {
                sendErrorResponse(resp, "400", "请求参数不能为空");
                return;
            }

            @SuppressWarnings("unchecked")
            List<Object> productIdObjects = (List<Object>) requestMap.get("productIds");
            List<Long> productIds = productIdObjects.stream()
                    .map(obj -> {
                        if (obj instanceof Number) {
                            return ((Number) obj).longValue();
                        } else {
                            return Long.parseLong(obj.toString());
                        }
                    })
                    .collect(Collectors.toList());

            CartOperationResult result = cartService.batchRemoveFromCart(productIds, userId);
            if (result.isSuccess()) {
                sendSuccessResponse(resp, result.getData());
            } else {
                sendErrorResponse(resp, result.getErrorCode(), result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("批量从购物车移除商品失败", e);
            sendErrorResponse(resp, "500", "系统错误");
        }
    }

    /**
     * 清空购物车
     */
    private void handleClearCart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 检查用户是否登录
        Long userId = getCurrentUserId(req);
        if (userId == null) {
            sendErrorResponse(resp, "401", "用户未登录");
            return;
        }

        try {
            CartOperationResult result = cartService.clearCart(userId);
            if (result.isSuccess()) {
                sendSuccessResponse(resp, result.getData());
            } else {
                sendErrorResponse(resp, result.getErrorCode(), result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("清空购物车失败", e);
            sendErrorResponse(resp, "500", "系统错误");
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
        } else if ("400".equals(code) || code.startsWith("CART_")) {
            // 购物车相关错误都返回400 Bad Request
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
