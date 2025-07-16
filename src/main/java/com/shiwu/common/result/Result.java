package com.shiwu.common.result;

/**
 * 统一API响应结果封装
 * @param <T> 数据类型
 */
public class Result<T> {
    private Boolean success;
    private T data;
    private ErrorInfo error;

    private Result() {
    }

    /**
     * 成功返回结果
     * @param data 返回的数据
     * @param <T> 数据类型
     * @return 成功的结果对象
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setData(data);
        return result;
    }

    /**
     * 失败返回结果
     * @param errorCode 错误码
     * @param errorMessage 错误信息
     * @param <T> 数据类型
     * @return 失败的结果对象
     */
    public static <T> Result<T> fail(String errorCode, String errorMessage) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        ErrorInfo errorInfo = new ErrorInfo(errorCode, errorMessage);
        result.setError(errorInfo);
        return result;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ErrorInfo getError() {
        return error;
    }

    public void setError(ErrorInfo error) {
        this.error = error;
    }

    /**
     * 错误信息类
     */
    public static class ErrorInfo {
        private String code;
        private String message;

        public ErrorInfo(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}