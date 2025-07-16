package com.shiwu.user.service;

import com.shiwu.user.model.UserVO;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回用户信息，失败返回null
     */
    UserVO login(String username, String password);
}