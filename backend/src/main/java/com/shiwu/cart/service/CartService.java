package com.shiwu.cart.service;

import com.shiwu.cart.model.CartAddDTO;
import com.shiwu.cart.model.CartOperationResult;
//import com.shiwu.cart.model.CartVO;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {

    /**
     * 添加商品到购物车
     * @param dto 添加购物车数据传输对象
     * @param userId 用户ID
     * @return 购物车操作结果
     */
    CartOperationResult addToCart(CartAddDTO dto, Long userId);
    
    /**
     * 获取用户购物车
     * @param userId 用户ID
     * @return 购物车操作结果
     */
    CartOperationResult getCart(Long userId);
    
    /**
     * 从购物车中移除商品
     * @param productId 商品ID
     * @param userId 用户ID
     * @return 购物车操作结果
     */
    CartOperationResult removeFromCart(Long productId, Long userId);

    /**
     * 批量从购物车中移除商品
     * @param productIds 商品ID列表
     * @param userId 用户ID
     * @return 购物车操作结果
     */
    CartOperationResult batchRemoveFromCart(List<Long> productIds, Long userId);

    /**
     * 清空购物车
     * @param userId 用户ID
     * @return 购物车操作结果
     */
    CartOperationResult clearCart(Long userId);
    
    /**
     * 获取购物车中商品总数
     * @param userId 用户ID
     * @return 商品总数
     */
    int getCartItemCount(Long userId);
}
