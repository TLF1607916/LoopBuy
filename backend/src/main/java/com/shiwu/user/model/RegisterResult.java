package com.shiwu.user.model;

/**
 * 注册结果封装类
 */
public class RegisterResult {
    private Boolean success;
    private UserVO userVO;
    private RegisterErrorEnum error;
    
    /**
     * 私有构造函数，使用静态工厂方法创建实例
     */
    private RegisterResult() {
    }
    
    /**
     * 创建成功的注册结果
     * 
     * @param userVO 用户视图对象
     * @return 注册结果
     */
    public static RegisterResult success(UserVO userVO) {
        RegisterResult result = new RegisterResult();
        result.setSuccess(true);
        result.setUserVO(userVO);
        return result;
    }
    
    /**
     * 创建失败的注册结果
     * 
     * @param error 注册错误枚举
     * @return 注册结果
     */
    public static RegisterResult fail(RegisterErrorEnum error) {
        RegisterResult result = new RegisterResult();
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
    
    public RegisterErrorEnum getError() {
        return error;
    }
    
    public void setError(RegisterErrorEnum error) {
        this.error = error;
    }
} 