package com.shiwu.product.service;

import com.shiwu.admin.model.AdminProductQueryDTO;

import java.util.Map;

/**
 * 管理员商品服务接口
 */
public interface AdminProductService {
    
    /**
     * 查询商品列表（管理员视角）
     * @param queryDTO 查询条件
     * @return 分页查询结果
     */
    Map<String, Object> findProducts(AdminProductQueryDTO queryDTO);
    
    /**
     * 获取商品详情（管理员视角）
     * @param productId 商品ID
     * @param adminId 管理员ID
     * @return 商品详情
     */
    Map<String, Object> getProductDetail(Long productId, Long adminId);
    
    /**
     * 审核通过商品
     * @param productId 商品ID
     * @param adminId 管理员ID
     * @param reason 审核备注
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     * @return 操作是否成功
     */
    boolean approveProduct(Long productId, Long adminId, String reason, String ipAddress, String userAgent);

    /**
     * 审核拒绝商品
     * @param productId 商品ID
     * @param adminId 管理员ID
     * @param reason 拒绝原因
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     * @return 操作是否成功
     */
    boolean rejectProduct(Long productId, Long adminId, String reason, String ipAddress, String userAgent);

    /**
     * 下架商品
     * @param productId 商品ID
     * @param adminId 管理员ID
     * @param reason 下架原因
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     * @return 操作是否成功
     */
    boolean delistProduct(Long productId, Long adminId, String reason, String ipAddress, String userAgent);

    /**
     * 删除商品（软删除）
     * @param productId 商品ID
     * @param adminId 管理员ID
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     * @return 操作是否成功
     */
    boolean deleteProduct(Long productId, Long adminId, String ipAddress, String userAgent);
}
