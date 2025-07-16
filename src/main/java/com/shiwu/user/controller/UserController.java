package com.shiwu.user.controller;

import com.shiwu.common.result.Result;
import com.shiwu.common.util.JsonUtil;
import com.shiwu.user.model.LoginRequest;
import com.shiwu.user.model.UserVO;
import com.shiwu.user.service.UserService;
import com.shiwu.user.service.impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 用户控制器
 */
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

        // 处理登录请求
        if ("/login".equals(pathInfo)) {
            handleLogin(req, resp);
        } else {
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
                sendErrorResponse(resp, "400", "无效的请求参数");
                return;
            }

            // 调用服务进行登录验证
            UserVO userVO = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
            
            // 返回结果
            if (userVO != null) {
                sendSuccessResponse(resp, userVO);
            } else {
                sendErrorResponse(resp, "401", "用户名或密码错误");
            }
        } catch (Exception e) {
            logger.error("处理登录请求失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "服务器内部错误");
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