package com.shiwu.user.service.impl;

import com.shiwu.common.util.JwtUtil;
import com.shiwu.common.util.PasswordUtil;
import com.shiwu.user.dao.UserDao;
import com.shiwu.user.model.*;
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
            
            // 获取当前存储的密码哈希
            String storedHash = user.getPassword();
            
            // 验证密码
            boolean passwordMatches = false;
            
            // 检查是否是旧格式密码（非BCrypt格式）
            if (!PasswordUtil.isBCryptHash(storedHash)) {
                // 使用旧方式验证密码
                if (PasswordUtil.legacyMatches(password, storedHash)) {
                    passwordMatches = true;
                    
                    // 密码验证成功，升级到BCrypt格式
                    String bcryptHash = PasswordUtil.encrypt(password);
                    if (bcryptHash != null) {
                        // 更新数据库中的密码哈希
                        if (userDao.updatePassword(user.getId(), bcryptHash)) {
                            logger.info("用户 {} 的密码哈希已从旧格式升级到BCrypt格式", username);
                        } else {
                            logger.warn("用户 {} 的密码哈希升级失败", username);
                        }
                    }
                }
            } else {
                // 使用BCrypt验证密码
                passwordMatches = PasswordUtil.matches(password, storedHash);
            }
            
            // 密码不正确
            if (!passwordMatches) {
                logger.warn("登录失败: 用户 {} 密码错误", username);
                return LoginResult.fail(LoginErrorEnum.WRONG_PASSWORD);
            }
            
            // 更新用户最后登录时间
            userDao.updateLastLoginTime(user.getId());
            
            // 登录成功，转换为VO对象
            UserVO userVO = convertToVO(user);
            
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
    
    @Override
    public RegisterResult register(RegisterRequest registerRequest) {
        // 参数校验
        if (registerRequest == null || registerRequest.getUsername() == null || registerRequest.getPassword() == null) {
            logger.warn("注册失败: 必填参数为空");
            return RegisterResult.fail(RegisterErrorEnum.PARAMETER_ERROR);
        }
        
        try {
            // 检查密码强度
            if (!PasswordUtil.isStrongPassword(registerRequest.getPassword())) {
                logger.warn("注册失败: 密码强度不足");
                return RegisterResult.fail(RegisterErrorEnum.WEAK_PASSWORD);
            }
            
            // 检查用户名唯一性
            User existingUser = userDao.findByUsername(registerRequest.getUsername());
            if (existingUser != null) {
                logger.warn("注册失败: 用户名 {} 已存在", registerRequest.getUsername());
                return RegisterResult.fail(RegisterErrorEnum.USERNAME_EXISTS);
            }
            
            // 检查邮箱唯一性
            if (registerRequest.getEmail() != null && !registerRequest.getEmail().isEmpty()) {
                existingUser = userDao.findByEmail(registerRequest.getEmail());
                if (existingUser != null) {
                    logger.warn("注册失败: 邮箱 {} 已被注册", registerRequest.getEmail());
                    return RegisterResult.fail(RegisterErrorEnum.EMAIL_EXISTS);
                }
            }
            
            // 检查手机号唯一性
            if (registerRequest.getPhone() != null && !registerRequest.getPhone().isEmpty()) {
                existingUser = userDao.findByPhone(registerRequest.getPhone());
                if (existingUser != null) {
                    logger.warn("注册失败: 手机号 {} 已被注册", registerRequest.getPhone());
                    return RegisterResult.fail(RegisterErrorEnum.PHONE_EXISTS);
                }
            }
            
            // 创建新用户
            User newUser = new User();
            newUser.setUsername(registerRequest.getUsername());
            // 使用BCrypt加盐哈希处理密码
            String hashedPassword = PasswordUtil.encrypt(registerRequest.getPassword());
            if (hashedPassword == null) {
                logger.error("注册失败: 密码加密错误");
                return RegisterResult.fail(RegisterErrorEnum.SYSTEM_ERROR);
            }
            newUser.setPassword(hashedPassword);
            newUser.setEmail(registerRequest.getEmail());
            newUser.setPhone(registerRequest.getPhone());
            newUser.setNickname(registerRequest.getNickname());
            newUser.setSchool(registerRequest.getSchool());
            newUser.setStatus(USER_STATUS_NORMAL);
            
            Long userId = userDao.createUser(newUser);
            if (userId == null) {
                logger.error("注册失败: 创建用户数据库记录失败");
                return RegisterResult.fail(RegisterErrorEnum.SYSTEM_ERROR);
            }
            
            // 设置用户ID
            newUser.setId(userId);
            
            // 转换为VO对象
            UserVO userVO = convertToVO(newUser);
            
            // 生成JWT令牌
            String token = JwtUtil.generateToken(userId, newUser.getUsername());
            if (token == null) {
                logger.error("用户 {} 注册成功但生成JWT令牌失败", newUser.getUsername());
                return RegisterResult.fail(RegisterErrorEnum.SYSTEM_ERROR);
            }
            
            userVO.setToken(token);
            logger.info("用户 {} 注册成功并生成JWT令牌", newUser.getUsername());
            return RegisterResult.success(userVO);
        } catch (Exception e) {
            logger.error("注册过程发生异常: {}", e.getMessage(), e);
            return RegisterResult.fail(RegisterErrorEnum.SYSTEM_ERROR);
        }
    }

    @Override
    public UserProfileVO getUserProfile(Long userId, Long currentUserId) {
        // 参数校验
        if (userId == null) {
            logger.warn("获取用户公开信息失败: 用户ID为空");
            return null;
        }

        try {
            // 获取用户公开信息
            User user = userDao.findPublicInfoById(userId);
            if (user == null) {
                logger.warn("获取用户公开信息失败: 用户 {} 不存在", userId);
                return null;
            }

            // 检查用户状态，被封禁的用户不显示公开信息
            if (USER_STATUS_BANNED.equals(user.getStatus())) {
                logger.warn("获取用户公开信息失败: 用户 {} 已被封禁", userId);
                return null;
            }

            // 构建UserProfileVO
            UserProfileVO profileVO = new UserProfileVO();

            // 设置基本用户信息
            UserVO userVO = new UserVO();
            userVO.setId(user.getId());
            userVO.setUsername(user.getUsername());
            userVO.setNickname(user.getNickname());
            userVO.setAvatarUrl(user.getAvatarUrl());
            profileVO.setUser(userVO);

            // 设置统计信息
            profileVO.setFollowerCount(user.getFollowerCount());
            profileVO.setAverageRating(user.getAverageRating());
            profileVO.setRegistrationDate(user.getCreateTime());

            // 获取在售商品列表
            // 注意：根据模块解耦原则，这里应该调用ProductService而不是直接调用ProductDao
            // 但由于产品模块还未实现，暂时返回空列表
            profileVO.setOnSaleProducts(userDao.findOnSaleProductsByUserId(userId));

            // 判断当前用户是否关注了该用户
            // 注意：这里需要查询user_follow表，但为了遵循模块解耦原则，
            // 应该有专门的FollowService来处理关注关系
            // 暂时设置为false
            profileVO.setIsFollowing(false);

            logger.info("成功获取用户 {} 的公开信息", userId);
            return profileVO;
        } catch (Exception e) {
            logger.error("获取用户公开信息过程发生异常: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将User实体转换为UserVO视图对象
     * @param user 用户实体
     * @return 用户视图对象
     */
    private UserVO convertToVO(User user) {
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setEmail(user.getEmail());
        userVO.setPhone(user.getPhone());
        userVO.setStatus(user.getStatus());
        userVO.setAvatarUrl(user.getAvatarUrl());
        userVO.setNickname(user.getNickname());
        userVO.setGender(user.getGender());
        userVO.setSchool(user.getSchool());
        return userVO;
    }
}