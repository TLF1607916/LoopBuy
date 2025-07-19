package com.shiwu.user.controller;

import com.shiwu.common.result.Result;
import com.shiwu.common.util.JsonUtil;
import com.shiwu.user.model.*;
import com.shiwu.user.service.UserService;
import com.shiwu.user.service.impl.UserServiceImpl;
import com.shiwu.user.vo.FeedResponseVO;
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
import java.util.HashMap;
import java.util.Map;

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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null) {
            sendErrorResponse(resp, "404", "请求路径不存在");
            return;
        }

        // 处理 /api/user/{userId} 格式的请求
        if (pathInfo.matches("^/\\d+$")) {
            handleGetUserProfile(req, resp);
        }
        // 处理 /api/user/{userId}/follow 格式的请求
        else if (pathInfo.matches("^/\\d+/follow$")) {
            handleGetFollowStatus(req, resp);
        }
        // 处理 /api/user/follow/feed 格式的请求 (Task4_2_1_3)
        else if ("/follow/feed".equals(pathInfo)) {
            handleGetFollowingFeed(req, resp);
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

        // 处理关注用户请求 /api/user/{userId}/follow
        if (pathInfo.matches("^/\\d+/follow$")) {
            handleFollowUser(req, resp);
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

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null) {
            sendErrorResponse(resp, "404", "请求路径不存在");
            return;
        }

        // 处理取关用户请求 /api/user/{userId}/follow
        if (pathInfo.matches("^/\\d+/follow$")) {
            handleUnfollowUser(req, resp);
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
     * 处理获取用户公开信息请求
     * API: GET /api/user/{userId}
     */
    private void handleGetUserProfile(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // 从路径中提取用户ID
            String pathInfo = req.getPathInfo();
            String userIdStr = pathInfo.substring(1); // 去掉开头的 "/"
            Long userId;

            try {
                userId = Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                sendErrorResponse(resp, "A0201", "用户ID格式错误");
                return;
            }

            // 获取当前登录用户ID（从JWT token中解析，这里暂时设为null）
            // TODO: 实现JWT token解析获取当前用户ID
            Long currentUserId = null;

            // 调用服务获取用户公开信息
            UserProfileVO userProfile = userService.getUserProfile(userId, currentUserId);

            if (userProfile == null) {
                sendErrorResponse(resp, "A0120", "用户不存在或已被封禁");
                return;
            }

            // 返回成功结果
            sendSuccessResponse(resp, userProfile);

        } catch (Exception e) {
            logger.error("处理获取用户公开信息请求失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "B0001", "系统执行错误");
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
     * 处理关注用户请求
     * API: POST /api/user/{userId}/follow
     */
    private void handleFollowUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // 从路径中提取目标用户ID
            String pathInfo = req.getPathInfo();
            String userIdStr = pathInfo.substring(1, pathInfo.lastIndexOf("/follow")); // 去掉开头的 "/" 和结尾的 "/follow"
            Long targetUserId;

            try {
                targetUserId = Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                sendErrorResponse(resp, "A0201", "用户ID格式错误");
                return;
            }

            // 获取当前登录用户ID（从JWT token中解析）
            // TODO: 实现JWT token解析获取当前用户ID
            Long currentUserId = getCurrentUserIdFromToken(req);
            if (currentUserId == null) {
                sendErrorResponse(resp, "A0300", "请先登录");
                return;
            }

            // 调用服务执行关注操作
            FollowResult followResult = userService.followUser(currentUserId, targetUserId);

            if (followResult.isSuccess()) {
                // 返回成功结果
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("isFollowing", followResult.getIsFollowing());
                responseData.put("followerCount", followResult.getFollowerCount());
                sendSuccessResponse(resp, responseData);
            } else {
                // 返回详细的错误信息
                sendErrorResponse(resp, followResult.getError().getCode(), followResult.getError().getMessage());
            }

        } catch (Exception e) {
            logger.error("处理关注用户请求失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "B0001", "系统执行错误");
        }
    }

    /**
     * 处理取关用户请求
     * API: DELETE /api/user/{userId}/follow
     */
    private void handleUnfollowUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // 从路径中提取目标用户ID
            String pathInfo = req.getPathInfo();
            String userIdStr = pathInfo.substring(1, pathInfo.lastIndexOf("/follow")); // 去掉开头的 "/" 和结尾的 "/follow"
            Long targetUserId;

            try {
                targetUserId = Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                sendErrorResponse(resp, "A0201", "用户ID格式错误");
                return;
            }

            // 获取当前登录用户ID（从JWT token中解析）
            // TODO: 实现JWT token解析获取当前用户ID
            Long currentUserId = getCurrentUserIdFromToken(req);
            if (currentUserId == null) {
                sendErrorResponse(resp, "A0300", "请先登录");
                return;
            }

            // 调用服务执行取关操作
            FollowResult unfollowResult = userService.unfollowUser(currentUserId, targetUserId);

            if (unfollowResult.isSuccess()) {
                // 返回成功结果
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("isFollowing", unfollowResult.getIsFollowing());
                responseData.put("followerCount", unfollowResult.getFollowerCount());
                sendSuccessResponse(resp, responseData);
            } else {
                // 返回详细的错误信息
                sendErrorResponse(resp, unfollowResult.getError().getCode(), unfollowResult.getError().getMessage());
            }

        } catch (Exception e) {
            logger.error("处理取关用户请求失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "B0001", "系统执行错误");
        }
    }

    /**
     * 处理获取关注状态请求
     * API: GET /api/user/{userId}/follow
     */
    private void handleGetFollowStatus(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // 从路径中提取目标用户ID
            String pathInfo = req.getPathInfo();
            String userIdStr = pathInfo.substring(1, pathInfo.lastIndexOf("/follow")); // 去掉开头的 "/" 和结尾的 "/follow"
            Long targetUserId;

            try {
                targetUserId = Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                sendErrorResponse(resp, "A0201", "用户ID格式错误");
                return;
            }

            // 获取当前登录用户ID（从JWT token中解析，可为null）
            Long currentUserId = getCurrentUserIdFromToken(req);

            // 调用服务获取关注状态
            FollowStatusVO followStatus = userService.getFollowStatus(currentUserId, targetUserId);

            if (followStatus == null) {
                sendErrorResponse(resp, "A0120", "用户不存在");
                return;
            }

            // 返回成功结果
            sendSuccessResponse(resp, followStatus);

        } catch (Exception e) {
            logger.error("处理获取关注状态请求失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "B0001", "系统执行错误");
        }
    }

    /**
     * 从JWT token中获取当前用户ID
     * TODO: 实现JWT token解析
     */
    private Long getCurrentUserIdFromToken(HttpServletRequest req) {
        // 暂时返回null，表示未登录
        // 实际实现中应该从Authorization header中解析JWT token
        return null;
    }

    /**
     * 处理获取关注动态信息流请求
     * API: GET /api/user/follow/feed (Task4_2_1_3)
     */
    private void handleGetFollowingFeed(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // 获取当前登录用户ID（从JWT token中解析）
            Long currentUserId = getCurrentUserIdFromToken(req);
            if (currentUserId == null) {
                sendErrorResponse(resp, "A0300", "请先登录");
                return;
            }

            // 获取查询参数
            String pageStr = req.getParameter("page");
            String sizeStr = req.getParameter("size");
            String type = req.getParameter("type");

            // 设置默认值
            int page = 1;
            int size = 20;

            // 解析分页参数
            try {
                if (pageStr != null && !pageStr.trim().isEmpty()) {
                    page = Integer.parseInt(pageStr);
                }
                if (sizeStr != null && !sizeStr.trim().isEmpty()) {
                    size = Integer.parseInt(sizeStr);
                }
            } catch (NumberFormatException e) {
                sendErrorResponse(resp, "A0202", "分页参数格式错误");
                return;
            }

            // 设置默认动态类型
            if (type == null || type.trim().isEmpty()) {
                type = "ALL";
            }

            // 调用服务获取关注动态
            Result<FeedResponseVO> result = userService.getFollowingFeed(currentUserId, page, size, type);

            if (result.isSuccess()) {
                sendSuccessResponse(resp, result.getData());
            } else {
                sendErrorResponse(resp, "B0001", result.getMessage());
            }

        } catch (Exception e) {
            logger.error("处理获取关注动态请求失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "B0001", "系统执行错误");
        }
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