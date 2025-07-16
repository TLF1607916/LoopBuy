package com.shiwu.user.service;

import com.shiwu.user.model.LoginResult;

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
}