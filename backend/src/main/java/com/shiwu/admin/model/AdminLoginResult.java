package com.shiwu.admin.model;

/**
 * 管理员登录结果类
 */
public class AdminLoginResult {
    private Boolean success;
    private AdminVO data;
    private AdminLoginErrorEnum error;

    private AdminLoginResult(Boolean success, AdminVO data, AdminLoginErrorEnum error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    /**
     * 创建成功结果
     * @param adminVO 管理员视图对象
     * @return 成功结果
     */
    public static AdminLoginResult success(AdminVO adminVO) {
        return new AdminLoginResult(true, adminVO, null);
    }

    /**
     * 创建失败结果
     * @param error 错误枚举
     * @return 失败结果
     */
    public static AdminLoginResult fail(AdminLoginErrorEnum error) {
        return new AdminLoginResult(false, null, error);
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public AdminVO getData() {
        return data;
    }

    public void setData(AdminVO data) {
        this.data = data;
    }

    public AdminLoginErrorEnum getError() {
        return error;
    }

    public void setError(AdminLoginErrorEnum error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "AdminLoginResult{" +
                "success=" + success +
                ", data=" + data +
                ", error=" + error +
                '}';
    }
}
