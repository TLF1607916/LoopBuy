package com.shiwu.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shiwu.admin.model.AdminProductManageDTO;
import com.shiwu.admin.model.AdminProductQueryDTO;
import com.shiwu.admin.service.AdminService;
import com.shiwu.admin.service.impl.AdminServiceImpl;
import com.shiwu.common.result.Result;
import com.shiwu.common.util.JwtUtil;
import com.shiwu.product.service.AdminProductService;
import com.shiwu.product.service.impl.AdminProductServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 管理员商品管理控制器
 */
@WebServlet("/api/admin/products/*")
public class AdminProductController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AdminProductController.class);
    
    private final AdminProductService adminProductService;
    private final AdminService adminService;
    private final ObjectMapper objectMapper;

    public AdminProductController() {
        this.adminProductService = new AdminProductServiceImpl();
        this.adminService = new AdminServiceImpl();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    // 用于测试的构造函数
    public AdminProductController(AdminProductService adminProductService, AdminService adminService) {
        this.adminProductService = adminProductService;
        this.adminService = adminService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        // 验证管理员权限
        Long adminId = validateAdminPermission(req, resp, "ADMIN");
        if (adminId == null) {
            return;
        }
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // 查询商品列表
            handleGetProducts(req, resp, adminId);
        } else if (pathInfo.startsWith("/") && pathInfo.length() > 1) {
            try {
                // 获取商品详情
                Long productId = Long.parseLong(pathInfo.substring(1));
                handleGetProductDetail(req, resp, adminId, productId);
            } catch (NumberFormatException e) {
                sendErrorResponse(resp, "400", "无效的商品ID格式");
            }
        } else {
            sendErrorResponse(resp, "404", "请求路径不存在");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        // 验证管理员权限
        Long adminId = validateAdminPermission(req, resp, "ADMIN");
        if (adminId == null) {
            return;
        }
        
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                String[] segments = pathInfo.substring(1).split("/");
                Long productId = Long.parseLong(segments[0]);
                
                if (segments.length > 1) {
                    switch (segments[1]) {
                        case "approve":
                            // 审核通过商品
                            handleApproveProduct(req, resp, adminId, productId);
                            break;
                        case "reject":
                            // 审核拒绝商品
                            handleRejectProduct(req, resp, adminId, productId);
                            break;
                        case "delist":
                            // 下架商品
                            handleDelistProduct(req, resp, adminId, productId);
                            break;
                        default:
                            sendErrorResponse(resp, "404", "请求路径不存在");
                    }
                } else {
                    sendErrorResponse(resp, "404", "请求路径不存在");
                }
            } catch (NumberFormatException e) {
                sendErrorResponse(resp, "400", "无效的商品ID格式");
            }
        } else {
            sendErrorResponse(resp, "404", "请求路径不存在");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        // 验证管理员权限
        Long adminId = validateAdminPermission(req, resp, "ADMIN");
        if (adminId == null) {
            return;
        }
        
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                // 删除商品
                Long productId = Long.parseLong(pathInfo.substring(1));
                handleDeleteProduct(req, resp, adminId, productId);
            } catch (NumberFormatException e) {
                sendErrorResponse(resp, "400", "无效的商品ID格式");
            }
        } else {
            sendErrorResponse(resp, "404", "请求路径不存在");
        }
    }

    /**
     * 验证管理员权限
     */
    private Long validateAdminPermission(HttpServletRequest req, HttpServletResponse resp, String requiredRole) 
            throws IOException {
        String token = req.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        if (!JwtUtil.validateToken(token)) {
            sendErrorResponse(resp, "401", "未授权访问");
            return null;
        }
        
        Long adminId = JwtUtil.getUserIdFromToken(token);
        if (adminId == null) {
            sendErrorResponse(resp, "401", "无效的令牌");
            return null;
        }
        
        if (!adminService.hasPermission(adminId, requiredRole)) {
            sendErrorResponse(resp, "403", "权限不足");
            return null;
        }
        
        return adminId;
    }

    /**
     * 处理查询商品列表请求
     */
    private void handleGetProducts(HttpServletRequest req, HttpServletResponse resp, Long adminId) 
            throws IOException {
        try {
            // 解析查询参数
            AdminProductQueryDTO queryDTO = parseQueryParams(req);
            
            // 查询商品列表
            Map<String, Object> result = adminProductService.findProducts(queryDTO);
            
            sendSuccessResponse(resp, result);
        } catch (Exception e) {
            logger.error("查询商品列表失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "系统错误，请稍后再试");
        }
    }

    /**
     * 处理获取商品详情请求
     */
    private void handleGetProductDetail(HttpServletRequest req, HttpServletResponse resp, Long adminId, Long productId) 
            throws IOException {
        try {
            Map<String, Object> result = adminProductService.getProductDetail(productId, adminId);
            
            if (result != null) {
                sendSuccessResponse(resp, result);
            } else {
                sendErrorResponse(resp, "404", "商品不存在");
            }
        } catch (Exception e) {
            logger.error("获取商品详情失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "系统错误，请稍后再试");
        }
    }

    /**
     * 处理审核通过商品请求
     */
    private void handleApproveProduct(HttpServletRequest req, HttpServletResponse resp, Long adminId, Long productId) 
            throws IOException {
        try {
            // 解析请求体
            AdminProductManageDTO manageDTO = parseRequestBody(req, AdminProductManageDTO.class);
            
            boolean success = adminProductService.approveProduct(productId, adminId, manageDTO.getReason());
            
            if (success) {
                sendSuccessResponse(resp, null, "商品审核通过");
                logger.info("管理员 {} 审核通过商品 {}", adminId, productId);
            } else {
                sendErrorResponse(resp, "400", "审核失败，请检查商品状态");
            }
        } catch (Exception e) {
            logger.error("审核通过商品失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "系统错误，请稍后再试");
        }
    }

    /**
     * 处理审核拒绝商品请求
     */
    private void handleRejectProduct(HttpServletRequest req, HttpServletResponse resp, Long adminId, Long productId) 
            throws IOException {
        try {
            // 解析请求体
            AdminProductManageDTO manageDTO = parseRequestBody(req, AdminProductManageDTO.class);
            
            if (manageDTO.getReason() == null || manageDTO.getReason().trim().isEmpty()) {
                sendErrorResponse(resp, "400", "拒绝原因不能为空");
                return;
            }
            
            boolean success = adminProductService.rejectProduct(productId, adminId, manageDTO.getReason());
            
            if (success) {
                sendSuccessResponse(resp, null, "商品审核拒绝");
                logger.info("管理员 {} 审核拒绝商品 {}, 原因: {}", adminId, productId, manageDTO.getReason());
            } else {
                sendErrorResponse(resp, "400", "审核失败，请检查商品状态");
            }
        } catch (Exception e) {
            logger.error("审核拒绝商品失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "系统错误，请稍后再试");
        }
    }

    /**
     * 处理下架商品请求
     */
    private void handleDelistProduct(HttpServletRequest req, HttpServletResponse resp, Long adminId, Long productId) 
            throws IOException {
        try {
            // 解析请求体
            AdminProductManageDTO manageDTO = parseRequestBody(req, AdminProductManageDTO.class);
            
            boolean success = adminProductService.delistProduct(productId, adminId, manageDTO.getReason());
            
            if (success) {
                sendSuccessResponse(resp, null, "商品下架成功");
                logger.info("管理员 {} 下架商品 {}, 原因: {}", adminId, productId, manageDTO.getReason());
            } else {
                sendErrorResponse(resp, "400", "下架失败，请检查商品状态");
            }
        } catch (Exception e) {
            logger.error("下架商品失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "系统错误，请稍后再试");
        }
    }

    /**
     * 处理删除商品请求
     */
    private void handleDeleteProduct(HttpServletRequest req, HttpServletResponse resp, Long adminId, Long productId) 
            throws IOException {
        try {
            boolean success = adminProductService.deleteProduct(productId, adminId);
            
            if (success) {
                sendSuccessResponse(resp, null, "商品删除成功");
                logger.info("管理员 {} 删除商品 {}", adminId, productId);
            } else {
                sendErrorResponse(resp, "400", "删除失败，商品不存在或已删除");
            }
        } catch (Exception e) {
            logger.error("删除商品失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "系统错误，请稍后再试");
        }
    }

    /**
     * 解析查询参数
     */
    private AdminProductQueryDTO parseQueryParams(HttpServletRequest req) {
        AdminProductQueryDTO queryDTO = new AdminProductQueryDTO();
        
        String keyword = req.getParameter("keyword");
        String status = req.getParameter("status");
        String sellerId = req.getParameter("sellerId");
        String categoryId = req.getParameter("categoryId");
        String pageNum = req.getParameter("pageNum");
        String pageSize = req.getParameter("pageSize");
        String sortBy = req.getParameter("sortBy");
        String sortDirection = req.getParameter("sortDirection");
        
        queryDTO.setKeyword(keyword);
        
        if (status != null && !status.trim().isEmpty()) {
            try {
                queryDTO.setStatus(Integer.parseInt(status));
            } catch (NumberFormatException e) {
                // 忽略无效的状态参数
            }
        }
        
        if (sellerId != null && !sellerId.trim().isEmpty()) {
            try {
                queryDTO.setSellerId(Long.parseLong(sellerId));
            } catch (NumberFormatException e) {
                // 忽略无效的卖家ID参数
            }
        }
        
        if (categoryId != null && !categoryId.trim().isEmpty()) {
            try {
                queryDTO.setCategoryId(Integer.parseInt(categoryId));
            } catch (NumberFormatException e) {
                // 忽略无效的分类ID参数
            }
        }
        
        queryDTO.setPageNum(pageNum != null ? Integer.parseInt(pageNum) : 1);
        queryDTO.setPageSize(pageSize != null ? Integer.parseInt(pageSize) : 20);
        queryDTO.setSortBy(sortBy != null ? sortBy : "create_time");
        queryDTO.setSortDirection(sortDirection != null ? sortDirection : "DESC");
        
        return queryDTO;
    }

    /**
     * 解析请求体
     */
    private <T> T parseRequestBody(HttpServletRequest req, Class<T> clazz) throws IOException {
        return objectMapper.readValue(req.getInputStream(), clazz);
    }

    /**
     * 发送成功响应
     */
    private void sendSuccessResponse(HttpServletResponse resp, Object data) throws IOException {
        sendSuccessResponse(resp, data, "操作成功");
    }

    /**
     * 发送成功响应
     */
    private void sendSuccessResponse(HttpServletResponse resp, Object data, String message) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Result<Object> result = Result.success(data);
        result.setMessage(message);
        resp.getWriter().write(objectMapper.writeValueAsString(result));
    }

    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse resp, String code, String message) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Result<Object> result = Result.fail(code, message);
        resp.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
