package com.shiwu.cart.model;

/**
 * 购物车相关错误码常量
 */
public class CartErrorCode {
    
    // 参数错误
    public static final String INVALID_PARAMS = "CART_001";
    public static final String INVALID_QUANTITY = "CART_002";
    
    // 商品相关错误
    public static final String PRODUCT_NOT_FOUND = "CART_101";
    public static final String PRODUCT_NOT_AVAILABLE = "CART_102";
    public static final String CANT_BUY_OWN_PRODUCT = "CART_103";
    public static final String PRODUCT_ALREADY_IN_CART = "CART_104";
    
    // 购物车操作错误
    public static final String CART_ITEM_NOT_FOUND = "CART_201";
    public static final String ADD_TO_CART_FAILED = "CART_202";
    public static final String REMOVE_FROM_CART_FAILED = "CART_203";
    
    // 系统错误
    public static final String SYSTEM_ERROR = "CART_500";
    
    // 错误信息
    public static final String MSG_INVALID_PARAMS = "请求参数不能为空";
    public static final String MSG_INVALID_QUANTITY = "商品数量必须大于0";
    public static final String MSG_PRODUCT_NOT_FOUND = "商品不存在或已被删除";
    public static final String MSG_PRODUCT_NOT_AVAILABLE = "商品当前不可购买（已下架、已售出等）";
    public static final String MSG_CANT_BUY_OWN_PRODUCT = "不能购买自己发布的商品";
    public static final String MSG_PRODUCT_ALREADY_IN_CART = "商品已在购物车中";
    public static final String MSG_CART_ITEM_NOT_FOUND = "购物车中不存在该商品";
    public static final String MSG_ADD_TO_CART_FAILED = "添加商品到购物车失败";
    public static final String MSG_REMOVE_FROM_CART_FAILED = "从购物车移除商品失败";
    public static final String MSG_SYSTEM_ERROR = "系统错误，请稍后重试";
}
