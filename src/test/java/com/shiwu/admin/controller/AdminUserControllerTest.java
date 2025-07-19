package com.shiwu.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shiwu.admin.model.AdminUserManageDTO;
import com.shiwu.admin.service.AdminService;
import com.shiwu.common.result.Result;
import com.shiwu.common.util.JwtUtil;
import com.shiwu.user.service.AdminUserService;
import org.junit.jupiter.api.AfterEach;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 管理员用户控制器测试类
 */
public class AdminUserControllerTest {

    @Mock
    private AdminUserService adminUserService;

    @Mock
    private AdminService adminService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private AdminUserController controller;
    private ObjectMapper objectMapper;
    private StringWriter responseWriter;
    private MockedStatic<JwtUtil> jwtUtilMock;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        controller = new AdminUserController(adminUserService, adminService);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        responseWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(responseWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // Mock JwtUtil静态方法
        jwtUtilMock = mockStatic(JwtUtil.class);
        jwtUtilMock.when(() -> JwtUtil.validateToken("valid_token")).thenReturn(true);
        jwtUtilMock.when(() -> JwtUtil.validateToken("invalid_token")).thenReturn(false);
        jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken("valid_token")).thenReturn(1L);
        jwtUtilMock.when(() -> JwtUtil.getUserIdFromToken("invalid_token")).thenReturn(null);
    }

    @AfterEach
    void tearDown() {
        if (jwtUtilMock != null) {
            jwtUtilMock.close();
        }
    }

    @Test
    void testGetUsers_Success() throws Exception {
        // 准备测试数据
        when(request.getHeader("Authorization")).thenReturn("Bearer valid_token");
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("keyword")).thenReturn("test");
        when(request.getParameter("status")).thenReturn("0");
        when(request.getParameter("pageNum")).thenReturn("1");
        when(request.getParameter("pageSize")).thenReturn("20");

        // Mock服务调用 - 模拟权限验证通过
        when(adminService.hasPermission(anyLong(), eq("ADMIN"))).thenReturn(true);

        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("users", Arrays.asList());
        mockResult.put("totalCount", 0);
        when(adminUserService.findUsers(any())).thenReturn(mockResult);

        // 执行测试
        controller.doGet(request, response);

        // 验证结果
        verify(adminUserService).findUsers(any());
        verify(response).setContentType("application/json;charset=UTF-8");
    }

    @Test
    void testGetUsers_Unauthorized() throws Exception {
        // 准备测试数据
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid_token");
        when(request.getPathInfo()).thenReturn("/");

        // 执行测试
        controller.doGet(request, response);

        // 验证结果
        verify(adminUserService, never()).findUsers(any());
        verify(response).setContentType("application/json;charset=UTF-8");
    }

    @Test
    void testGetUserDetail_Success() throws Exception {
        // 准备测试数据
        when(request.getHeader("Authorization")).thenReturn("Bearer valid_token");
        when(request.getPathInfo()).thenReturn("/123");

        // Mock服务调用
        when(adminService.hasPermission(anyLong(), eq("ADMIN"))).thenReturn(true);

        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("user", new HashMap<>());
        when(adminUserService.getUserDetail(eq(123L), anyLong())).thenReturn(mockResult);

        // 执行测试
        controller.doGet(request, response);

        // 验证结果
        verify(adminUserService).getUserDetail(eq(123L), anyLong());
        verify(response).setContentType("application/json;charset=UTF-8");
    }

    @Test
    void testGetUserDetail_NotFound() throws Exception {
        // 准备测试数据
        when(request.getHeader("Authorization")).thenReturn("Bearer valid_token");
        when(request.getPathInfo()).thenReturn("/999");

        // Mock服务调用
        when(adminService.hasPermission(anyLong(), eq("ADMIN"))).thenReturn(true);
        when(adminUserService.getUserDetail(eq(999L), anyLong())).thenReturn(null);

        // 执行测试
        controller.doGet(request, response);

        // 验证结果
        verify(adminUserService).getUserDetail(eq(999L), anyLong());
        verify(response).setContentType("application/json;charset=UTF-8");
    }

    @Test
    void testBanUser_Success() throws Exception {
        // 准备测试数据
        when(request.getHeader("Authorization")).thenReturn("Bearer valid_token");
        when(request.getPathInfo()).thenReturn("/123/ban");
        
        AdminUserManageDTO manageDTO = new AdminUserManageDTO();
        manageDTO.setReason("violation");
        String requestBody = objectMapper.writeValueAsString(manageDTO);
        when(request.getInputStream()).thenReturn(new MockServletInputStream(requestBody));

        // Mock服务调用
        when(adminService.hasPermission(anyLong(), eq("ADMIN"))).thenReturn(true);
        when(adminUserService.banUser(eq(123L), anyLong(), eq("violation"))).thenReturn(true);
        
        // 执行测试
        controller.doPut(request, response);
        
        // 验证结果
        verify(adminUserService).banUser(eq(123L), anyLong(), eq("violation"));
        verify(response).setContentType("application/json;charset=UTF-8");
    }

    @Test
    void testBanUser_Failed() throws Exception {
        // 准备测试数据
        when(request.getHeader("Authorization")).thenReturn("Bearer valid_token");
        when(request.getPathInfo()).thenReturn("/123/ban");
        
        AdminUserManageDTO manageDTO = new AdminUserManageDTO();
        manageDTO.setReason("violation");
        String requestBody = objectMapper.writeValueAsString(manageDTO);
        when(request.getInputStream()).thenReturn(new MockServletInputStream(requestBody));

        // Mock服务调用
        when(adminService.hasPermission(anyLong(), eq("ADMIN"))).thenReturn(true);
        when(adminUserService.banUser(eq(123L), anyLong(), eq("violation"))).thenReturn(false);

        // 执行测试
        controller.doPut(request, response);

        // 验证结果
        verify(adminUserService).banUser(eq(123L), anyLong(), eq("violation"));
        verify(response).setContentType("application/json;charset=UTF-8");
    }

    @Test
    void testMuteUser_Success() throws Exception {
        // 准备测试数据
        when(request.getHeader("Authorization")).thenReturn("Bearer valid_token");
        when(request.getPathInfo()).thenReturn("/123/mute");
        
        AdminUserManageDTO manageDTO = new AdminUserManageDTO();
        manageDTO.setReason("inappropriate speech");
        String requestBody = objectMapper.writeValueAsString(manageDTO);
        when(request.getInputStream()).thenReturn(new MockServletInputStream(requestBody));

        // Mock服务调用
        when(adminService.hasPermission(anyLong(), eq("ADMIN"))).thenReturn(true);
        when(adminUserService.muteUser(eq(123L), anyLong(), eq("inappropriate speech"))).thenReturn(true);

        // 执行测试
        controller.doPut(request, response);

        // 验证结果
        verify(adminUserService).muteUser(eq(123L), anyLong(), eq("inappropriate speech"));
        verify(response).setContentType("application/json;charset=UTF-8");
    }

    @Test
    void testUnbanUser_Success() throws Exception {
        // 准备测试数据
        when(request.getHeader("Authorization")).thenReturn("Bearer valid_token");
        when(request.getPathInfo()).thenReturn("/123/unban");
        
        // Mock服务调用
        when(adminService.hasPermission(anyLong(), eq("ADMIN"))).thenReturn(true);
        when(adminUserService.unbanUser(eq(123L), anyLong())).thenReturn(true);

        // 执行测试
        controller.doPut(request, response);

        // 验证结果
        verify(adminUserService).unbanUser(eq(123L), anyLong());
        verify(response).setContentType("application/json;charset=UTF-8");
    }

    @Test
    void testUnmuteUser_Success() throws Exception {
        // 准备测试数据
        when(request.getHeader("Authorization")).thenReturn("Bearer valid_token");
        when(request.getPathInfo()).thenReturn("/123/unmute");
        
        // Mock服务调用
        when(adminService.hasPermission(anyLong(), eq("ADMIN"))).thenReturn(true);
        when(adminUserService.unmuteUser(eq(123L), anyLong())).thenReturn(true);

        // 执行测试
        controller.doPut(request, response);

        // 验证结果
        verify(adminUserService).unmuteUser(eq(123L), anyLong());
        verify(response).setContentType("application/json;charset=UTF-8");
    }

    @Test
    void testBatchBanUsers_Success() throws Exception {
        // 准备测试数据
        when(request.getHeader("Authorization")).thenReturn("Bearer valid_token");
        when(request.getPathInfo()).thenReturn("/batch-ban");
        
        AdminUserManageDTO manageDTO = new AdminUserManageDTO();
        manageDTO.setUserIds(Arrays.asList(1L, 2L, 3L));
        manageDTO.setReason("batch violation");
        String requestBody = objectMapper.writeValueAsString(manageDTO);
        when(request.getInputStream()).thenReturn(new MockServletInputStream(requestBody));

        // Mock服务调用
        when(adminService.hasPermission(anyLong(), eq("ADMIN"))).thenReturn(true);

        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("totalCount", 3);
        mockResult.put("successCount", 2);
        mockResult.put("failCount", 1);
        when(adminUserService.batchBanUsers(eq(Arrays.asList(1L, 2L, 3L)), anyLong(), eq("batch violation"))).thenReturn(mockResult);

        // 执行测试
        controller.doPost(request, response);

        // 验证结果
        verify(adminUserService).batchBanUsers(eq(Arrays.asList(1L, 2L, 3L)), anyLong(), eq("batch violation"));
        verify(response).setContentType("application/json;charset=UTF-8");
    }

    @Test
    void testBatchBanUsers_EmptyUserIds() throws Exception {
        // 准备测试数据
        when(request.getHeader("Authorization")).thenReturn("Bearer valid_token");
        when(request.getPathInfo()).thenReturn("/batch-ban");
        
        AdminUserManageDTO manageDTO = new AdminUserManageDTO();
        manageDTO.setUserIds(Arrays.asList());
        manageDTO.setReason("batch violation");
        String requestBody = objectMapper.writeValueAsString(manageDTO);
        when(request.getInputStream()).thenReturn(new MockServletInputStream(requestBody));
        
        // Mock服务调用
        when(adminService.hasPermission(anyLong(), eq("ADMIN"))).thenReturn(true);
        
        // 执行测试
        controller.doPost(request, response);
        
        // 验证结果
        verify(adminUserService, never()).batchBanUsers(any(), any(), any());
        verify(response).setContentType("application/json;charset=UTF-8");
    }

    @Test
    void testBatchMuteUsers_Success() throws Exception {
        // 准备测试数据
        when(request.getHeader("Authorization")).thenReturn("Bearer valid_token");
        when(request.getPathInfo()).thenReturn("/batch-mute");
        
        AdminUserManageDTO manageDTO = new AdminUserManageDTO();
        manageDTO.setUserIds(Arrays.asList(1L, 2L));
        manageDTO.setReason("batch mute");
        String requestBody = objectMapper.writeValueAsString(manageDTO);
        when(request.getInputStream()).thenReturn(new MockServletInputStream(requestBody));

        // Mock服务调用
        when(adminService.hasPermission(anyLong(), eq("ADMIN"))).thenReturn(true);

        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("totalCount", 2);
        mockResult.put("successCount", 2);
        mockResult.put("failCount", 0);
        when(adminUserService.batchMuteUsers(eq(Arrays.asList(1L, 2L)), anyLong(), eq("batch mute"))).thenReturn(mockResult);

        // 执行测试
        controller.doPost(request, response);

        // 验证结果
        verify(adminUserService).batchMuteUsers(eq(Arrays.asList(1L, 2L)), anyLong(), eq("batch mute"));
        verify(response).setContentType("application/json;charset=UTF-8");
    }

    @Test
    void testInvalidPath() throws Exception {
        // 准备测试数据
        when(request.getHeader("Authorization")).thenReturn("Bearer valid_token");
        when(request.getPathInfo()).thenReturn("/invalid");
        
        // Mock服务调用
        when(adminService.hasPermission(anyLong(), eq("ADMIN"))).thenReturn(true);
        
        // 执行测试
        controller.doGet(request, response);
        
        // 验证结果
        verify(response).setContentType("application/json;charset=UTF-8");
    }

    @Test
    void testInvalidUserId() throws Exception {
        // 准备测试数据
        when(request.getHeader("Authorization")).thenReturn("Bearer valid_token");
        when(request.getPathInfo()).thenReturn("/invalid_id");
        
        // Mock服务调用
        when(adminService.hasPermission(anyLong(), eq("ADMIN"))).thenReturn(true);
        
        // 执行测试
        controller.doGet(request, response);
        
        // 验证结果
        verify(response).setContentType("application/json;charset=UTF-8");
    }

    // Mock ServletInputStream 类
    private static class MockServletInputStream extends javax.servlet.ServletInputStream {
        private final ByteArrayInputStream inputStream;

        public MockServletInputStream(String data) {
            this.inputStream = new ByteArrayInputStream(data.getBytes());
        }

        @Override
        public int read() {
            return inputStream.read();
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(javax.servlet.ReadListener readListener) {
            // Not implemented for testing
        }
    }
}
