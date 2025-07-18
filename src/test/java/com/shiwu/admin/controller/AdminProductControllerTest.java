package com.shiwu.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shiwu.admin.model.AdminProductManageDTO;
import com.shiwu.admin.service.AdminService;
import com.shiwu.common.util.JwtUtil;
import com.shiwu.product.service.AdminProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 管理员商品控制器测试
 */
public class AdminProductControllerTest {

    @Mock
    private AdminProductService mockAdminProductService;

    @Mock
    private AdminService mockAdminService;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    private AdminProductController controller;
    private ObjectMapper objectMapper;
    private StringWriter responseWriter;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        controller = new AdminProductController(mockAdminProductService, mockAdminService);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        responseWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(responseWriter);
        when(mockResponse.getWriter()).thenReturn(printWriter);
    }

    @Test
    public void testGetProducts_Success() throws Exception {
        // 准备测试数据
        Long adminId = 1L;
        Map<String, Object> result = new HashMap<>();
        result.put("products", new java.util.ArrayList<>());
        result.put("totalCount", 0);

        // Mock JWT验证
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(anyString())).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(anyString())).thenReturn(adminId);

            // Mock请求参数
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer valid-token");
            when(mockRequest.getPathInfo()).thenReturn("/");
            when(mockRequest.getParameter("keyword")).thenReturn("test");
            when(mockRequest.getParameter("status")).thenReturn("1");
            when(mockRequest.getParameter("pageNum")).thenReturn("1");
            when(mockRequest.getParameter("pageSize")).thenReturn("20");

            // Mock服务调用
            when(mockAdminService.hasPermission(adminId, "ADMIN")).thenReturn(true);
            when(mockAdminProductService.findProducts(any())).thenReturn(result);

            // 执行测试
            controller.doGet(mockRequest, mockResponse);

            // 验证
            verify(mockAdminProductService).findProducts(any());
            verify(mockResponse).setContentType("application/json;charset=UTF-8");
        }
    }

    @Test
    public void testGetProducts_Unauthorized() throws Exception {
        // Mock JWT验证失败
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(anyString())).thenReturn(false);

            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer invalid-token");
            when(mockRequest.getPathInfo()).thenReturn("/");

            // 执行测试
            controller.doGet(mockRequest, mockResponse);

            // 验证
            verify(mockAdminProductService, never()).findProducts(any());
            verify(mockResponse).setContentType("application/json;charset=UTF-8");
        }
    }

    @Test
    public void testGetProductDetail_Success() throws Exception {
        // 准备测试数据
        Long adminId = 1L;
        Long productId = 123L;
        Map<String, Object> result = new HashMap<>();
        result.put("product", new HashMap<>());

        // Mock JWT验证
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(anyString())).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(anyString())).thenReturn(adminId);

            // Mock请求参数
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer valid-token");
            when(mockRequest.getPathInfo()).thenReturn("/" + productId);

            // Mock服务调用
            when(mockAdminService.hasPermission(adminId, "ADMIN")).thenReturn(true);
            when(mockAdminProductService.getProductDetail(productId, adminId)).thenReturn(result);

            // 执行测试
            controller.doGet(mockRequest, mockResponse);

            // 验证
            verify(mockAdminProductService).getProductDetail(productId, adminId);
            verify(mockResponse).setContentType("application/json;charset=UTF-8");
        }
    }

    @Test
    public void testApproveProduct_Success() throws Exception {
        // 准备测试数据
        Long adminId = 1L;
        Long productId = 123L;
        AdminProductManageDTO manageDTO = new AdminProductManageDTO();
        manageDTO.setReason("审核通过");

        // Mock JWT验证
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(anyString())).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(anyString())).thenReturn(adminId);

            // Mock请求参数
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer valid-token");
            when(mockRequest.getPathInfo()).thenReturn("/" + productId + "/approve");
            when(mockRequest.getInputStream()).thenReturn(
                new javax.servlet.ServletInputStream() {
                    private final ByteArrayInputStream bis = new ByteArrayInputStream(
                        objectMapper.writeValueAsBytes(manageDTO));
                    
                    @Override
                    public int read() {
                        return bis.read();
                    }
                    
                    @Override
                    public boolean isFinished() { return false; }
                    @Override
                    public boolean isReady() { return true; }
                    @Override
                    public void setReadListener(javax.servlet.ReadListener readListener) {}
                }
            );

            // Mock服务调用
            when(mockAdminService.hasPermission(adminId, "ADMIN")).thenReturn(true);
            when(mockAdminProductService.approveProduct(productId, adminId, "审核通过")).thenReturn(true);

            // 执行测试
            controller.doPut(mockRequest, mockResponse);

            // 验证
            verify(mockAdminProductService).approveProduct(productId, adminId, "审核通过");
            verify(mockResponse).setContentType("application/json;charset=UTF-8");
        }
    }

    @Test
    public void testRejectProduct_Success() throws Exception {
        // 准备测试数据
        Long adminId = 1L;
        Long productId = 123L;
        AdminProductManageDTO manageDTO = new AdminProductManageDTO();
        manageDTO.setReason("不符合规范");

        // Mock JWT验证
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(anyString())).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(anyString())).thenReturn(adminId);

            // Mock请求参数
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer valid-token");
            when(mockRequest.getPathInfo()).thenReturn("/" + productId + "/reject");
            when(mockRequest.getInputStream()).thenReturn(
                new javax.servlet.ServletInputStream() {
                    private final ByteArrayInputStream bis = new ByteArrayInputStream(
                        objectMapper.writeValueAsBytes(manageDTO));
                    
                    @Override
                    public int read() {
                        return bis.read();
                    }
                    
                    @Override
                    public boolean isFinished() { return false; }
                    @Override
                    public boolean isReady() { return true; }
                    @Override
                    public void setReadListener(javax.servlet.ReadListener readListener) {}
                }
            );

            // Mock服务调用
            when(mockAdminService.hasPermission(adminId, "ADMIN")).thenReturn(true);
            when(mockAdminProductService.rejectProduct(productId, adminId, "不符合规范")).thenReturn(true);

            // 执行测试
            controller.doPut(mockRequest, mockResponse);

            // 验证
            verify(mockAdminProductService).rejectProduct(productId, adminId, "不符合规范");
            verify(mockResponse).setContentType("application/json;charset=UTF-8");
        }
    }

    @Test
    public void testRejectProduct_EmptyReason() throws Exception {
        // 准备测试数据
        Long adminId = 1L;
        Long productId = 123L;
        AdminProductManageDTO manageDTO = new AdminProductManageDTO();
        manageDTO.setReason(""); // 空原因

        // Mock JWT验证
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(anyString())).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(anyString())).thenReturn(adminId);

            // Mock请求参数
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer valid-token");
            when(mockRequest.getPathInfo()).thenReturn("/" + productId + "/reject");
            when(mockRequest.getInputStream()).thenReturn(
                new javax.servlet.ServletInputStream() {
                    private final ByteArrayInputStream bis = new ByteArrayInputStream(
                        objectMapper.writeValueAsBytes(manageDTO));
                    
                    @Override
                    public int read() {
                        return bis.read();
                    }
                    
                    @Override
                    public boolean isFinished() { return false; }
                    @Override
                    public boolean isReady() { return true; }
                    @Override
                    public void setReadListener(javax.servlet.ReadListener readListener) {}
                }
            );

            // Mock服务调用
            when(mockAdminService.hasPermission(adminId, "ADMIN")).thenReturn(true);

            // 执行测试
            controller.doPut(mockRequest, mockResponse);

            // 验证 - 不应该调用服务方法
            verify(mockAdminProductService, never()).rejectProduct(any(), any(), any());
            verify(mockResponse).setContentType("application/json;charset=UTF-8");
        }
    }

    @Test
    public void testDelistProduct_Success() throws Exception {
        // 准备测试数据
        Long adminId = 1L;
        Long productId = 123L;
        AdminProductManageDTO manageDTO = new AdminProductManageDTO();
        manageDTO.setReason("违规内容");

        // Mock JWT验证
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(anyString())).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(anyString())).thenReturn(adminId);

            // Mock请求参数
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer valid-token");
            when(mockRequest.getPathInfo()).thenReturn("/" + productId + "/delist");
            when(mockRequest.getInputStream()).thenReturn(
                new javax.servlet.ServletInputStream() {
                    private final ByteArrayInputStream bis = new ByteArrayInputStream(
                        objectMapper.writeValueAsBytes(manageDTO));
                    
                    @Override
                    public int read() {
                        return bis.read();
                    }
                    
                    @Override
                    public boolean isFinished() { return false; }
                    @Override
                    public boolean isReady() { return true; }
                    @Override
                    public void setReadListener(javax.servlet.ReadListener readListener) {}
                }
            );

            // Mock服务调用
            when(mockAdminService.hasPermission(adminId, "ADMIN")).thenReturn(true);
            when(mockAdminProductService.delistProduct(productId, adminId, "违规内容")).thenReturn(true);

            // 执行测试
            controller.doPut(mockRequest, mockResponse);

            // 验证
            verify(mockAdminProductService).delistProduct(productId, adminId, "违规内容");
            verify(mockResponse).setContentType("application/json;charset=UTF-8");
        }
    }

    @Test
    public void testDeleteProduct_Success() throws Exception {
        // 准备测试数据
        Long adminId = 1L;
        Long productId = 123L;

        // Mock JWT验证
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(anyString())).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(anyString())).thenReturn(adminId);

            // Mock请求参数
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer valid-token");
            when(mockRequest.getPathInfo()).thenReturn("/" + productId);

            // Mock服务调用
            when(mockAdminService.hasPermission(adminId, "ADMIN")).thenReturn(true);
            when(mockAdminProductService.deleteProduct(productId, adminId)).thenReturn(true);

            // 执行测试
            controller.doDelete(mockRequest, mockResponse);

            // 验证
            verify(mockAdminProductService).deleteProduct(productId, adminId);
            verify(mockResponse).setContentType("application/json;charset=UTF-8");
        }
    }

    @Test
    public void testInvalidProductId() throws Exception {
        // 准备测试数据
        Long adminId = 1L;

        // Mock JWT验证
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken(anyString())).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken(anyString())).thenReturn(adminId);

            // Mock请求参数 - 无效的商品ID
            when(mockRequest.getHeader("Authorization")).thenReturn("Bearer valid-token");
            when(mockRequest.getPathInfo()).thenReturn("/invalid-id");

            // Mock服务调用
            when(mockAdminService.hasPermission(adminId, "ADMIN")).thenReturn(true);

            // 执行测试
            controller.doDelete(mockRequest, mockResponse);

            // 验证 - 不应该调用服务方法
            verify(mockAdminProductService, never()).deleteProduct(any(), any());
            verify(mockResponse).setContentType("application/json;charset=UTF-8");
        }
    }
}
