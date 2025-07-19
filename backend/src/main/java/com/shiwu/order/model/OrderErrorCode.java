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
    public static final String MSG_SYSTEM_ERROR = "系统错误，请稍后重试";
}
