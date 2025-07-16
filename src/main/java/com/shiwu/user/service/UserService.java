package com.shiwu.user.service;

import com.shiwu.user.model.LoginResult;
import com.shiwu.user.model.RegisterRequest;
import com.shiwu.user.model.RegisterResult;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果，包含成功信息或失败原因
     */
    LoginResult login(String username, String password);
    
    /**
     * 用户注册
     * @param registerRequest 注册请求对象
     * @return 注册结果，包含成功信息或失败原因
     */
    RegisterResult register(RegisterRequest registerRequest);
}