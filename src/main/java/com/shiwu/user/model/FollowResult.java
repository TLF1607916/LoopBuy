package com.shiwu.user.model;

/**
 * 关注操作结果封装类
 * 用于关注/取关功能的统一返回结果
 */
public class FollowResult {
    private Boolean success;
    private Boolean isFollowing; // 当前关注状态
    private Integer followerCount; // 被关注者的粉丝数量
    private FollowErrorEnum error;
    
    /**
     * 私有构造函数，使用静态工厂方法创建实例
     */
    private FollowResult() {
    }
    
    /**
     * 创建成功的关注操作结果
     * 
     * @param isFollowing 当前关注状态
     * @param followerCount 被关注者的粉丝数量
     * @return 操作结果
     */
    public static FollowResult success(Boolean isFollowing, Integer followerCount) {
        FollowResult result = new FollowResult();
        result.setSuccess(true);
        result.setIsFollowing(isFollowing);
        result.setFollowerCount(followerCount);
        return result;
    }
    
    /**
     * 创建失败的关注操作结果
     * 
     * @param error 错误枚举
     * @return 操作结果
     */
    public static FollowResult fail(FollowErrorEnum error) {
        FollowResult result = new FollowResult();
        result.setSuccess(false);
        result.setError(error);
        return result;
    }
    
    public Boolean getSuccess() {
        return success;
    }
    
    public void setSuccess(Boolean success) {
        this.success = success;
    }
    
    public Boolean getIsFollowing() {
        return isFollowing;
    }
    
    public void setIsFollowing(Boolean isFollowing) {
        this.isFollowing = isFollowing;
    }
    
    public Integer getFollowerCount() {
        return followerCount;
    }
    
    public void setFollowerCount(Integer followerCount) {
        this.followerCount = followerCount;
    }
    
    public FollowErrorEnum getError() {
        return error;
    }
    
    public void setError(FollowErrorEnum error) {
        this.error = error;
    }
    
    /**
     * 判断是否成功
     * @return true if successful, false otherwise
     */
    public boolean isSuccess() {
        return Boolean.TRUE.equals(success);
    }
    
    /**
     * 判断是否失败
     * @return true if failed, false otherwise
     */
    public boolean isFailed() {
        return !isSuccess();
    }
}
