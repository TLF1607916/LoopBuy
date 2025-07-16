package com.shiwu.user.service.impl;

import com.shiwu.common.util.JwtUtil;
import com.shiwu.common.util.PasswordUtil;
import com.shiwu.user.dao.UserDao;
import com.shiwu.user.model.LoginErrorEnum;
import com.shiwu.user.model.LoginResult;
import com.shiwu.user.model.User;
import com.shiwu.user.model.UserVO;
import com.shiwu.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户服务实现类
 */
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final Integer USER_STATUS_NORMAL = 0;
    private static final Integer USER_STATUS_BANNED = 1;
    
    private final UserDao userDao;

    public UserServiceImpl() {
        this.userDao = new UserDao();
    }

    @Override
    public LoginResult login(String username, String password) {
        // 参数校验
        if (username == null || password == null) {
            logger.warn("登录失败: 用户名或密码为空");
            return LoginResult.fail(LoginErrorEnum.PARAMETER_ERROR);
        }

        try {
            // 根据用户名查询用户
            User user = userDao.findByUsername(username);
            
            // 用户不存在
            if (user == null) {
                logger.warn("登录失败: 用户 {} 不存在", username);
                return LoginResult.fail(LoginErrorEnum.USER_NOT_FOUND);
            }
            
            // 检查账户状态
            if (USER_STATUS_BANNED.equals(user.getStatus())) {
                logger.warn("登录失败: 用户 {} 账户已被封禁", username);
                return LoginResult.fail(LoginErrorEnum.ACCOUNT_BANNED);
            }
            
            // 验证密码
            if (!PasswordUtil.matches(password, user.getPassword())) {
                logger.warn("登录失败: 用户 {} 密码错误", username);
                return LoginResult.fail(LoginErrorEnum.WRONG_PASSWORD);
            }
            
            // 登录成功，转换为VO对象
            UserVO userVO = new UserVO();
            userVO.setId(user.getId());
            userVO.setUsername(user.getUsername());
            userVO.setEmail(user.getEmail());
            userVO.setPhone(user.getPhone());
            
            // 生成JWT令牌
            String token = JwtUtil.generateToken(user.getId(), user.getUsername());
            if (token == null) {
                logger.error("用户 {} 登录成功但生成JWT令牌失败", username);
                return LoginResult.fail(LoginErrorEnum.SYSTEM_ERROR);
            }
            
            userVO.setToken(token);
            logger.info("用户 {} 登录成功并生成JWT令牌", username);
            return LoginResult.success(userVO);
        } catch (Exception e) {
            logger.error("登录过程发生异常: {}", e.getMessage(), e);
            return LoginResult.fail(LoginErrorEnum.SYSTEM_ERROR);
        }
    }
}