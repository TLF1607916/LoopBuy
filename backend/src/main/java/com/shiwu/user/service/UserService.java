package com.shiwu.user.service;

import com.shiwu.common.result.Result;
import com.shiwu.user.model.LoginResult;
import com.shiwu.user.model.RegisterRequest;
import com.shiwu.user.model.RegisterResult;
import com.shiwu.user.model.UserProfileVO;
import com.shiwu.user.model.UserProfileResult;
import com.shiwu.user.model.FollowResult;
import com.shiwu.user.model.FollowStatusVO;
import com.shiwu.user.vo.FeedResponseVO;

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

    /**
     * 获取用户公开信息
     * @param userId 用户ID
     * @param currentUserId 当前登录用户ID（可为null，用于判断是否关注）
     * @return 用户公开信息，如果用户不存在则返回null
     */
    UserProfileVO getUserProfile(Long userId, Long currentUserId);

    /**
     * 获取用户主页信息（增强版，包含详细错误处理）
     * @param userId 用户ID
     * @param currentUserId 当前登录用户ID（可为null，用于判断是否关注）
     * @return 用户主页访问结果，包含成功信息或详细的失败原因
     */
    UserProfileResult getUserProfileWithErrorHandling(Long userId, Long currentUserId);

    /**
     * 关注用户
     * @param currentUserId 当前登录用户ID
     * @param targetUserId 目标用户ID
     * @return 关注操作结果
     */
    FollowResult followUser(Long currentUserId, Long targetUserId);

    /**
     * 取关用户
     * @param currentUserId 当前登录用户ID
     * @param targetUserId 目标用户ID
     * @return 取关操作结果
     */
    FollowResult unfollowUser(Long currentUserId, Long targetUserId);

    /**
     * 获取关注状态
     * @param currentUserId 当前登录用户ID（可为null）
     * @param targetUserId 目标用户ID
     * @return 关注状态信息
     */
    FollowStatusVO getFollowStatus(Long currentUserId, Long targetUserId);

    /**
     * 获取关注动态信息流
     * Task4_2_1_3: 获取用户关注的卖家的商品动态
     *
     * @param userId 用户ID
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @param type 动态类型过滤（ALL, PRODUCT_APPROVED, PRODUCT_PUBLISHED）
     * @return 关注动态信息流
     */
    Result<FeedResponseVO> getFollowingFeed(Long userId, int page, int size, String type);

    /**
     *  更新用户平均评分
     * @param userId 用户ID
     * @return 更新是否成功 
     */
    boolean updateUserAverageRating(Long userId);
}