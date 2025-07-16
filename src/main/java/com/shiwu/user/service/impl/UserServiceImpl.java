package com.shiwu.user.service.impl;

import com.shiwu.common.util.PasswordUtil;
import com.shiwu.user.dao.UserDao;
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
    private final UserDao userDao;

    public UserServiceImpl() {
        this.userDao = new UserDao();
    }

    @Override
    public UserVO login(String username, String password) {
        if (username == null || password == null) {
            logger.warn("登录失败: 用户名或密码为空");
            return null;
        }

        try {
            // 根据用户名查询用户
            User user = userDao.findByUsername(username);
            
            // 用户不存在
            if (user == null) {
                logger.warn("登录失败: 用户 {} 不存在", username);
                return null;
            }
            
            // 验证密码
            if (!PasswordUtil.matches(password, user.getPassword())) {
                logger.warn("登录失败: 用户 {} 密码错误", username);
                return null;
            }
            
            // 登录成功，转换为VO对象
            UserVO userVO = new UserVO();
            userVO.setId(user.getId());
            userVO.setUsername(user.getUsername());
            userVO.setEmail(user.getEmail());
            userVO.setPhone(user.getPhone());
            
            logger.info("用户 {} 登录成功", username);
            return userVO;
        } catch (Exception e) {
            logger.error("登录过程发生异常: {}", e.getMessage(), e);
            return null;
        }
    }
}