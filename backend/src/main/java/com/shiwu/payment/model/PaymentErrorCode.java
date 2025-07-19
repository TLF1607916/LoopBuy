package com.shiwu.payment.model;

/**
 * 支付相关错误码常量
 */
public class PaymentErrorCode {
    
    // 参数错误
    public static final String INVALID_PARAMS = "PAYMENT_001";
    public static final String EMPTY_ORDER_LIST = "PAYMENT_002";
    public static final String INVALID_AMOUNT = "PAYMENT_003";
    public static final String INVALID_PAYMENT_METHOD = "PAYMENT_004";
    public static final String INVALID_PAYMENT_PASSWORD = "PAYMENT_005";
    
    // 订单相关错误
    public static final String ORDER_NOT_FOUND = "PAYMENT_101";
    public static final String ORDER_STATUS_INVALID = "PAYMENT_102";
    public static final String ORDER_AMOUNT_MISMATCH = "PAYMENT_103";
    public static final String ORDER_ALREADY_PAID = "PAYMENT_104";
    public static final String ORDER_EXPIRED = "PAYMENT_105";
    public static final String ORDER_PERMISSION_DENIED = "PAYMENT_106";
    
    // 支付操作错误
    public static final String PAYMENT_NOT_FOUND = "PAYMENT_201";
    public static final String PAYMENT_ALREADY_PROCESSED = "PAYMENT_202";
    public static final String PAYMENT_AMOUNT_ERROR = "PAYMENT_203";
    public static final String PAYMENT_METHOD_ERROR = "PAYMENT_204";
    public static final String PAYMENT_PASSWORD_ERROR = "PAYMENT_205";
    public static final String PAYMENT_TIMEOUT = "PAYMENT_206";
    public static final String PAYMENT_CANCELLED = "PAYMENT_207";
    public static final String PAYMENT_FAILED = "PAYMENT_208";
    
    // 系统错误
    public static final String SYSTEM_ERROR = "PAYMENT_500";
    public static final String UPDATE_ORDER_STATUS_FAILED = "PAYMENT_501";
    public static final String UPDATE_PRODUCT_STATUS_FAILED = "PAYMENT_502";
    
    // 错误信息
    public static final String MSG_INVALID_PARAMS = "请求参数不能为空";
    public static final String MSG_EMPTY_ORDER_LIST = "订单列表不能为空";
    public static final String MSG_INVALID_AMOUNT = "支付金额无效";
    public static final String MSG_INVALID_PAYMENT_METHOD = "支付方式无效";
    public static final String MSG_INVALID_PAYMENT_PASSWORD = "支付密码不能为空";
    
    public static final String MSG_ORDER_NOT_FOUND = "订单不存在或已被删除";
    public static final String MSG_ORDER_STATUS_INVALID = "订单状态不正确，无法支付";
    public static final String MSG_ORDER_AMOUNT_MISMATCH = "订单金额与支付金额不匹配";
    public static final String MSG_ORDER_ALREADY_PAID = "订单已支付，请勿重复支付";
    public static final String MSG_ORDER_EXPIRED = "订单已过期，无法支付";
    public static final String MSG_ORDER_PERMISSION_DENIED = "无权限操作此订单";
    
    public static final String MSG_PAYMENT_NOT_FOUND = "支付记录不存在";
    public static final String MSG_PAYMENT_ALREADY_PROCESSED = "支付已处理，请勿重复操作";
    public static final String MSG_PAYMENT_AMOUNT_ERROR = "支付金额错误";
    public static final String MSG_PAYMENT_METHOD_ERROR = "支付方式错误";
    public static final String MSG_PAYMENT_PASSWORD_ERROR = "支付密码错误";
    public static final String MSG_PAYMENT_TIMEOUT = "支付超时，订单已取消";
    public static final String MSG_PAYMENT_CANCELLED = "支付已取消";
    public static final String MSG_PAYMENT_FAILED = "支付失败，请稍后重试";
    
    public static final String MSG_SYSTEM_ERROR = "系统错误，请稍后重试";
    public static final String MSG_UPDATE_ORDER_STATUS_FAILED = "更新订单状态失败";
    public static final String MSG_UPDATE_PRODUCT_STATUS_FAILED = "更新商品状态失败";
}
