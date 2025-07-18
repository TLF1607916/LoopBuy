package com.shiwu.product.controller;

import com.shiwu.common.result.Result;
import com.shiwu.common.util.JsonUtil;
import com.shiwu.product.model.CategoryVO;
import com.shiwu.product.service.ProductService;
import com.shiwu.product.service.impl.ProductServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * 商品分类控制器
 */
@WebServlet("/api/categories/*")
public class CategoryController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    private final ProductService productService;
    
    public CategoryController() {
        this.productService = new ProductServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // 获取所有商品分类
            handleGetAllCategories(req, resp);
        } else {
            sendErrorResponse(resp, "404", "请求路径不存在");
        }
    }
    
    /**
     * 处理获取所有商品分类请求
     */
    private void handleGetAllCategories(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // 获取所有分类
            List<CategoryVO> categories = productService.getAllCategories();
            
            // 返回结果
            sendSuccessResponse(resp, categories);
        } catch (Exception e) {
            logger.error("处理获取商品分类请求失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "系统错误，请稍后再试");
        }
    }
    
    /**
     * 发送成功响应
     */
    private void sendSuccessResponse(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        Result<Object> result = Result.success(data);
        String jsonResponse = JsonUtil.toJson(result);
        
        PrintWriter out = resp.getWriter();
        out.print(jsonResponse);
        out.flush();
    }
    
    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse resp, String errorCode, String errorMessage) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        Result<Object> result = Result.fail(errorCode, errorMessage);
        String jsonResponse = JsonUtil.toJson(result);
        
        PrintWriter out = resp.getWriter();
        out.print(jsonResponse);
        out.flush();
    }
} 