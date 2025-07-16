package com.shiwu.user.controller;

import com.shiwu.common.result.Result;
import com.shiwu.common.util.JsonUtil;
import com.shiwu.user.model.*;
import com.shiwu.user.service.UserService;
import com.shiwu.user.service.impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 用户控制器
 */
@WebServlet("/api/user/*")
public class UserController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController() {
        this.userService = new UserServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null) {
            sendErrorResponse(resp, "404", "请求路径不存在");
            return;
        }

        switch (pathInfo) {
            case "/login":
                handleLogin(req, resp);
                break;
            case "/register":
                handleRegister(req, resp);
                break;
            default:
                sendErrorResponse(resp, "404", "请求路径不存在");
        }
    }

    /**
     * 处理登录请求
     */
    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // 读取请求体
            BufferedReader reader = req.getReader();
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }

            // 解析JSON请求
            LoginRequest loginRequest = JsonUtil.fromJson(requestBody.toString(), LoginRequest.class);
            if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
                sendErrorResponse(resp, LoginErrorEnum.PARAMETER_ERROR.getCode(), LoginErrorEnum.PARAMETER_ERROR.getMessage());
                return;
            }

            // 调用服务进行登录验证
            LoginResult loginResult = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
            
            // 返回结果
            if (loginResult.getSuccess()) {
                sendSuccessResponse(resp, loginResult.getUserVO());
            } else {
                sendErrorResponse(resp, loginResult.getError().getCode(), loginResult.getError().getMessage());
            }
        } catch (Exception e) {
            logger.error("处理登录请求失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, LoginErrorEnum.SYSTEM_ERROR.getCode(), LoginErrorEnum.SYSTEM_ERROR.getMessage());
        }
    }
    
    /**
     * 处理注册请求
     */
    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // 读取请求体
            BufferedReader reader = req.getReader();
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }

            // 解析JSON请求
            RegisterRequest registerRequest = JsonUtil.fromJson(requestBody.toString(), RegisterRequest.class);
            if (registerRequest == null || registerRequest.getUsername() == null || registerRequest.getPassword() == null) {
                sendErrorResponse(resp, RegisterErrorEnum.PARAMETER_ERROR.getCode(), RegisterErrorEnum.PARAMETER_ERROR.getMessage());
                return;
            }

            // 调用服务进行注册
            RegisterResult registerResult = userService.register(registerRequest);
            
            // 返回结果
            if (registerResult.getSuccess()) {
                sendSuccessResponse(resp, registerResult.getUserVO());
            } else {
                sendErrorResponse(resp, registerResult.getError().getCode(), registerResult.getError().getMessage());
            }
        } catch (Exception e) {
            logger.error("处理注册请求失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, RegisterErrorEnum.SYSTEM_ERROR.getCode(), RegisterErrorEnum.SYSTEM_ERROR.getMessage());
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