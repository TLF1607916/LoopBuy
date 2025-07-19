package com.shiwu.user.model;

/**
 * 登录结果封装类
 */
public class LoginResult {
    private Boolean success;
    private UserVO userVO;
    private LoginErrorEnum error;
    
    /**
     * 私有构造函数，使用静态工厂方法创建实例
     */
    private LoginResult() {
    }
    
    /**
     * 创建成功的登录结果
     * 
     * @param userVO 用户视图对象
     * @return 登录结果
     */
    public static LoginResult success(UserVO userVO) {
        LoginResult result = new LoginResult();
        result.setSuccess(true);
        result.setUserVO(userVO);
        return result;
    }
    
    /**
     * 创建失败的登录结果
     * 
     * @param error 登录错误枚举
     * @return 登录结果
     */
    public static LoginResult fail(LoginErrorEnum error) {
        LoginResult result = new LoginResult();
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
    
    public UserVO getUserVO() {
        return userVO;
    }
    
    public void setUserVO(UserVO userVO) {
        this.userVO = userVO;
    }
    
    public LoginErrorEnum getError() {
        return error;
    }
    
    public void setError(LoginErrorEnum error) {
        this.error = error;
    }
} 