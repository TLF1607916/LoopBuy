package com.shiwu.review.model;

/**
 * 评价相关错误码常量
 */
public class ReviewErrorCode {
    
    // 参数错误
    public static final String INVALID_PARAMS = "REVIEW_001";
    public static final String INVALID_ORDER_ID = "REVIEW_002";
    public static final String INVALID_RATING = "REVIEW_003";
    public static final String COMMENT_TOO_LONG = "REVIEW_004";
    
    // 订单相关错误
    public static final String ORDER_NOT_FOUND = "REVIEW_101";
    public static final String ORDER_NOT_COMPLETED = "REVIEW_102";
    public static final String ORDER_ALREADY_REVIEWED = "REVIEW_103";
    public static final String NOT_ORDER_BUYER = "REVIEW_104";
    public static final String ORDER_STATUS_RETURNED = "REVIEW_105";
    
    // 评价操作错误
    public static final String CREATE_REVIEW_FAILED = "REVIEW_201";
    public static final String REVIEW_NOT_FOUND = "REVIEW_202";
    public static final String UPDATE_SELLER_RATING_FAILED = "REVIEW_203";
    
    // 系统错误
    public static final String SYSTEM_ERROR = "REVIEW_500";
    
    // 错误信息
    public static final String MSG_INVALID_PARAMS = "请求参数不能为空";
    public static final String MSG_INVALID_ORDER_ID = "订单ID不能为空";
    public static final String MSG_INVALID_RATING = "评分必须在1-5星之间";
    public static final String MSG_COMMENT_TOO_LONG = "评价内容不能超过500个字符";
    public static final String MSG_ORDER_NOT_FOUND = "订单不存在或已被删除";
    public static final String MSG_ORDER_NOT_COMPLETED = "只有已完成的订单才能评价";
    public static final String MSG_ORDER_ALREADY_REVIEWED = "该订单已经评价过了";
    public static final String MSG_NOT_ORDER_BUYER = "只有买家可以评价订单";
    public static final String MSG_ORDER_STATUS_RETURNED = "已退货的订单不能评价";
    public static final String MSG_CREATE_REVIEW_FAILED = "提交评价失败，请稍后重试";
    public static final String MSG_REVIEW_NOT_FOUND = "评价不存在";
    public static final String MSG_UPDATE_SELLER_RATING_FAILED = "更新卖家评分失败";
    public static final String MSG_SYSTEM_ERROR = "系统错误，请稍后重试";
}
