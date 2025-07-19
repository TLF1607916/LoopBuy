package com.shiwu.cart.demo;

import com.shiwu.cart.model.*;
import com.shiwu.cart.service.CartService;
import com.shiwu.cart.service.impl.CartServiceImpl;

/**
 * 购物车API使用演示
 * 展示如何正确使用购物车功能和处理各种错误情况
 */
public class CartApiDemo {
    
    private final CartService cartService;
    
    public CartApiDemo() {
        this.cartService = new CartServiceImpl();
    }
    
    /**
     * 演示添加商品到购物车的完整流程
     */
    public void demonstrateAddToCart() {
        System.out.println("=== 购物车添加商品演示 ===");
        
        Long userId = 1L;
        Long productId = 2L;
        
        // 创建添加购物车请求
        CartAddDTO dto = new CartAddDTO(productId, 1);
        
        // 调用服务
        CartOperationResult result = cartService.addToCart(dto, userId);
        
        // 处理结果
        if (result.isSuccess()) {
            System.out.println("✅ 添加商品到购物车成功！");
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> data = (java.util.Map<String, Object>) result.getData();
            System.out.println("购物车总商品数: " + data.get("totalItems"));
        } else {
            System.out.println("❌ 添加商品到购物车失败！");
            System.out.println("错误码: " + result.getErrorCode());
            System.out.println("错误信息: " + result.getErrorMessage());
            
            // 根据错误码进行相应处理
            handleAddToCartError(result.getErrorCode());
        }
    }
    
    /**
     * 处理添加到购物车的各种错误情况
     */
    private void handleAddToCartError(String errorCode) {
        switch (errorCode) {
            case CartErrorCode.INVALID_PARAMS:
                System.out.println("💡 建议: 请检查请求参数是否完整");
                break;
            case CartErrorCode.INVALID_QUANTITY:
                System.out.println("💡 建议: 商品数量必须大于0");
                break;
            case CartErrorCode.PRODUCT_NOT_FOUND:
                System.out.println("💡 建议: 请确认商品是否存在");
                break;
            case CartErrorCode.PRODUCT_NOT_AVAILABLE:
                System.out.println("💡 建议: 该商品当前不可购买，可能已下架或售完");
                break;
            case CartErrorCode.CANT_BUY_OWN_PRODUCT:
                System.out.println("💡 建议: 不能购买自己发布的商品");
                break;
            case CartErrorCode.PRODUCT_ALREADY_IN_CART:
                System.out.println("💡 建议: 商品已在购物车中，请直接前往购物车查看");
                break;
            case CartErrorCode.ADD_TO_CART_FAILED:
                System.out.println("💡 建议: 系统繁忙，请稍后重试");
                break;
            default:
                System.out.println("💡 建议: 未知错误，请联系客服");
                break;
        }
    }
    
    /**
     * 演示查看购物车功能
     */
    public void demonstrateViewCart() {
        System.out.println("\n=== 查看购物车演示 ===");
        
        Long userId = 1L;
        
        // 获取购物车
        CartOperationResult cartResult = cartService.getCart(userId);

        if (cartResult.isSuccess()) {
            System.out.println("✅ 获取购物车成功！");
            CartVO cart = (CartVO) cartResult.getData();
            System.out.println("商品总数: " + cart.getTotalItems());
            System.out.println("总价格: ¥" + cart.getTotalPrice());

            if (cart.getItems() != null && !cart.getItems().isEmpty()) {
                System.out.println("\n购物车商品列表:");
                for (int i = 0; i < cart.getItems().size(); i++) {
                    CartItemVO item = cart.getItems().get(i);
                    System.out.println((i + 1) + ". " + formatCartItem(item));
                }
            } else {
                System.out.println("购物车为空");
            }
        } else {
            System.out.println("❌ 获取购物车失败！");
            System.out.println("错误码: " + cartResult.getErrorCode());
            System.out.println("错误信息: " + cartResult.getErrorMessage());
        }
    }
    
    /**
     * 格式化购物车项显示
     */
    private String formatCartItem(CartItemVO item) {
        StringBuilder sb = new StringBuilder();
        
        if (item.getProduct() != null) {
            sb.append(item.getProduct().getTitle());
            sb.append(" - ¥").append(item.getProduct().getPrice());
        } else {
            sb.append("商品信息缺失");
        }
        
        sb.append(" x").append(item.getQuantity());
        
        if (item.getSellerName() != null) {
            sb.append(" (卖家: ").append(item.getSellerName()).append(")");
        }
        
        if (!item.getAvailable()) {
            sb.append(" [商品不可用]");
        }
        
        return sb.toString();
    }
    
    /**
     * 演示移除商品功能
     */
    public void demonstrateRemoveFromCart() {
        System.out.println("\n=== 移除购物车商品演示 ===");
        
        Long userId = 1L;
        Long productId = 2L;
        
        CartOperationResult result = cartService.removeFromCart(productId, userId);

        if (result.isSuccess()) {
            System.out.println("✅ 移除商品成功！");
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> data = (java.util.Map<String, Object>) result.getData();
            System.out.println("购物车剩余商品数: " + data.get("totalItems"));
        } else {
            System.out.println("❌ 移除商品失败！");
            System.out.println("错误码: " + result.getErrorCode());
            System.out.println("错误信息: " + result.getErrorMessage());
        }
    }
    
    /**
     * 演示清空购物车功能
     */
    public void demonstrateClearCart() {
        System.out.println("\n=== 清空购物车演示 ===");
        
        Long userId = 1L;
        
        CartOperationResult result = cartService.clearCart(userId);

        if (result.isSuccess()) {
            System.out.println("✅ 清空购物车成功！");
        } else {
            System.out.println("❌ 清空购物车失败！");
            System.out.println("错误码: " + result.getErrorCode());
            System.out.println("错误信息: " + result.getErrorMessage());
        }
    }
    
    /**
     * 运行完整演示
     */
    public void runFullDemo() {
        System.out.println("🛒 购物车功能完整演示开始");
        System.out.println("=====================================");
        
        demonstrateAddToCart();
        demonstrateViewCart();
        demonstrateRemoveFromCart();
        demonstrateClearCart();
        
        System.out.println("\n=====================================");
        System.out.println("🎉 购物车功能演示完成");
    }
    
    /**
     * 主方法，用于运行演示
     */
    public static void main(String[] args) {
        CartApiDemo demo = new CartApiDemo();
        demo.runFullDemo();
    }
}
