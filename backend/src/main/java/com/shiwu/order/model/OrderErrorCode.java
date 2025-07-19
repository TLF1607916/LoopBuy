package com.shiwu.order.model;

/**
 * 订单相关错误码常量
 */
public class OrderErrorCode {
    
    // 参数错误
    public static final String INVALID_PARAMS = "ORDER_001";
    public static final String EMPTY_PRODUCT_LIST = "ORDER_002";
    
    // 商品相关错误
    public static final String PRODUCT_NOT_FOUND = "ORDER_101";
    public static final String PRODUCT_NOT_AVAILABLE = "ORDER_102";
    public static final String CANT_BUY_OWN_PRODUCT = "ORDER_103";
    public static final String PRODUCT_STATUS_CHANGED = "ORDER_104";
    
    // 订单操作错误
    public static final String CREATE_ORDER_FAILED = "ORDER_201";
    public static final String ORDER_NOT_FOUND = "ORDER_202";
    public static final String UPDATE_ORDER_STATUS_FAILED = "ORDER_203";
    public static final String UPDATE_PRODUCT_STATUS_FAILED = "ORDER_204";

    // 发货相关错误
    public static final String SHIP_PERMISSION_DENIED = "ORDER_301";
    public static final String ORDER_STATUS_NOT_AWAITING_SHIPPING = "ORDER_302";
    public static final String SHIP_ORDER_FAILED = "ORDER_303";

    // 确认收货相关错误
    public static final String CONFIRM_RECEIPT_PERMISSION_DENIED = "ORDER_401";
    public static final String ORDER_STATUS_NOT_SHIPPED = "ORDER_402";
    public static final String CONFIRM_RECEIPT_FAILED = "ORDER_403";
    public static final String UPDATE_PRODUCT_TO_SOLD_FAILED = "ORDER_404";

    // 系统错误
    public static final String SYSTEM_ERROR = "ORDER_500";
    
    // 错误信息
    public static final String MSG_INVALID_PARAMS = "请求参数不能为空";
    public static final String MSG_EMPTY_PRODUCT_LIST = "商品列表不能为空";
    public static final String MSG_PRODUCT_NOT_FOUND = "商品不存在或已被删除";
    public static final String MSG_PRODUCT_NOT_AVAILABLE = "商品当前不可购买（已下架、已售出等）";
    public static final String MSG_CANT_BUY_OWN_PRODUCT = "不能购买自己发布的商品";
    public static final String MSG_PRODUCT_STATUS_CHANGED = "商品状态已发生变化，请刷新后重试";
    public static final String MSG_CREATE_ORDER_FAILED = "创建订单失败";
    public static final String MSG_ORDER_NOT_FOUND = "订单不存在";
    public static final String MSG_UPDATE_ORDER_STATUS_FAILED = "更新订单状态失败";
    public static final String MSG_UPDATE_PRODUCT_STATUS_FAILED = "更新商品状态失败";
    public static final String MSG_SHIP_PERMISSION_DENIED = "无权限发货此订单，只有卖家可以发货";
    public static final String MSG_ORDER_STATUS_NOT_AWAITING_SHIPPING = "订单状态不正确，只有待发货状态的订单才能发货";
    public static final String MSG_SHIP_ORDER_FAILED = "发货失败，请稍后重试";
    public static final String MSG_CONFIRM_RECEIPT_PERMISSION_DENIED = "无权限确认收货此订单，只有买家可以确认收货";
    public static final String MSG_ORDER_STATUS_NOT_SHIPPED = "订单状态不正确，只有已发货状态的订单才能确认收货";
    public static final String MSG_CONFIRM_RECEIPT_FAILED = "确认收货失败，请稍后重试";
    public static final String MSG_UPDATE_PRODUCT_TO_SOLD_FAILED = "更新商品状态为已售失败";

    // 退货相关错误码
    public static final String RETURN_REQUEST_INVALID_REASON = "ORDER_301";
    public static final String RETURN_REQUEST_REASON_TOO_LONG = "ORDER_302";
    public static final String RETURN_REQUEST_ORDER_NOT_COMPLETED = "ORDER_303";
    public static final String RETURN_REQUEST_TIME_EXPIRED = "ORDER_304";
    public static final String RETURN_REQUEST_PERMISSION_DENIED = "ORDER_305";
    public static final String RETURN_REQUEST_ALREADY_APPLIED = "ORDER_306";
    public static final String RETURN_REQUEST_ORDER_ALREADY_REVIEWED = "ORDER_307";
    public static final String APPLY_RETURN_FAILED = "ORDER_308";

    // 退货相关错误信息
    public static final String MSG_RETURN_REQUEST_INVALID_REASON = "退货原因不能为空";
    public static final String MSG_RETURN_REQUEST_REASON_TOO_LONG = "退货原因不能超过500个字符";
    public static final String MSG_RETURN_REQUEST_ORDER_NOT_COMPLETED = "只有已完成的订单才能申请退货";
    public static final String MSG_RETURN_REQUEST_TIME_EXPIRED = "申请退货时间已过期，只能在订单完成后7天内申请";
    public static final String MSG_RETURN_REQUEST_PERMISSION_DENIED = "无权限申请退货此订单，只有买家可以申请退货";
    public static final String MSG_RETURN_REQUEST_ALREADY_APPLIED = "该订单已经申请过退货";
    public static final String MSG_RETURN_REQUEST_ORDER_ALREADY_REVIEWED = "已评价的订单不能申请退货";
    public static final String MSG_APPLY_RETURN_FAILED = "申请退货失败，请稍后重试";

    // 退货处理相关错误码
    public static final String PROCESS_RETURN_INVALID_DECISION = "ORDER_401";
    public static final String PROCESS_RETURN_MISSING_REJECT_REASON = "ORDER_402";
    public static final String PROCESS_RETURN_REJECT_REASON_TOO_LONG = "ORDER_403";
    public static final String PROCESS_RETURN_ORDER_NOT_RETURN_REQUESTED = "ORDER_404";
    public static final String PROCESS_RETURN_PERMISSION_DENIED = "ORDER_405";
    public static final String PROCESS_RETURN_FAILED = "ORDER_406";
    public static final String SIMULATE_REFUND_FAILED = "ORDER_407";

    // 退货处理相关错误信息
    public static final String MSG_PROCESS_RETURN_INVALID_DECISION = "处理决定不能为空";
    public static final String MSG_PROCESS_RETURN_MISSING_REJECT_REASON = "拒绝退货时必须填写拒绝原因";
    public static final String MSG_PROCESS_RETURN_REJECT_REASON_TOO_LONG = "拒绝原因不能超过500个字符";
    public static final String MSG_PROCESS_RETURN_ORDER_NOT_RETURN_REQUESTED = "只有申请退货状态的订单才能处理";
    public static final String MSG_PROCESS_RETURN_PERMISSION_DENIED = "无权限处理此退货申请，只有卖家可以处理";
    public static final String MSG_PROCESS_RETURN_FAILED = "处理退货申请失败，请稍后重试";
    public static final String MSG_SIMULATE_REFUND_FAILED = "模拟退款操作失败";

    public static final String MSG_SYSTEM_ERROR = "系统错误，请稍后重试";
}
