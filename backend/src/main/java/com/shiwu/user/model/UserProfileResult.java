package com.shiwu.user.model;

/**
 * 用户主页访问结果封装类
 * 用于UC-02: View User Profile的统一返回结果
 */
public class UserProfileResult {
    private Boolean success;
    private UserProfileVO userProfile;
    private UserProfileErrorEnum error;
    
    /**
     * 私有构造函数，使用静态工厂方法创建实例
     */
    private UserProfileResult() {
    }
    
    /**
     * 创建成功的用户主页访问结果
     * 
     * @param userProfile 用户主页信息
     * @return 访问结果
     */
    public static UserProfileResult success(UserProfileVO userProfile) {
        UserProfileResult result = new UserProfileResult();
        result.setSuccess(true);
        result.setUserProfile(userProfile);
        return result;
    }
    
    /**
     * 创建失败的用户主页访问结果
     * 
     * @param error 错误枚举
     * @return 访问结果
     */
    public static UserProfileResult fail(UserProfileErrorEnum error) {
        UserProfileResult result = new UserProfileResult();
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
    
    public UserProfileVO getUserProfile() {
        return userProfile;
    }
    
    public void setUserProfile(UserProfileVO userProfile) {
        this.userProfile = userProfile;
    }
    
    public UserProfileErrorEnum getError() {
        return error;
    }
    
    public void setError(UserProfileErrorEnum error) {
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
