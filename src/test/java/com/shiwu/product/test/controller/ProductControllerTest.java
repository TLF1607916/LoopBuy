package com.shiwu.test.controller;

import com.shiwu.common.result.Result;
import com.shiwu.common.util.JsonUtil;
import com.shiwu.common.util.RequestUtil;
import com.shiwu.product.controller.ProductController;
import com.shiwu.product.model.Product;
import com.shiwu.product.model.ProductCardVO;
import com.shiwu.product.model.ProductDetailVO;
import com.shiwu.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private PrintWriter writer;

    private TestableProductController productController;

    // 创建一个子类来公开protected方法
    private static class TestableProductController extends ProductController {
        @Override
        public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            super.doGet(req, resp);
        }
    }

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        // 创建ProductController并通过反射注入模拟的productService
        productController = new TestableProductController();
        Field serviceField = ProductController.class.getDeclaredField("productService");
        serviceField.setAccessible(true);
        serviceField.set(productController, productService);
        
        // 模拟HttpServletResponse的Writer
        StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
    }

    @Test
    public void testGetProductDetailEndpoint() throws ServletException, IOException {
        // 准备
        Long productId = 1L;
        Long currentUserId = 1L;
        
        // 模拟请求PathInfo
        when(request.getPathInfo()).thenReturn("/" + productId);
        
        // 模拟当前用户ID
        try (MockedStatic<RequestUtil> mockedRequestUtil = Mockito.mockStatic(RequestUtil.class)) {
            mockedRequestUtil.when(() -> RequestUtil.getCurrentUserId(request)).thenReturn(currentUserId);
            
            // 模拟商品详情
            ProductDetailVO mockProductDetail = new ProductDetailVO();
            mockProductDetail.setId(productId);
            mockProductDetail.setTitle("测试商品");
            mockProductDetail.setPrice(new BigDecimal("99.99"));
            mockProductDetail.setStatus(Product.STATUS_ONSALE);
            
            when(productService.getProductDetailById(eq(productId), eq(currentUserId))).thenReturn(mockProductDetail);
            
            // 执行
            productController.doGet(request, response);
            
            // 验证
            verify(response).setContentType("application/json");
            verify(response).setCharacterEncoding("UTF-8");
            verify(productService).getProductDetailById(eq(productId), eq(currentUserId));
        }
    }

    @Test
    public void testGetProductDetailEndpoint_NotFound() throws ServletException, IOException {
        // 准备
        Long productId = 999L;
        Long currentUserId = 1L;
        
        // 模拟请求PathInfo
        when(request.getPathInfo()).thenReturn("/" + productId);
        
        // 模拟当前用户ID
        try (MockedStatic<RequestUtil> mockedRequestUtil = Mockito.mockStatic(RequestUtil.class)) {
            mockedRequestUtil.when(() -> RequestUtil.getCurrentUserId(request)).thenReturn(currentUserId);
            
            // 模拟商品不存在
            when(productService.getProductDetailById(eq(productId), eq(currentUserId))).thenReturn(null);
            
            // 执行
            productController.doGet(request, response);
            
            // 验证
            verify(response).setContentType("application/json");
            verify(response).setCharacterEncoding("UTF-8");
            verify(productService).getProductDetailById(eq(productId), eq(currentUserId));
        }
    }

    @Test
    public void testGetProductsEndpoint() throws ServletException, IOException {
        // 准备
        String keyword = "测试";
        String categoryIdStr = "1";
        String minPriceStr = "10";
        String maxPriceStr = "100";
        String sortBy = "price";
        String sortDirection = "asc";
        String pageNumStr = "1";
        String pageSizeStr = "10";
        
        // 模拟请求PathInfo和查询参数
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("keyword")).thenReturn(keyword);
        when(request.getParameter("categoryId")).thenReturn(categoryIdStr);
        when(request.getParameter("minPrice")).thenReturn(minPriceStr);
        when(request.getParameter("maxPrice")).thenReturn(maxPriceStr);
        when(request.getParameter("sortBy")).thenReturn(sortBy);
        when(request.getParameter("sortDirection")).thenReturn(sortDirection);
        when(request.getParameter("pageNum")).thenReturn(pageNumStr);
        when(request.getParameter("pageSize")).thenReturn(pageSizeStr);
        
        // 模拟查询结果
        List<ProductDetailVO> productList = new ArrayList<>();
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("list", productList);
        mockResult.put("total", 1);
        mockResult.put("pageNum", 1);
        mockResult.put("pageSize", 10);
        
        when(productService.findProducts(
            eq(keyword), eq(1), eq(new BigDecimal("10")), eq(new BigDecimal("100")),
            eq(sortBy), eq(sortDirection), eq(1), eq(10)
        )).thenReturn(mockResult);
        
        // 执行
        productController.doGet(request, response);
        
        // 验证
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(productService).findProducts(
            eq(keyword), eq(1), eq(new BigDecimal("10")), eq(new BigDecimal("100")),
            eq(sortBy), eq(sortDirection), eq(1), eq(10)
        );
    }

    @Test
    public void testGetMyProductsEndpoint() throws ServletException, IOException {
        // 准备
        Long currentUserId = 1L;
        String statusStr = "1";
        Integer status = 1;
        
        // 模拟请求PathInfo和查询参数
        when(request.getPathInfo()).thenReturn("/my");
        when(request.getParameter("status")).thenReturn(statusStr);
        
        // 模拟当前用户ID
        try (MockedStatic<RequestUtil> mockedRequestUtil = Mockito.mockStatic(RequestUtil.class)) {
            mockedRequestUtil.when(() -> RequestUtil.getCurrentUserId(request)).thenReturn(currentUserId);
            
            // 模拟结果 - 修复类型不匹配问题
            List<ProductCardVO> productList = new ArrayList<>();
            when(productService.getProductsBySellerIdAndStatus(eq(currentUserId), eq(status))).thenReturn(productList);
            
            // 执行
            productController.doGet(request, response);
            
            // 验证
            verify(response).setContentType("application/json");
            verify(response).setCharacterEncoding("UTF-8");
            verify(productService).getProductsBySellerIdAndStatus(eq(currentUserId), eq(status));
        }
    }
} 